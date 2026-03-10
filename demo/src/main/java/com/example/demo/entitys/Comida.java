package com.example.demo.entitys;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Entity
public class Comida {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    // Restricciones: nullable, length
    @Column(nullable = false, length = 100)
    private String name;
    private String description;
    @Column(nullable = false)
    private double price;
    private String image;
    private boolean available;

    @ManyToOne // <--- ESTO ES LO QUE FALTABA PARA QUE FUNCIONE CON LA NUEVA CATEGORIA
    @JoinColumn(nullable = false)
    private Categoria category;

    // Constructor vacío
    public Comida() {
    }

    // Constructor completo
    public Comida(Long id, String name, String description, double price, String image, boolean available, Categoria category) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.image = image;
        this.available = available;
        this.category = category;
    }

    public Comida(String name, String description, double price, String image, boolean available, Categoria category) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.image = image;
        this.available = available;
        this.category = category;
    }

    // --- GETTERS Y SETTERS ---
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    public String getImage() { return image; }
    public void setImage(String image) { this.image = image; }

    public boolean isAvailable() { return available; }
    public void setAvailable(boolean available) { this.available = available; }

    public Categoria getCategory() { return category; }
    public void setCategory(Categoria category) { this.category = category; }
}