package com.example.demo.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.example.demo.dto.MpItemRequest;
import com.example.demo.dto.MpPreferenceRequest;
import com.example.demo.service.MercadoPagoService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mercadopago.exceptions.MPApiException;
import com.mercadopago.exceptions.MPException;
import com.mercadopago.net.MPResponse;

@WebMvcTest(controllers = MercadoPagoRestController.class)
@ActiveProfiles("test")
public class MercadoPagoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private MercadoPagoService mercadoPagoService;

    private MpPreferenceRequest buildRequest() {
        MpPreferenceRequest req = new MpPreferenceRequest();
        req.setPedidoId(123L);
        req.setTotal(new BigDecimal("45000"));
        req.setOrigin("https://mi-app.com");

        MpItemRequest it = new MpItemRequest();
        it.setId("5");
        it.setTitle("Pizza Monza");
        it.setQuantity(2);
        it.setUnitPrice(new BigDecimal("18000"));
        req.setItems(List.of(it));
        return req;
    }

    // ───────── POST /api/mp/preference ─────────

    @Test
    void preference_creaPreferenciaExitosamente_retorna200ConSnakeCase() throws Exception {
        Map<String, Object> resp = new LinkedHashMap<>();
        resp.put("id", "PREF-123");
        resp.put("init_point", "https://www.mercadopago.com.co/checkout/v1/redirect?pref=PREF-123");
        resp.put("sandbox_init_point", "https://sandbox.mercadopago.com.co/checkout/v1/redirect?pref=PREF-123");
        when(mercadoPagoService.crearPreferencia(any(MpPreferenceRequest.class))).thenReturn(resp);

        mockMvc.perform(post("/api/mp/preference")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(buildRequest())))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value("PREF-123"))
                .andExpect(jsonPath("$.init_point").exists())
                .andExpect(jsonPath("$.sandbox_init_point").exists());
    }

    @Test
    void preference_pedidoNoExiste_retorna404() throws Exception {
        when(mercadoPagoService.crearPreferencia(any(MpPreferenceRequest.class)))
                .thenThrow(new NoSuchElementException("Pedido no encontrado."));

        mockMvc.perform(post("/api/mp/preference")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(buildRequest())))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Pedido no encontrado."));
    }

    @Test
    void preference_datosInvalidos_retorna400() throws Exception {
        when(mercadoPagoService.crearPreferencia(any(MpPreferenceRequest.class)))
                .thenThrow(new IllegalArgumentException("Falta el campo 'origin'."));

        mockMvc.perform(post("/api/mp/preference")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(buildRequest())))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Falta el campo 'origin'."));
    }

    @Test
    void preference_mpFalla_retorna500ConDetalle() throws Exception {
        when(mercadoPagoService.crearPreferencia(any(MpPreferenceRequest.class)))
                .thenThrow(new MPException("conexión rechazada"));

        mockMvc.perform(post("/api/mp/preference")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(buildRequest())))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.error").value("No se pudo crear la preferencia de pago."))
                .andExpect(jsonPath("$.detail").value("conexión rechazada"));
    }

    // ───────── GET /api/mp/payment/{id} ─────────

    @Test
    void payment_pagoExistente_retorna200ConCamposEsperados() throws Exception {
        Map<String, Object> resp = new LinkedHashMap<>();
        resp.put("id", 123456789L);
        resp.put("status", "approved");
        resp.put("status_detail", "accredited");
        resp.put("payment_method_id", "visa");
        resp.put("payment_type_id", "credit_card");
        resp.put("transaction_amount", new BigDecimal("45000"));
        resp.put("currency_id", "COP");
        resp.put("external_reference", "123");
        resp.put("date_approved", "2025-01-15T10:30:00-05:00");
        resp.put("payer", Map.of("email", "juan@example.com"));
        when(mercadoPagoService.consultarPago("123456789")).thenReturn(resp);

        mockMvc.perform(get("/api/mp/payment/123456789"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(123456789L))
                .andExpect(jsonPath("$.status").value("approved"))
                .andExpect(jsonPath("$.status_detail").value("accredited"))
                .andExpect(jsonPath("$.payment_method_id").value("visa"))
                .andExpect(jsonPath("$.payment_type_id").value("credit_card"))
                .andExpect(jsonPath("$.currency_id").value("COP"))
                .andExpect(jsonPath("$.external_reference").value("123"))
                .andExpect(jsonPath("$.payer.email").value("juan@example.com"));
    }

    @Test
    void payment_pagoNoExiste_retorna404() throws Exception {
        MPResponse fakeResp = new MPResponse(404, null, "{\"message\":\"not found\"}");
        when(mercadoPagoService.consultarPago("999"))
                .thenThrow(new MPApiException("not found", fakeResp));

        mockMvc.perform(get("/api/mp/payment/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Pago no encontrado."));
    }

    @Test
    void payment_idInvalido_retorna400() throws Exception {
        when(mercadoPagoService.consultarPago("abc"))
                .thenThrow(new IllegalArgumentException("paymentId inválido: abc"));

        mockMvc.perform(get("/api/mp/payment/abc"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("paymentId inválido: abc"));
    }

    @Test
    void payment_mpFalla500_retorna500() throws Exception {
        when(mercadoPagoService.consultarPago(anyString()))
                .thenThrow(new MPException("timeout"));

        mockMvc.perform(get("/api/mp/payment/123"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.error").value("No se pudo consultar el pago."));
    }

    // ───────── POST /api/mp/webhook ─────────

    @Test
    void webhook_siempreRetorna200_aunqueServicioFalle() throws Exception {
        // El servicio nunca debe lanzar, pero aún si lo hiciera el controller debe responder 200.
        // Simulamos un servicio que sí ejecuta normalmente.
        doAnswer(inv -> null).when(mercadoPagoService)
                .procesarWebhook(anyString(), anyString(), anyString(), anyString(), any());

        String body = "{\"action\":\"payment.created\",\"data\":{\"id\":\"123456789\"}}";

        mockMvc.perform(post("/api/mp/webhook")
                .header("x-signature", "ts=1700000000,v1=abcdef")
                .header("x-request-id", "req-1")
                .param("type", "payment")
                .param("data.id", "123456789")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
                .andExpect(status().isOk());

        verify(mercadoPagoService, times(1)).procesarWebhook(
                eq("ts=1700000000,v1=abcdef"),
                eq("req-1"),
                eq("123456789"),
                eq("payment"),
                any());
    }

    @Test
    void webhook_sinHeadersNiQueryParams_igualResponde200() throws Exception {
        doAnswer(inv -> null).when(mercadoPagoService)
                .procesarWebhook(any(), any(), any(), any(), any());

        mockMvc.perform(post("/api/mp/webhook")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
                .andExpect(status().isOk());
    }

    @Test
    void webhook_servicioLanzaExcepcion_aunRetorna200() throws Exception {
        // Defensa en profundidad: si el servicio escapa con excepción (regresión futura),
        // el controller debe atraparla y aún así responder 200 — MP reintenta ante 5xx.
        doThrow(new RuntimeException("boom")).when(mercadoPagoService)
                .procesarWebhook(any(), any(), any(), any(), any());

        mockMvc.perform(post("/api/mp/webhook")
                .header("x-signature", "ts=1,v1=ff")
                .header("x-request-id", "r")
                .param("type", "payment")
                .param("data.id", "999")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"data\":{\"id\":\"999\"}}"))
                .andExpect(status().isOk());
    }
}
