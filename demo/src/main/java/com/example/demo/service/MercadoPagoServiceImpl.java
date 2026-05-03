package com.example.demo.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.config.MercadoPagoConfig;
import com.example.demo.dto.MpItemRequest;
import com.example.demo.dto.MpPreferenceRequest;
import com.example.demo.entitys.LineaPedido;
import com.example.demo.entitys.LineaPedidoAdicional;
import com.example.demo.entitys.Pedido;
import com.example.demo.repository.PedidoRepository;
import com.mercadopago.client.payment.PaymentClient;
import com.mercadopago.client.preference.PreferenceBackUrlsRequest;
import com.mercadopago.client.preference.PreferenceClient;
import com.mercadopago.client.preference.PreferenceItemRequest;
import com.mercadopago.client.preference.PreferenceRequest;
import com.mercadopago.exceptions.MPApiException;
import com.mercadopago.exceptions.MPException;
import com.mercadopago.resources.payment.Payment;
import com.mercadopago.resources.preference.Preference;

@Service
public class MercadoPagoServiceImpl implements MercadoPagoService {

    private static final Logger log = LoggerFactory.getLogger(MercadoPagoServiceImpl.class);
    private static final String CURRENCY = "COP";
    private static final String STATEMENT_DESCRIPTOR = "LA VUELTA RAPIDA";

    @Autowired
    private PedidoRepository pedidoRepository;

    @Autowired
    private MercadoPagoConfig mpConfig;

    @Autowired
    private PreferenceClient preferenceClient;

    @Autowired
    private PaymentClient paymentClient;

    @Override
    @Transactional
    public Map<String, Object> crearPreferencia(MpPreferenceRequest req) throws MPException, MPApiException {
        if (req == null || req.getPedidoId() == null) {
            throw new IllegalArgumentException("Falta el campo 'pedidoId'.");
        }
        if (req.getOrigin() == null || req.getOrigin().isBlank()) {
            throw new IllegalArgumentException("Falta el campo 'origin'.");
        }

        Pedido pedido = pedidoRepository.findById(req.getPedidoId())
                .orElseThrow(() -> new NoSuchElementException("Pedido no encontrado."));

        BigDecimal totalReal = calcularTotalDesdeBd(pedido);

        List<PreferenceItemRequest> items = construirItems(req, pedido, totalReal);

        String backUrl = req.getOrigin().replaceAll("/+$", "")
                + "/pago/resultado/" + pedido.getId();

        PreferenceBackUrlsRequest backUrls = PreferenceBackUrlsRequest.builder()
                .success(backUrl)
                .failure(backUrl)
                .pending(backUrl)
                .build();

        PreferenceRequest.PreferenceRequestBuilder builder = PreferenceRequest.builder()
                .items(items)
                .externalReference(String.valueOf(pedido.getId()))
                .statementDescriptor(STATEMENT_DESCRIPTOR)
                .backUrls(backUrls);

        // Nota: NO enviamos el bloque payer al crear la preferencia.
        // En modo de prueba (TEST credentials) MP rechaza la tokenización
        // de tarjetas si el email pertenece a una cuenta MP real, y en
        // producción dejamos que MP recolecte los datos del comprador en
        // su propia pantalla de checkout.

        String backendUrl = mpConfig.getBackendUrl();
        if (backendUrl != null && !backendUrl.isBlank()) {
            builder.notificationUrl(backendUrl.replaceAll("/+$", "") + "/api/mp/webhook");
        }

        Preference preference = preferenceClient.create(builder.build());

        pedido.setMpPreferenceId(preference.getId());
        pedidoRepository.save(pedido);

        Map<String, Object> resp = new LinkedHashMap<>();
        resp.put("id", preference.getId());
        resp.put("init_point", preference.getInitPoint());
        resp.put("sandbox_init_point", preference.getSandboxInitPoint());
        return resp;
    }

    @Override
    public Map<String, Object> consultarPago(String paymentId) throws MPException, MPApiException {
        if (paymentId == null || paymentId.isBlank()) {
            throw new IllegalArgumentException("Falta el paymentId.");
        }
        Long id;
        try {
            id = Long.parseLong(paymentId);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("paymentId inválido: " + paymentId);
        }

        Payment payment = paymentClient.get(id);
        return mapPaymentToResponse(payment);
    }

    @Override
    @Transactional
    public void procesarWebhook(String xSignature,
                                String xRequestId,
                                String dataIdQuery,
                                String typeQuery,
                                Map<String, Object> body) {
        try {
            String dataId = extraerDataId(dataIdQuery, body);
            String type = extraerType(typeQuery, body);

            if (dataId == null || dataId.isBlank()) {
                log.warn("Webhook MP sin data.id, se ignora.");
                return;
            }

            if (!validarFirma(xSignature, xRequestId, dataId)) {
                log.warn("Webhook MP con firma inválida (data.id={}). Se ignora la actualización.", dataId);
                return;
            }

            if (type != null && !"payment".equalsIgnoreCase(type)) {
                log.info("Webhook MP de tipo '{}' ignorado.", type);
                return;
            }

            Long paymentIdLong;
            try {
                paymentIdLong = Long.parseLong(dataId);
            } catch (NumberFormatException e) {
                log.warn("data.id no numérico en webhook MP: {}", dataId);
                return;
            }

            Payment payment = paymentClient.get(paymentIdLong);
            if (payment == null || payment.getExternalReference() == null) {
                log.warn("Webhook MP: pago {} sin external_reference.", dataId);
                return;
            }

            Long pedidoId;
            try {
                pedidoId = Long.parseLong(payment.getExternalReference());
            } catch (NumberFormatException e) {
                log.warn("external_reference no numérico: {}", payment.getExternalReference());
                return;
            }

            Pedido pedido = pedidoRepository.findById(pedidoId).orElse(null);
            if (pedido == null) {
                log.warn("Webhook MP: pedido {} no encontrado.", pedidoId);
                return;
            }

            String estadoMapeado = mapearEstado(payment.getStatus());
            pedido.setEstadoPago(estadoMapeado);
            pedido.setMpPaymentId(String.valueOf(payment.getId()));
            pedido.setMpPaymentMethod(payment.getPaymentMethodId());
            pedido.setMpPaymentType(payment.getPaymentTypeId());
            pedido.setTotalPagado(payment.getTransactionAmount());
            if ("APROBADO".equals(estadoMapeado) && payment.getDateApproved() != null) {
                pedido.setFechaPago(payment.getDateApproved()
                        .atZoneSameInstant(ZoneId.systemDefault())
                        .toLocalDateTime());
            }
            pedidoRepository.save(pedido);
            log.info("Webhook MP procesado: pedido {} → estadoPago={}", pedidoId, estadoMapeado);
        } catch (Exception e) {
            // Nunca propagar: MP reintenta al recibir error.
            log.error("Error procesando webhook MP: {}", e.getMessage(), e);
        }
    }

    // ───────────────────────── helpers ─────────────────────────

    private BigDecimal calcularTotalDesdeBd(Pedido pedido) {
        BigDecimal total = BigDecimal.ZERO;
        if (pedido.getLineasPedido() == null) {
            return total.setScale(2, RoundingMode.HALF_UP);
        }
        for (LineaPedido lp : pedido.getLineasPedido()) {
            int cantidad = lp.getCantidad() == null ? 0 : lp.getCantidad();
            double precioBase = lp.getComida() != null ? lp.getComida().getPrice() : 0.0;
            double precioAdic = 0.0;
            if (lp.getAdicionales() != null) {
                for (LineaPedidoAdicional a : lp.getAdicionales()) {
                    if (a.getAdicional() != null) {
                        precioAdic += a.getAdicional().getPrice();
                    }
                }
            }
            BigDecimal subtotal = BigDecimal.valueOf(precioBase + precioAdic)
                    .multiply(BigDecimal.valueOf(cantidad));
            total = total.add(subtotal);
        }
        return total.setScale(2, RoundingMode.HALF_UP);
    }

    private List<PreferenceItemRequest> construirItems(MpPreferenceRequest req,
                                                       Pedido pedido,
                                                       BigDecimal totalReal) {
        List<PreferenceItemRequest> items = new ArrayList<>();
        boolean usarRequest = req.getItems() != null && !req.getItems().isEmpty();

        if (usarRequest) {
            BigDecimal sumaRequest = BigDecimal.ZERO;
            for (MpItemRequest it : req.getItems()) {
                int qty = it.getQuantity() == null ? 0 : it.getQuantity();
                BigDecimal unit = it.getUnitPrice() == null ? BigDecimal.ZERO : it.getUnitPrice();
                sumaRequest = sumaRequest.add(unit.multiply(BigDecimal.valueOf(qty)));
            }
            sumaRequest = sumaRequest.setScale(2, RoundingMode.HALF_UP);

            if (sumaRequest.compareTo(totalReal) == 0) {
                for (MpItemRequest it : req.getItems()) {
                    items.add(PreferenceItemRequest.builder()
                            .id(it.getId())
                            .title(it.getTitle() != null ? it.getTitle() : "Item")
                            .quantity(it.getQuantity() != null ? it.getQuantity() : 1)
                            .unitPrice(it.getUnitPrice() != null ? it.getUnitPrice() : BigDecimal.ZERO)
                            .currencyId(CURRENCY)
                            .build());
                }
                return items;
            }
            log.warn("Total enviado por el frontend ({}) no coincide con el total real ({}); " +
                    "se usará un único item con el total real.", sumaRequest, totalReal);
        }

        items.add(PreferenceItemRequest.builder()
                .id(String.valueOf(pedido.getId()))
                .title("Pedido La Vuelta Rápida #" + pedido.getId())
                .quantity(1)
                .unitPrice(totalReal)
                .currencyId(CURRENCY)
                .build());
        return items;
    }

    private Map<String, Object> mapPaymentToResponse(Payment payment) {
        Map<String, Object> resp = new LinkedHashMap<>();
        resp.put("id", payment.getId());
        resp.put("status", payment.getStatus());
        resp.put("status_detail", payment.getStatusDetail());
        resp.put("payment_method_id", payment.getPaymentMethodId());
        resp.put("payment_type_id", payment.getPaymentTypeId());
        resp.put("transaction_amount", payment.getTransactionAmount());
        resp.put("currency_id", payment.getCurrencyId());
        resp.put("external_reference", payment.getExternalReference());
        OffsetDateTime approved = payment.getDateApproved();
        resp.put("date_approved", approved != null ? approved.toString() : null);
        Map<String, Object> payer = new HashMap<>();
        if (payment.getPayer() != null) {
            payer.put("email", payment.getPayer().getEmail());
        } else {
            payer.put("email", null);
        }
        resp.put("payer", payer);
        return resp;
    }

    private String mapearEstado(String status) {
        if (status == null) return "PENDIENTE";
        switch (status.toLowerCase()) {
            case "approved":
                return "APROBADO";
            case "pending":
            case "in_process":
            case "authorized":
                return "EN_PROCESO";
            case "rejected":
            case "cancelled":
            case "refunded":
            case "charged_back":
                return "RECHAZADO";
            default:
                return "PENDIENTE";
        }
    }

    private String extraerDataId(String fromQuery, Map<String, Object> body) {
        if (fromQuery != null && !fromQuery.isBlank()) return fromQuery;
        if (body != null) {
            Object data = body.get("data");
            if (data instanceof Map) {
                Object id = ((Map<?, ?>) data).get("id");
                if (id != null) return String.valueOf(id);
            }
        }
        return null;
    }

    private String extraerType(String fromQuery, Map<String, Object> body) {
        if (fromQuery != null && !fromQuery.isBlank()) return fromQuery;
        if (body != null) {
            Object type = body.get("type");
            if (type != null) return String.valueOf(type);
        }
        return null;
    }

    private boolean validarFirma(String xSignature, String xRequestId, String dataId) {
        String secret = mpConfig.getWebhookSecret();
        if (secret == null || secret.isBlank()) {
            log.warn("MERCADOPAGO_WEBHOOK_SECRET no configurado: validación de firma omitida.");
            return true;
        }
        if (xSignature == null || xSignature.isBlank()) {
            return false;
        }
        String ts = null;
        String v1 = null;
        for (String part : xSignature.split(",")) {
            String[] kv = part.trim().split("=", 2);
            if (kv.length != 2) continue;
            String k = kv[0].trim();
            String v = kv[1].trim();
            if ("ts".equals(k)) ts = v;
            else if ("v1".equals(k)) v1 = v;
        }
        if (ts == null || v1 == null) return false;

        String requestId = xRequestId == null ? "" : xRequestId;
        String manifest = "id:" + dataId + ";request-id:" + requestId + ";ts:" + ts + ";";

        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
            byte[] hash = mac.doFinal(manifest.getBytes(StandardCharsets.UTF_8));
            StringBuilder hex = new StringBuilder();
            for (byte b : hash) hex.append(String.format("%02x", b));
            return constantTimeEquals(hex.toString(), v1);
        } catch (Exception e) {
            log.error("Error calculando HMAC del webhook MP: {}", e.getMessage());
            return false;
        }
    }

    private boolean constantTimeEquals(String a, String b) {
        if (a == null || b == null || a.length() != b.length()) return false;
        int result = 0;
        for (int i = 0; i < a.length(); i++) {
            result |= a.charAt(i) ^ b.charAt(i);
        }
        return result == 0;
    }

    // package-private para tests
    LocalDateTime ahora() {
        return LocalDateTime.now();
    }
}
