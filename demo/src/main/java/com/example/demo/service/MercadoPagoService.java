package com.example.demo.service;

import java.util.Map;

import com.example.demo.dto.CardPaymentRequest;
import com.example.demo.dto.MpPreferenceRequest;
import com.mercadopago.exceptions.MPApiException;
import com.mercadopago.exceptions.MPException;

public interface MercadoPagoService {

    Map<String, Object> crearPreferencia(MpPreferenceRequest req) throws MPException, MPApiException;

    /**
     * Checkout API: crea un pago directo con token de tarjeta.
     * Si el request incluye pedidoId, actualiza el estadoPago del pedido inmediatamente.
     * deviceId es el MP_DEVICE_SESSION_ID generado por el SDK en el frontend.
     */
    Map<String, Object> procesarPagoConTarjeta(CardPaymentRequest req, String deviceId, String clientIp) throws MPException, MPApiException;

    Map<String, Object> consultarPago(String paymentId) throws MPException, MPApiException;

    void procesarWebhook(String xSignature,
                         String xRequestId,
                         String dataIdQuery,
                         String typeQuery,
                         Map<String, Object> body);

    /**
     * Consulta el estado actual del pago en MP para el pedido dado y actualiza
     * el pedido en base de datos (estadoPago, y estado=CANCELADO si fue rechazado).
     * Retorna el pedido actualizado.
     * Lanza NoSuchElementException si el pedido no existe o no tiene mpPaymentId.
     */
    Map<String, Object> sincronizarPago(Long pedidoId) throws MPException, MPApiException;
}
