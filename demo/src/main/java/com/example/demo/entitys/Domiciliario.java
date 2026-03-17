package com.example.demo.entitys;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.Data;

@Data
@Entity
public class Domiciliario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre;

    @Column(unique = true)
    private String cedula;

    @Column(unique = true)
    private String celular;

    private boolean disponible;

    @OneToMany(mappedBy = "domiciliario")
    private List<Pedido> pedidos = new ArrayList<>();

    public Domiciliario() {
    }

    public Domiciliario(Long id, String nombre, String cedula, String celular, boolean disponible) {
        this.id = id;
        this.nombre = nombre;
        this.cedula = cedula;
        this.celular = celular;
        this.disponible = disponible;
    }

    public Domiciliario(String nombre, String cedula, String celular, boolean disponible) {
        this.nombre = nombre;
        this.cedula = cedula;
        this.celular = celular;
        this.disponible = disponible;
    }
}
