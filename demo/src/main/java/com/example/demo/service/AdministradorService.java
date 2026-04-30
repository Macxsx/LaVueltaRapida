package com.example.demo.service;

import java.util.List;
import java.util.Optional;

import com.example.demo.entitys.Administrador;

public interface AdministradorService {

    List<Administrador> findAll();

    Optional<Administrador> findById(Long id);

    Optional<Administrador> findByUsuario(String usuario);

    /**
     * Lanza NoSuchElementException si no existe.
     * Lanza SecurityException si currentPassword es incorrecto.
     * Lanza IllegalStateException si el nuevo usuario ya pertenece a otro administrador.
     */
    Administrador update(Long id, String usuario, String contrasena, String currentPassword);

    /** Lanza NoSuchElementException si no existe. */
    void delete(Long id);
}
