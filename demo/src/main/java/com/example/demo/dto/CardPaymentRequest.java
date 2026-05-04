package com.example.demo.dto;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonProperty;

public class CardPaymentRequest {

    /** ID del pedido al que se vincula este pago (opcional pero recomendado). */
    private Long pedidoId;

    /** Token de tarjeta generado por el SDK de Mercado Pago en el frontend. */
    private String token;

    @JsonProperty("transaction_amount")
    private BigDecimal transactionAmount;

    @JsonProperty("payment_method_id")
    private String paymentMethodId;

    private Integer installments;

    private CardPayerRequest payer;

    public Long getPedidoId() { return pedidoId; }
    public void setPedidoId(Long pedidoId) { this.pedidoId = pedidoId; }

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }

    public BigDecimal getTransactionAmount() { return transactionAmount; }
    public void setTransactionAmount(BigDecimal transactionAmount) { this.transactionAmount = transactionAmount; }

    public String getPaymentMethodId() { return paymentMethodId; }
    public void setPaymentMethodId(String paymentMethodId) { this.paymentMethodId = paymentMethodId; }

    public Integer getInstallments() { return installments; }
    public void setInstallments(Integer installments) { this.installments = installments; }

    public CardPayerRequest getPayer() { return payer; }
    public void setPayer(CardPayerRequest payer) { this.payer = payer; }

    // ── Payer anidado ──────────────────────────────────────────────────────

    public static class CardPayerRequest {

        private String email;
        private CardIdentificationRequest identification;

        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }

        public CardIdentificationRequest getIdentification() { return identification; }
        public void setIdentification(CardIdentificationRequest identification) { this.identification = identification; }
    }

    public static class CardIdentificationRequest {

        private String type;
        private String number;

        public String getType() { return type; }
        public void setType(String type) { this.type = type; }

        public String getNumber() { return number; }
        public void setNumber(String number) { this.number = number; }
    }
}
