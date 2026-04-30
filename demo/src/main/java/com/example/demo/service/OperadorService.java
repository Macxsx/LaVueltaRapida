package com.example.demo.service;

import java.util.List;
import java.util.Optional;

import com.example.demo.entitys.Operador;

public interface OperadorService {

    List<Operador> findAll();

    Optional<Operador> findById(Long id);

    Optional<Operador> findByUsuario(String usuario);

    /** Lanza IllegalStateException si el usuario ya está en uso. */
    Operador create(Operador operador);

    /**
     * Lanza NoSuchElementException si no existe.
     * Lanza SecurityException si currentPassword es incorrecto.
     * Lanza IllegalStateException si el nuevo usuario ya pertenece a otro operador.
     */
    Operador update(Long id, String nombre, String usuario, String contrasena, String currentPassword);

    /** Lanza NoSuchElementException si no existe. */
    void delete(Long id);
}
