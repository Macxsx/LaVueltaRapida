package com.example.demo.entitys;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import lombok.Data;



public enum EstadoPedido {

    RECIBIDO,
    COCINANDO,
    ENVIADO,
    ENTREGADO;

    /** Retorna el único estado al que puede avanzar este pedido, o null si ya es el estado final. */
    public EstadoPedido siguiente() {
        return switch (this) {
            case RECIBIDO  -> COCINANDO;
            case COCINANDO -> ENVIADO;
            case ENVIADO   -> ENTREGADO;
            case ENTREGADO -> null;
        };
    }
}
