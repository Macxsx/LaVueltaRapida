package com.example.demo.service;

import java.util.List;
import java.util.Optional;

import com.example.demo.entitys.Carrito;

public interface CarritoService {

    Optional<Carrito> findById(Long id);

    Optional<Carrito> findByClienteId(Long clienteId);

    Carrito save(Carrito carrito);

    /**
     * Lanza IllegalArgumentException si comidaId o cantidad son inválidos.
     * Lanza NoSuchElementException si el carrito, la comida o un adicional no existen.
     */
    Carrito addProducto(Long carritoId, Long comidaId, Integer cantidad, List<Long> adicionalesIds);

    /** Lanza NoSuchElementException si el carrito o la línea no existen. */
    Carrito removeProducto(Long carritoId, Long lineaId);

    /** Lanza NoSuchElementException si el carrito o la línea no existen. */
    Carrito aumentarCantidad(Long carritoId, Long lineaId);

    /** Lanza NoSuchElementException si el carrito o la línea no existen. */
    Carrito disminuirCantidad(Long carritoId, Long lineaId);

    /** Lanza NoSuchElementException si el carrito no existe. */
    Carrito vaciar(Long carritoId);
}
