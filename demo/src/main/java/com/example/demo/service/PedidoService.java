package com.example.demo.service;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.example.demo.entitys.Pedido;

public interface PedidoService {

    /** Clampea page y size antes de paginar (page ≥ 0, 1 ≤ size ≤ 100). */
    Page<Pedido> findAll(int page, int size);

    List<Pedido> findActivos();

    Optional<Pedido> findById(Long id);

    /** Lanza NoSuchElementException si el cliente no existe. */
    List<Pedido> findByClienteId(Long clienteId);

    /**
     * Lanza NoSuchElementException si el carrito no existe.
     * Lanza IllegalArgumentException si el carrito está vacío.
     */
    Pedido desdeCarrito(Long carritoId);

    /**
     * Lanza NoSuchElementException si el pedido no existe.
     * Lanza IllegalArgumentException si estadoStr es nulo, inválido o la transición no está permitida.
     * Lanza IllegalStateException si no hay domiciliarios disponibles al pasar a ENVIADO.
     */
    Pedido actualizarEstado(Long id, String estadoStr);
}
