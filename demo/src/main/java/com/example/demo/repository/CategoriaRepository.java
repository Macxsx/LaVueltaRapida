package com.example.demo.repository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import com.example.demo.entitys.Categoria;

@Repository
public class CategoriaRepository {

    private final Map<Integer, Categoria> categorias = new HashMap<>();

    public CategoriaRepository() {
        categorias.put(1, new Categoria(1, "Clasicas"));
        categorias.put(2, new Categoria(2, "Especiales"));
        categorias.put(3, new Categoria(3, "Picantes"));
        categorias.put(4, new Categoria(4, "Bebidas"));
        categorias.put(5, new Categoria(5, "Postres"));
    }

    public Categoria findById(int id) {
        return categorias.get(id);
    }

    public Collection<Categoria> findAll() {
        return categorias.values();
    }
}