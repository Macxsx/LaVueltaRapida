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
     */
    Map<String, Object> procesarPagoConTarjeta(CardPaymentRequest req) throws MPException, MPApiException;

    Map<String, Object> consultarPago(String paymentId) throws MPException, MPApiException;

    void procesarWebhook(String xSignature,
                         String xRequestId,
                         String dataIdQuery,
                         String typeQuery,
                         Map<String, Object> body);
}
