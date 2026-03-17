package com.example.demo.entitys;

import java.util.ArrayList;
import java.util.List;

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
    private Comida comida;

    @OneToMany(mappedBy = "lineaPedido")
    private List<LineaPedidoAdicional> adicionales = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "carrito_id")
    private Carrito carrito;

    @ManyToOne
    @JoinColumn(name = "pedido_id")
    private Pedido pedido;

    public LineaPedido() {
    }

    public LineaPedido(Long id, Integer cantidad, Comida comida, Carrito carrito, Pedido pedido) {
        this.id = id;
        this.cantidad = cantidad;
        this.comida = comida;
        this.carrito = carrito;
        this.pedido = pedido;
    }

    public LineaPedido(Integer cantidad, Comida comida, Carrito carrito, Pedido pedido) {
        this.cantidad = cantidad;
        this.comida = comida;
        this.carrito = carrito;
        this.pedido = pedido;
    }
}
