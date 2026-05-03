package com.example.demo.dto;

import java.math.BigDecimal;
import java.util.List;

public class MpPreferenceRequest {

    private Long pedidoId;
    private BigDecimal total;
    private List<MpItemRequest> items;
    private MpPayerRequest payer;
    private String origin;

    public MpPreferenceRequest() {
    }

    public Long getPedidoId() {
        return pedidoId;
    }

    public void setPedidoId(Long pedidoId) {
        this.pedidoId = pedidoId;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }

    public List<MpItemRequest> getItems() {
        return items;
    }

    public void setItems(List<MpItemRequest> items) {
        this.items = items;
    }

    public MpPayerRequest getPayer() {
        return payer;
    }

    public void setPayer(MpPayerRequest payer) {
        this.payer = payer;
    }

    public String getOrigin() {
        return origin;
    }

    public void setOrigin(String origin) {
        this.origin = origin;
    }
}
