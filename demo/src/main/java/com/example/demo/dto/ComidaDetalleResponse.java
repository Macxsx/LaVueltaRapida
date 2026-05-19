package com.example.demo.dto;

import com.example.demo.entitys.Adicional;
import com.example.demo.entitys.Comida;
import java.util.List;

public class ComidaDetalleResponse {
    public Long id;
    public String name;
    public String description;
    public double price;
    public String image;
    public boolean available;
    public CategoriaConAdicionales category;

    public static class CategoriaConAdicionales {
        public Long id;
        public String name;
        public List<Adicional> adicionales;
    }

    public static ComidaDetalleResponse from(Comida comida) {
        ComidaDetalleResponse r = new ComidaDetalleResponse();
        r.id = comida.getId();
        r.name = comida.getName();
        r.description = comida.getDescription();
        r.price = comida.getPrice();
        r.image = comida.getImage();
        r.available = comida.isAvailable();
        CategoriaConAdicionales cat = new CategoriaConAdicionales();
        cat.id = comida.getCategory().getId();
        cat.name = comida.getCategory().getName();
        cat.adicionales = comida.getCategory().getAdicionales();
        r.category = cat;
        return r;
    }
}
