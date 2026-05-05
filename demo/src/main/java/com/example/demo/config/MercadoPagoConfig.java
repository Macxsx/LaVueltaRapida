package com.example.demo.config;

import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.mercadopago.client.payment.PaymentClient;
import com.mercadopago.client.preference.PreferenceClient;

@Configuration
public class MercadoPagoConfig {

    private static final Logger log = LoggerFactory.getLogger(MercadoPagoConfig.class);

    @Value("${mercadopago.access-token:}")
    private String accessToken;

    @Value("${mercadopago.public-key:}")
    private String publicKey;

    @Value("${mercadopago.webhook-secret:}")
    private String webhookSecret;

    @Value("${app.frontend-url:http://localhost:4200}")
    private String frontendUrl;

    @Value("${app.backend-url:}")
    private String backendUrl;

    @PostConstruct
    public void init() {
        if (accessToken == null || accessToken.isBlank()) {
            log.warn("MERCADOPAGO_ACCESS_TOKEN no está configurado. " +
                    "El SDK de Mercado Pago no se inicializará y los pagos no funcionarán hasta que se provea el secreto.");
            return;
        }
        com.mercadopago.MercadoPagoConfig.setAccessToken(accessToken);
        log.info("Mercado Pago SDK inicializado correctamente.");
    }

    public String getAccessToken() {
        return accessToken;
    }

    public String getPublicKey() {
        return publicKey;
    }

    public String getWebhookSecret() {
        return webhookSecret;
    }

    public String getFrontendUrl() {
        return frontendUrl;
    }

    public String getBackendUrl() {
        return backendUrl;
    }

    @Bean
    public PreferenceClient preferenceClient() {
        return new PreferenceClient();
    }

    @Bean
    public PaymentClient paymentClient() {
        return new PaymentClient();
    }
}
