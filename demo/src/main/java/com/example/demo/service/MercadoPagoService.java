package com.example.demo.service;

import java.util.Map;

import com.example.demo.dto.MpPreferenceRequest;
import com.mercadopago.exceptions.MPApiException;
import com.mercadopago.exceptions.MPException;

public interface MercadoPagoService {

    Map<String, Object> crearPreferencia(MpPreferenceRequest req) throws MPException, MPApiException;

    Map<String, Object> consultarPago(String paymentId) throws MPException, MPApiException;

    void procesarWebhook(String xSignature,
                         String xRequestId,
                         String dataIdQuery,
                         String typeQuery,
                         Map<String, Object> body);
}
