package com.example.demo.entitys;

public class Comida {

    private int id;
    private String name;
    private String description;
    private float price;
    private String currency;
    private String image;
    private boolean available;
    private String category;

    // Constructor vacío
    public Comida() {
    }

    // Constructor completo
    public Comida(int id, String name, String description, float price,
                  String currency, String image, boolean available, String category) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.currency = currency;
        this.image = image;
        this.available = available;
        this.category = category;
    }

    // Getters y Setters

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public float getPrice() { return price; }
    public void setPrice(float price) { this.price = price; }

    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }

    public String getImage() { return image; }
    public void setImage(String image) { this.image = image; }

    public boolean isAvailable() { return available; }
    public void setAvailable(boolean available) { this.available = available; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
}