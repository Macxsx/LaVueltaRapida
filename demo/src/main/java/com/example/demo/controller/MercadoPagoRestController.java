package com.example.demo.controller;

import java.util.Map;
import java.util.NoSuchElementException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletRequest;

import com.example.demo.dto.CardPaymentRequest;
import com.example.demo.dto.MpPreferenceRequest;
import com.example.demo.error.ApiError;
import com.example.demo.service.MercadoPagoService;
import com.mercadopago.exceptions.MPApiException;
import com.mercadopago.exceptions.MPException;

@RestController
@RequestMapping("/api/mp")
public class MercadoPagoRestController {

    private static final Logger log = LoggerFactory.getLogger(MercadoPagoRestController.class);

    @Autowired
    private MercadoPagoService mercadoPagoService;

    @PostMapping("/preference")
    public ResponseEntity<?> crearPreferencia(@RequestBody MpPreferenceRequest req) {
        try {
            return ResponseEntity.ok(mercadoPagoService.crearPreferencia(req));
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(404).body(ApiError.of(e.getMessage()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(ApiError.of(e.getMessage()));
        } catch (IllegalStateException e) {
            log.error("Error de configuración del servidor MP: {}", e.getMessage());
            return ResponseEntity.status(500).body(ApiError.of(e.getMessage()));
        } catch (MPApiException e) {
            String detail = e.getApiResponse() != null ? e.getApiResponse().getContent() : null;
            log.error("Error de la API de Mercado Pago: status={}, body={}", e.getStatusCode(), detail);
            return ResponseEntity.status(500).body(ApiError.of("No se pudo crear la preferencia de pago.", detail));
        } catch (MPException e) {
            log.error("Error del SDK de Mercado Pago: {}", e.getMessage());
            return ResponseEntity.status(500).body(ApiError.of("No se pudo crear la preferencia de pago.", e.getMessage()));
        } catch (Exception e) {
            log.error("Error inesperado creando preferencia MP: {}", e.getMessage(), e);
            return ResponseEntity.status(500).body(ApiError.of("Error inesperado.", e.getMessage()));
        }
    }

    @PostMapping("/payment")
    public ResponseEntity<?> procesarPagoConTarjeta(
            @RequestBody CardPaymentRequest req,
            @RequestHeader(value = "X-meli-session-id", required = false) String deviceId,
            HttpServletRequest httpRequest) {
        String clientIp = extraerIpCliente(httpRequest);
        try {
            return ResponseEntity.ok(mercadoPagoService.procesarPagoConTarjeta(req, deviceId, clientIp));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(ApiError.of(e.getMessage()));
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(404).body(ApiError.of(e.getMessage()));
        } catch (MPApiException e) {
            String detail = e.getApiResponse() != null ? e.getApiResponse().getContent() : null;
            log.error("Error API MP procesando pago con tarjeta: status={}, body={}", e.getStatusCode(), detail);
            return ResponseEntity.status(500).body(ApiError.of("No se pudo procesar el pago.", detail));
        } catch (MPException e) {
            log.error("Error SDK MP procesando pago con tarjeta: {}", e.getMessage());
            return ResponseEntity.status(500).body(ApiError.of("No se pudo procesar el pago.", e.getMessage()));
        } catch (Exception e) {
            log.error("Error inesperado procesando pago con tarjeta: {}", e.getMessage(), e);
            return ResponseEntity.status(500).body(ApiError.of("Error inesperado.", e.getMessage()));
        }
    }

    @GetMapping("/payment/{paymentId}")
    public ResponseEntity<?> consultarPago(@PathVariable String paymentId) {
        try {
            return ResponseEntity.ok(mercadoPagoService.consultarPago(paymentId));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(ApiError.of(e.getMessage()));
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(404).body(ApiError.of(e.getMessage()));
        } catch (MPApiException e) {
            String detail = e.getApiResponse() != null ? e.getApiResponse().getContent() : null;
            log.error("Error API MP consultando pago: status={}, body={}", e.getStatusCode(), detail);
            return ResponseEntity.status(500).body(ApiError.of("No se pudo consultar el pago.", detail));
        } catch (MPException e) {
            log.error("Error SDK MP consultando pago: {}", e.getMessage());
            return ResponseEntity.status(500).body(ApiError.of("No se pudo consultar el pago.", e.getMessage()));
        } catch (Exception e) {
            log.error("Error inesperado consultando pago MP: {}", e.getMessage(), e);
            return ResponseEntity.status(500).body(ApiError.of("Error inesperado.", e.getMessage()));
        }
    }

    @PostMapping("/sincronizar/{pedidoId}")
    public ResponseEntity<?> sincronizarPago(@PathVariable Long pedidoId) {
        try {
            return ResponseEntity.ok(mercadoPagoService.sincronizarPago(pedidoId));
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(404).body(ApiError.of(e.getMessage()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(ApiError.of(e.getMessage()));
        } catch (MPApiException e) {
            String detail = e.getApiResponse() != null ? e.getApiResponse().getContent() : null;
            log.error("Error API MP sincronizando pedido {}: status={}, body={}", pedidoId, e.getStatusCode(), detail);
            return ResponseEntity.status(500).body(ApiError.of("No se pudo consultar el estado del pago.", detail));
        } catch (MPException e) {
            log.error("Error SDK MP sincronizando pedido {}: {}", pedidoId, e.getMessage());
            return ResponseEntity.status(500).body(ApiError.of("No se pudo consultar el estado del pago.", e.getMessage()));
        } catch (Exception e) {
            log.error("Error inesperado sincronizando pedido {}: {}", pedidoId, e.getMessage(), e);
            return ResponseEntity.status(500).body(ApiError.of("Error inesperado.", e.getMessage()));
        }
    }

    @PostMapping("/webhook")
    public ResponseEntity<Void> webhook(
            @RequestHeader(value = "x-signature", required = false) String xSignature,
            @RequestHeader(value = "x-request-id", required = false) String xRequestId,
            @RequestParam(required = false) String type,
            @RequestParam(value = "data.id", required = false) String dataId,
            @RequestBody(required = false) Map<String, Object> body) {
        log.info("=== WEBHOOK MP RECIBIDO ===");
        log.info("  x-signature  : {}", xSignature);
        log.info("  x-request-id : {}", xRequestId);
        log.info("  type (query) : {}", type);
        log.info("  data.id (query): {}", dataId);
        log.info("  body         : {}", body);
        log.info("==========================");

        // Contrato: SIEMPRE devolver 200, incluso si el servicio escapa con excepción.
        // MP reintenta el webhook ante cualquier respuesta != 2xx.
        try {
            mercadoPagoService.procesarWebhook(xSignature, xRequestId, dataId, type, body);
        } catch (Exception e) {
            log.error("Excepción inesperada del servicio de MP en webhook (se ignora para devolver 200): {}",
                    e.getMessage(), e);
        }
        return ResponseEntity.ok().build();
    }

    private String extraerIpCliente(HttpServletRequest req) {
        String[] headers = {
            "X-Forwarded-For", "X-Real-IP", "Proxy-Client-IP",
            "WL-Proxy-Client-IP", "HTTP_CLIENT_IP", "HTTP_X_FORWARDED_FOR"
        };
        for (String h : headers) {
            String val = req.getHeader(h);
            if (val != null && !val.isBlank() && !"unknown".equalsIgnoreCase(val)) {
                return val.split(",")[0].trim();
            }
        }
        return req.getRemoteAddr();
    }
}
