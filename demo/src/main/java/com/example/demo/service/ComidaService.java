package com.example.demo.service;

import java.util.Collection;

import com.example.demo.entitys.Comida;

public interface ComidaService {

    Collection<Comida> findAll();

    Comida findById(Long id);


    boolean estaEnPedido(Long id);

    /**
     * Lanza IllegalArgumentException si los campos son inválidos o la categoría no existe.
     */
    Comida create(String name, String description, double price,
                  String image, boolean available, Long categoryId);

    /**
     * Lanza NoSuchElementException si no existe.
     * Lanza IllegalArgumentException si los campos son inválidos o la categoría no existe.
     */
    Comida update(Long id, String name, String description, double price,
                  String image, boolean available, Long categoryId);

    /**
     * Lanza NoSuchElementException si no existe.
     * Lanza IllegalStateException si ya está incluida en un pedido.
     */
    void delete(Long id);
}
