package com.example.demo.entitys;

public class Comida {

    private Integer id;
    private String name;
    private String description;
    private int price;
    private String image;
    private boolean available;
    private Categoria category;

    // Constructor vacío
    public Comida() {
    }

    // Constructor completo
    public Comida(Integer id, String name, String description, int price, String image, boolean available, Categoria category) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.image = image;
        this.available = available;
        this.category = category;
    }

    // Getters y Setters

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public int getPrice() { return price; }
    public void setPrice(int price) { this.price = price; }

    public String getImage() { return image; }
    public void setImage(String image) { this.image = image; }

    public boolean isAvailable() { return available; }
    public void setAvailable(boolean available) { this.available = available; }

    public Categoria getCategory() { return category; }
    public void setCategory(Categoria category) { this.category = category; }


}