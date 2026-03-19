package com.example.demo.entitys;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.Data;

@Data
@Entity
public class LineaPedido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer cantidad;

    @ManyToOne
    @JoinColumn(name = "comida_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Comida comida;

    @OneToMany(mappedBy = "lineaPedido", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<LineaPedidoAdicional> adicionales = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "carrito_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Carrito carrito;

    @ManyToOne
    @JoinColumn(name = "pedido_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Pedido pedido;

    public LineaPedido() {
    }

    public LineaPedido(Integer cantidad, Comida comida, Carrito carrito, Pedido pedido) {
        this.cantidad = cantidad;
        this.comida = comida;
        this.carrito = carrito;
        this.pedido = pedido;
    }
}
