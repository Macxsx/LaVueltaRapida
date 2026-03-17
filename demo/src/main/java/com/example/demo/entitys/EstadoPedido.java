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
    ENTREGADO
}
