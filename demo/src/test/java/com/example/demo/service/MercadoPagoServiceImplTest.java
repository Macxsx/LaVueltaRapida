package com.example.demo.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.demo.config.MercadoPagoConfig;
import com.example.demo.dto.MpItemRequest;
import com.example.demo.dto.MpPreferenceRequest;
import com.example.demo.entitys.Adicional;
import com.example.demo.entitys.Comida;
import com.example.demo.entitys.LineaPedido;
import com.example.demo.entitys.LineaPedidoAdicional;
import com.example.demo.entitys.Pedido;
import com.example.demo.repository.PedidoRepository;
import com.mercadopago.client.payment.PaymentClient;
import com.mercadopago.client.preference.PreferenceClient;
import com.mercadopago.client.preference.PreferenceItemRequest;
import com.mercadopago.client.preference.PreferenceRequest;
import com.mercadopago.resources.payment.Payment;
import com.mercadopago.resources.payment.PaymentPayer;
import com.mercadopago.resources.preference.Preference;

@ExtendWith(MockitoExtension.class)
public class MercadoPagoServiceImplTest {

    @Mock private PedidoRepository pedidoRepository;
    @Mock private MercadoPagoConfig mpConfig;
    @Mock private PreferenceClient preferenceClient;
    @Mock private PaymentClient paymentClient;

    @InjectMocks private MercadoPagoServiceImpl service;

    private Pedido pedido;

    @BeforeEach
    void setUp() {
        pedido = buildPedidoConLineas();
    }

    private Pedido buildPedidoConLineas() {
        Comida pizza = new Comida();
        pizza.setName("Pizza Monza");
        pizza.setPrice(18000.0);

        Adicional queso = new Adicional();
        queso.setName("Queso extra");
        queso.setPrice(2000.0);

        LineaPedido linea = new LineaPedido();
        linea.setComida(pizza);
        linea.setCantidad(2);

        LineaPedidoAdicional lpa = new LineaPedidoAdicional();
        lpa.setAdicional(queso);
        lpa.setLineaPedido(linea);
        linea.getAdicionales().add(lpa);

        Pedido p = new Pedido();
        p.setId(123L);
        p.setLineasPedido(new ArrayList<>(List.of(linea)));
        return p;
        // Total esperado: (18000 + 2000) * 2 = 40000.00
    }

    private MpPreferenceRequest buildReq(List<MpItemRequest> items) {
        MpPreferenceRequest req = new MpPreferenceRequest();
        req.setPedidoId(123L);
        req.setOrigin("https://mi-app.com");
        req.setItems(items);
        return req;
    }

    // ───────── crearPreferencia ─────────

    @Test
    void crearPreferencia_pedidoNoExiste_lanzaNoSuchElement() {
        when(pedidoRepository.findById(123L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.crearPreferencia(buildReq(List.of())))
                .isInstanceOf(NoSuchElementException.class);
    }

    @Test
    void crearPreferencia_sinPedidoId_lanzaIllegalArgument() {
        MpPreferenceRequest req = new MpPreferenceRequest();
        req.setOrigin("x");
        assertThatThrownBy(() -> service.crearPreferencia(req))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void crearPreferencia_sinOriginYSinFrontendUrl_lanzaIllegalArgument() {
        when(mpConfig.getFrontendUrl()).thenReturn("");
        MpPreferenceRequest req = new MpPreferenceRequest();
        req.setPedidoId(123L);
        assertThatThrownBy(() -> service.crearPreferencia(req))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("frontend-url");
    }

    @Test
    void crearPreferencia_sinOrigin_usaFrontendUrlComoFallback() throws Exception {
        when(pedidoRepository.findById(123L)).thenReturn(Optional.of(pedido));
        when(mpConfig.getBackendUrl()).thenReturn("");
        when(mpConfig.getFrontendUrl()).thenReturn("http://localhost:5000");
        when(preferenceClient.create(any(PreferenceRequest.class)))
                .thenReturn(mockPreference("P", "i", "s"));

        MpPreferenceRequest req = new MpPreferenceRequest();
        req.setPedidoId(123L);
        // origin intencionalmente NO seteado

        service.crearPreferencia(req);

        ArgumentCaptor<PreferenceRequest> captor = ArgumentCaptor.forClass(PreferenceRequest.class);
        verify(preferenceClient).create(captor.capture());
        PreferenceRequest sent = captor.getValue();
        assertThat(sent.getBackUrls()).isNotNull();
        assertThat(sent.getBackUrls().getSuccess()).isEqualTo("http://localhost:5000/pago/resultado/123");
        assertThat(sent.getBackUrls().getFailure()).isEqualTo("http://localhost:5000/pago/resultado/123");
        assertThat(sent.getBackUrls().getPending()).isEqualTo("http://localhost:5000/pago/resultado/123");
    }

    @Test
    void crearPreferencia_recalculaTotalDesdeBdYUsaItemsRequestSiCuadran() throws Exception {
        when(pedidoRepository.findById(123L)).thenReturn(Optional.of(pedido));
        when(mpConfig.getBackendUrl()).thenReturn("");
        Preference fake = mockPreference("PREF-1", "https://init", "https://sandbox");
        when(preferenceClient.create(any(PreferenceRequest.class))).thenReturn(fake);

        // Items que SÍ cuadran con el total real (40000): 2 x 20000
        MpItemRequest it = new MpItemRequest();
        it.setId("5"); it.setTitle("Pizza Monza"); it.setQuantity(2);
        it.setUnitPrice(new BigDecimal("20000"));

        Map<String, Object> resp = service.crearPreferencia(buildReq(List.of(it)));

        ArgumentCaptor<PreferenceRequest> captor = ArgumentCaptor.forClass(PreferenceRequest.class);
        verify(preferenceClient).create(captor.capture());
        PreferenceRequest sent = captor.getValue();

        assertThat(sent.getExternalReference()).isEqualTo("123");
        assertThat(sent.getStatementDescriptor()).isEqualTo("LA VUELTA RAPIDA");
        assertThat(sent.getAutoReturn()).isNull();
        assertThat(sent.getBackUrls().getSuccess()).isEqualTo("https://mi-app.com/pago/resultado/123");
        assertThat(sent.getBackUrls().getFailure()).isEqualTo("https://mi-app.com/pago/resultado/123");
        assertThat(sent.getBackUrls().getPending()).isEqualTo("https://mi-app.com/pago/resultado/123");
        assertThat(sent.getItems()).hasSize(1);
        PreferenceItemRequest item = sent.getItems().get(0);
        assertThat(item.getCurrencyId()).isEqualTo("COP");
        assertThat(item.getQuantity()).isEqualTo(2);
        assertThat(item.getUnitPrice()).isEqualByComparingTo("20000");

        assertThat(resp.get("id")).isEqualTo("PREF-1");
        assertThat(resp.get("init_point")).isEqualTo("https://init");
        assertThat(resp.get("sandbox_init_point")).isEqualTo("https://sandbox");
        assertThat(pedido.getMpPreferenceId()).isEqualTo("PREF-1");
        verify(pedidoRepository).save(pedido);
    }

    @Test
    void crearPreferencia_itemsNoCuadran_usaFallbackUnItemConTotalReal() throws Exception {
        when(pedidoRepository.findById(123L)).thenReturn(Optional.of(pedido));
        when(mpConfig.getBackendUrl()).thenReturn("");
        when(preferenceClient.create(any(PreferenceRequest.class)))
                .thenReturn(mockPreference("P", "i", "s"));

        // Items maliciosos: cliente intenta cobrar solo $1 (no debería poder)
        MpItemRequest it = new MpItemRequest();
        it.setId("X"); it.setTitle("Pizza barata"); it.setQuantity(1);
        it.setUnitPrice(new BigDecimal("1"));

        service.crearPreferencia(buildReq(List.of(it)));

        ArgumentCaptor<PreferenceRequest> captor = ArgumentCaptor.forClass(PreferenceRequest.class);
        verify(preferenceClient).create(captor.capture());
        PreferenceRequest sent = captor.getValue();
        assertThat(sent.getItems()).hasSize(1);
        PreferenceItemRequest fallback = sent.getItems().get(0);
        assertThat(fallback.getTitle()).isEqualTo("Pedido La Vuelta Rápida #123");
        assertThat(fallback.getQuantity()).isEqualTo(1);
        assertThat(fallback.getUnitPrice()).isEqualByComparingTo("40000.00");
        assertThat(fallback.getCurrencyId()).isEqualTo("COP");
    }

    @Test
    void crearPreferencia_itemsVacios_usaFallbackConTotalRealYAgregaNotificationUrl() throws Exception {
        when(pedidoRepository.findById(123L)).thenReturn(Optional.of(pedido));
        when(mpConfig.getBackendUrl()).thenReturn("https://mi-back.com/");
        when(preferenceClient.create(any(PreferenceRequest.class)))
                .thenReturn(mockPreference("P", "i", "s"));

        service.crearPreferencia(buildReq(List.of()));

        ArgumentCaptor<PreferenceRequest> captor = ArgumentCaptor.forClass(PreferenceRequest.class);
        verify(preferenceClient).create(captor.capture());
        PreferenceRequest sent = captor.getValue();
        assertThat(sent.getNotificationUrl()).isEqualTo("https://mi-back.com/api/mp/webhook");
        assertThat(sent.getItems().get(0).getUnitPrice()).isEqualByComparingTo("40000.00");
    }

    @Test
    void crearPreferencia_nuncaEnviaPayerAlSDK() throws Exception {
        when(pedidoRepository.findById(123L)).thenReturn(Optional.of(pedido));
        when(mpConfig.getBackendUrl()).thenReturn("");
        when(preferenceClient.create(any(PreferenceRequest.class)))
                .thenReturn(mockPreference("P", "i", "s"));

        service.crearPreferencia(buildReq(List.of()));

        ArgumentCaptor<PreferenceRequest> captor = ArgumentCaptor.forClass(PreferenceRequest.class);
        verify(preferenceClient).create(captor.capture());
        PreferenceRequest sent = captor.getValue();
        assertThat(sent.getPayer()).isNull();
    }

    // ───────── consultarPago ─────────

    @Test
    void consultarPago_idInvalido_lanzaIllegalArgument() {
        assertThatThrownBy(() -> service.consultarPago("abc"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void consultarPago_devuelveCamposEsperados() throws Exception {
        Payment p = mockPayment(123456789L, "approved", "accredited",
                "visa", "credit_card", new BigDecimal("45000"), "COP", "123",
                OffsetDateTime.parse("2025-01-15T10:30:00-05:00"), "juan@example.com");
        when(paymentClient.get(123456789L)).thenReturn(p);

        Map<String, Object> resp = service.consultarPago("123456789");

        assertThat(resp.get("id")).isEqualTo(123456789L);
        assertThat(resp.get("status")).isEqualTo("approved");
        assertThat(resp.get("status_detail")).isEqualTo("accredited");
        assertThat(resp.get("payment_method_id")).isEqualTo("visa");
        assertThat(resp.get("payment_type_id")).isEqualTo("credit_card");
        assertThat(resp.get("currency_id")).isEqualTo("COP");
        assertThat(resp.get("external_reference")).isEqualTo("123");
        assertThat(resp.get("date_approved")).isEqualTo("2025-01-15T10:30-05:00");
        @SuppressWarnings("unchecked")
        Map<String, Object> payer = (Map<String, Object>) resp.get("payer");
        assertThat(payer.get("email")).isEqualTo("juan@example.com");
    }

    // ───────── procesarWebhook ─────────

    @Test
    void webhook_secretBlanco_omiteFirmaYActualizaPedido() throws Exception {
        when(mpConfig.getWebhookSecret()).thenReturn("");
        Payment p = mockPayment(99L, "approved", "accredited", "visa", "credit_card",
                new BigDecimal("40000"), "COP", "123",
                OffsetDateTime.parse("2025-01-15T10:30:00-05:00"), "x@y.z");
        when(paymentClient.get(99L)).thenReturn(p);
        when(pedidoRepository.findById(123L)).thenReturn(Optional.of(pedido));

        service.procesarWebhook(null, null, "99", "payment", null);

        assertThat(pedido.getEstadoPago()).isEqualTo("APROBADO");
        assertThat(pedido.getMpPaymentId()).isEqualTo("99");
        assertThat(pedido.getMpPaymentMethod()).isEqualTo("visa");
        assertThat(pedido.getMpPaymentType()).isEqualTo("credit_card");
        assertThat(pedido.getTotalPagado()).isEqualByComparingTo("40000");
        assertThat(pedido.getFechaPago()).isNotNull();
        verify(pedidoRepository).save(pedido);
    }

    @Test
    void webhook_firmaInvalida_noActualizaPedido() {
        when(mpConfig.getWebhookSecret()).thenReturn("secreto-de-prueba");

        service.procesarWebhook("ts=123,v1=deadbeef", "req-1", "99", "payment", null);

        verify(pedidoRepository, never()).findById(any());
        verify(pedidoRepository, never()).save(any());
    }

    @Test
    void webhook_firmaValida_actualizaPedido() throws Exception {
        String secret = "mi-secreto";
        String dataId = "99";
        String requestId = "req-1";
        String ts = "1700000000";
        String manifest = "id:" + dataId + ";request-id:" + requestId + ";ts:" + ts + ";";
        String v1 = hmacHex(secret, manifest);
        String xSignature = "ts=" + ts + ",v1=" + v1;

        when(mpConfig.getWebhookSecret()).thenReturn(secret);
        Payment p = mockPayment(99L, "approved", "ok", "pse", "bank_transfer",
                new BigDecimal("40000"), "COP", "123",
                OffsetDateTime.parse("2025-01-15T10:30:00-05:00"), "a@b.c");
        when(paymentClient.get(99L)).thenReturn(p);
        when(pedidoRepository.findById(123L)).thenReturn(Optional.of(pedido));

        service.procesarWebhook(xSignature, requestId, dataId, "payment", null);

        verify(pedidoRepository, times(1)).save(pedido);
        assertThat(pedido.getEstadoPago()).isEqualTo("APROBADO");
    }

    @Test
    void webhook_typeNoEsPayment_seIgnora() {
        when(mpConfig.getWebhookSecret()).thenReturn("");
        service.procesarWebhook(null, null, "99", "merchant_order", null);
        verify(pedidoRepository, never()).findById(any());
    }

    @Test
    void webhook_dataIdEnBody_seExtraeYActualiza() throws Exception {
        when(mpConfig.getWebhookSecret()).thenReturn("");
        Payment p = mockPayment(77L, "pending", "pending_review", "pse", "bank_transfer",
                new BigDecimal("40000"), "COP", "123", null, null);
        when(paymentClient.get(77L)).thenReturn(p);
        when(pedidoRepository.findById(123L)).thenReturn(Optional.of(pedido));

        Map<String, Object> body = Map.of(
                "type", "payment",
                "data", Map.of("id", "77"));
        service.procesarWebhook(null, null, null, null, body);

        assertThat(pedido.getEstadoPago()).isEqualTo("EN_PROCESO");
        assertThat(pedido.getFechaPago()).isNull();
    }

    @Test
    void webhook_pedidoNoEncontrado_noFalla() throws Exception {
        when(mpConfig.getWebhookSecret()).thenReturn("");
        Payment p = mockPayment(77L, "approved", "ok", "visa", "credit_card",
                new BigDecimal("1"), "COP", "999", OffsetDateTime.now(), "x@y.z");
        when(paymentClient.get(77L)).thenReturn(p);
        when(pedidoRepository.findById(999L)).thenReturn(Optional.empty());

        // No debe lanzar
        service.procesarWebhook(null, null, "77", "payment", null);
        verify(pedidoRepository, never()).save(any());
    }

    @Test
    void webhook_excepcionDelSdk_noPropaga() throws Exception {
        when(mpConfig.getWebhookSecret()).thenReturn("");
        when(paymentClient.get(77L)).thenThrow(new RuntimeException("MP caído"));

        // Contrato del servicio: nunca propagar.
        service.procesarWebhook(null, null, "77", "payment", null);
        verify(pedidoRepository, never()).save(any());
    }

    @Test
    void webhook_estadosMapeadosCorrectamente() throws Exception {
        when(mpConfig.getWebhookSecret()).thenReturn("");
        when(pedidoRepository.findById(123L)).thenReturn(Optional.of(pedido));

        // rejected → RECHAZADO
        Payment rej = mockPayment(1L, "rejected", "x", "visa", "credit_card",
                new BigDecimal("1"), "COP", "123", null, "a@b.c");
        when(paymentClient.get(1L)).thenReturn(rej);
        service.procesarWebhook(null, null, "1", "payment", null);
        assertThat(pedido.getEstadoPago()).isEqualTo("RECHAZADO");

        // in_process → EN_PROCESO
        Payment ip = mockPayment(2L, "in_process", "x", "visa", "credit_card",
                new BigDecimal("1"), "COP", "123", null, "a@b.c");
        when(paymentClient.get(2L)).thenReturn(ip);
        service.procesarWebhook(null, null, "2", "payment", null);
        assertThat(pedido.getEstadoPago()).isEqualTo("EN_PROCESO");

        // refunded → RECHAZADO
        Payment ref = mockPayment(3L, "refunded", "x", "visa", "credit_card",
                new BigDecimal("1"), "COP", "123", null, "a@b.c");
        when(paymentClient.get(3L)).thenReturn(ref);
        service.procesarWebhook(null, null, "3", "payment", null);
        assertThat(pedido.getEstadoPago()).isEqualTo("RECHAZADO");
    }

    // ───────── helpers ─────────

    private static String hmacHex(String secret, String manifest) throws Exception {
        Mac mac = Mac.getInstance("HmacSHA256");
        mac.init(new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
        byte[] hash = mac.doFinal(manifest.getBytes(StandardCharsets.UTF_8));
        StringBuilder hex = new StringBuilder();
        for (byte b : hash) hex.append(String.format("%02x", b));
        return hex.toString();
    }

    private static Preference mockPreference(String id, String init, String sandbox) throws Exception {
        Preference p = new Preference();
        setField(p, "id", id);
        setField(p, "initPoint", init);
        setField(p, "sandboxInitPoint", sandbox);
        return p;
    }

    private static Payment mockPayment(Long id, String status, String statusDetail,
                                       String methodId, String typeId, BigDecimal amount,
                                       String currency, String externalRef,
                                       OffsetDateTime dateApproved, String email) throws Exception {
        Payment p = new Payment();
        setField(p, "id", id);
        setField(p, "status", status);
        setField(p, "statusDetail", statusDetail);
        setField(p, "paymentMethodId", methodId);
        setField(p, "paymentTypeId", typeId);
        setField(p, "transactionAmount", amount);
        setField(p, "currencyId", currency);
        setField(p, "externalReference", externalRef);
        setField(p, "dateApproved", dateApproved);
        if (email != null) {
            PaymentPayer payer = new PaymentPayer();
            setField(payer, "email", email);
            setField(p, "payer", payer);
        }
        return p;
    }

    private static void setField(Object target, String name, Object value) throws Exception {
        Class<?> c = target.getClass();
        while (c != null) {
            try {
                java.lang.reflect.Field f = c.getDeclaredField(name);
                f.setAccessible(true);
                f.set(target, value);
                return;
            } catch (NoSuchFieldException e) {
                c = c.getSuperclass();
            }
        }
        throw new NoSuchFieldException(name);
    }
}
