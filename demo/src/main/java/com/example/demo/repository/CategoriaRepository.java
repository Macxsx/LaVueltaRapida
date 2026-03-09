package com.example.demo.repository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import com.example.demo.entitys.Categoria;

@Repository
public class CategoriaRepository {

    private final Map<Long, Categoria> categorias = new HashMap<>();

    public CategoriaRepository() {
        categorias.put(1L, new Categoria(1L, "Clasicas"));
        categorias.put(2L, new Categoria(2L, "Especiales"));
        categorias.put(3L, new Categoria(3L, "Picantes"));
        categorias.put(4L, new Categoria(4L, "Bebidas"));
        categorias.put(5L, new Categoria(5L, "Postres"));
    }

    public Categoria findById(Long id) {
        return categorias.get(id);
    }

    public Collection<Categoria> findAll() {
        return categorias.values();
    }
}