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


@Data
@Entity
public class Adicional {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false)
    private double price;

    private boolean available;

    @ManyToMany(mappedBy = "adicionales")
    private List<Categoria> categorias = new ArrayList<>();

    // Constructor vacío
    public Adicional() {
    }

    // Constructor completo
    public Adicional(Long id, String name, double price, boolean available) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.available = available;
    }

    public Adicional(String name, double price, boolean available) {
        this.name = name;
        this.price = price;
        this.available = available;
    }

}
