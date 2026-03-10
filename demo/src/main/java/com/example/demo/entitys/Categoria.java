package com.example.demo.entitys;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Column;

@Entity
public class Categoria {

    @Id
    //PK
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    // nullable, unique, length 
    @Column(nullable = false, unique = true, length = 50)
    private String name;

    // Constructor vacío (Obligatorio para JPA)
    public Categoria() {
    }

    // Constructor completo
    public Categoria(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public Categoria(String name) {
        this.name = name;
    }

    // --- GETTERS Y SETTERS ---

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}