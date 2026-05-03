package com.example.demo.controller;

import java.util.LinkedHashMap;
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

import com.example.demo.dto.MpPreferenceRequest;
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
            return ResponseEntity.status(404).body(error(e.getMessage(), null));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(error(e.getMessage(), null));
        } catch (MPApiException e) {
            String detail = e.getApiResponse() != null ? e.getApiResponse().getContent() : null;
            log.error("Error de la API de Mercado Pago: status={}, body={}", e.getStatusCode(), detail);
            return ResponseEntity.status(500)
                    .body(error("No se pudo crear la preferencia de pago.", detail));
        } catch (MPException e) {
            log.error("Error del SDK de Mercado Pago: {}", e.getMessage());
            return ResponseEntity.status(500)
                    .body(error("No se pudo crear la preferencia de pago.", e.getMessage()));
        } catch (Exception e) {
            log.error("Error inesperado creando preferencia MP: {}", e.getMessage(), e);
            return ResponseEntity.status(500)
                    .body(error("Error inesperado.", e.getMessage()));
        }
    }

    @GetMapping("/payment/{paymentId}")
    public ResponseEntity<?> consultarPago(@PathVariable String paymentId) {
        try {
            return ResponseEntity.ok(mercadoPagoService.consultarPago(paymentId));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(error(e.getMessage(), null));
        } catch (MPApiException e) {
            if (e.getStatusCode() == 404) {
                return ResponseEntity.status(404).body(error("Pago no encontrado.", null));
            }
            String detail = e.getApiResponse() != null ? e.getApiResponse().getContent() : null;
            log.error("Error API MP consultando pago: status={}, body={}", e.getStatusCode(), detail);
            return ResponseEntity.status(500)
                    .body(error("No se pudo consultar el pago.", detail));
        } catch (MPException e) {
            log.error("Error SDK MP consultando pago: {}", e.getMessage());
            return ResponseEntity.status(500)
                    .body(error("No se pudo consultar el pago.", e.getMessage()));
        } catch (Exception e) {
            log.error("Error inesperado consultando pago MP: {}", e.getMessage(), e);
            return ResponseEntity.status(500)
                    .body(error("Error inesperado.", e.getMessage()));
        }
    }

    @PostMapping("/webhook")
    public ResponseEntity<Void> webhook(
            @RequestHeader(value = "x-signature", required = false) String xSignature,
            @RequestHeader(value = "x-request-id", required = false) String xRequestId,
            @RequestParam(value = "type", required = false) String type,
            @RequestParam(value = "data.id", required = false) String dataId,
            @RequestBody(required = false) Map<String, Object> body) {
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

    private Map<String, Object> error(String mensaje, String detalle) {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("error", mensaje);
        if (detalle != null) m.put("detail", detalle);
        return m;
    }
}
