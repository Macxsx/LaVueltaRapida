package com.example.demo.entitys;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.time.LocalDateTime;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.Data;

@Data
@Entity
public class Pedido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaEntrega;

    @Enumerated(EnumType.STRING)
    private EstadoPedido estado;

    @ManyToOne
    @JoinColumn(name = "cliente_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Cliente cliente;

    @OneToMany(mappedBy = "pedido", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<LineaPedido> lineasPedido = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "domiciliario_id")
    private Domiciliario domiciliario;

    // ─── Mercado Pago ──────────────────────────────────────────────
    @Column(name = "estado_pago", length = 20)
    private String estadoPago = "PENDIENTE";

    @Column(name = "mp_preference_id", length = 100)
    private String mpPreferenceId;

    @Column(name = "mp_payment_id", length = 100)
    private String mpPaymentId;

    @Column(name = "mp_payment_method", length = 50)
    private String mpPaymentMethod;

    @Column(name = "mp_payment_type", length = 50)
    private String mpPaymentType;

    @Column(name = "total_pagado", precision = 12, scale = 2)
    private BigDecimal totalPagado;

    @Column(name = "fecha_pago")
    private LocalDateTime fechaPago;

    public Pedido() {
    }

    public Pedido(Long id, LocalDateTime fechaCreacion, LocalDateTime fechaEntrega,
                  EstadoPedido estado, Cliente cliente, Domiciliario domiciliario) {
        this.id = id;
        this.fechaCreacion = fechaCreacion;
        this.fechaEntrega = fechaEntrega;
        this.estado = estado;
        this.cliente = cliente;
        this.domiciliario = domiciliario;
    }

    public Pedido(LocalDateTime fechaCreacion, LocalDateTime fechaEntrega,
                  EstadoPedido estado, Cliente cliente) {
        this.fechaCreacion = fechaCreacion;
        this.fechaEntrega = fechaEntrega;
        this.estado = estado;
        this.cliente = cliente;
    }
}
