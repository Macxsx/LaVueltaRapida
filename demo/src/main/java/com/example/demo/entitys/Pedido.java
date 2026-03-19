package com.example.demo.entitys;

import java.util.ArrayList;
import java.util.List;
import java.time.LocalDateTime;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import jakarta.persistence.CascadeType;
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
