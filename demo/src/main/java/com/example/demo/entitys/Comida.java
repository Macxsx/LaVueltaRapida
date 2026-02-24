package com.example.demo.entitys;

import lombok.AllArgsConstructor;
import lombok.Data;
@Data
@AllArgsConstructor
public class Comida {
    
    private int id;
    private String name;
    private String description;
    private float price;
    private String currency;
    private String image;
    private boolean available;
}
