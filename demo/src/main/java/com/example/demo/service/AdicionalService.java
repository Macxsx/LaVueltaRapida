package com.example.demo.service;

import java.util.Collection;

import com.example.demo.entitys.Adicional;
import com.example.demo.entitys.Categoria;

public interface AdicionalService {

    Collection<Adicional> findAll();

    Adicional findById(Long id);

    Collection<Adicional> findByCategoriaId(Long categoriaId);

    Collection<Categoria> findCategorias(Long adicionalId);

    boolean estaEnPedido(Long id);

    /** Lanza IllegalArgumentException si los campos son inválidos. */
    Adicional create(String name, double price, boolean available, Long[] categoriaIds);

    /**
     * Lanza NoSuchElementException si no existe.
     * Lanza IllegalArgumentException si los campos son inválidos.
     */
    Adicional update(Long id, String name, double price, boolean available, Long[] categoriaIds);

    /**
     * Lanza NoSuchElementException si no existe.
     * Lanza IllegalStateException si ya está incluido en un pedido.
     */
    void delete(Long id);
}
