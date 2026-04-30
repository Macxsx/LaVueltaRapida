package com.example.demo.service;

import java.util.List;
import java.util.Optional;

import com.example.demo.entitys.Domiciliario;

public interface DomiciliarioService {

    List<Domiciliario> findAll();

    Optional<Domiciliario> findById(Long id);

    /** Guardado directo, sin validaciones (uso interno de PedidoService). */
    Domiciliario save(Domiciliario domiciliario);

    Optional<Domiciliario> findFirstDisponible();

    /** Lanza IllegalStateException si la cédula o el celular ya están en uso. */
    Domiciliario create(Domiciliario domiciliario);

    /**
     * Lanza NoSuchElementException si no existe.
     * Lanza IllegalStateException si la cédula o el celular ya pertenecen a otro.
     */
    Domiciliario update(Long id, Domiciliario data);

    /**
     * Lanza NoSuchElementException si no existe.
     * Lanza IllegalStateException si tiene un pedido en curso.
     */
    void delete(Long id);
}
