package com.example.demo.entitys;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Data;

@Data
@Entity
public class LineaPedidoAdicional {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "linea_pedido_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private LineaPedido lineaPedido;

    @ManyToOne
    @JoinColumn(name = "adicional_id")
    private Adicional adicional;

    public LineaPedidoAdicional() {
    }

    public LineaPedidoAdicional(LineaPedido lineaPedido, Adicional adicional) {
        this.lineaPedido = lineaPedido;
        this.adicional = adicional;
    }
}
