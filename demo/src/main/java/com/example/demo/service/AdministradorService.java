package com.example.demo.service;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;

import com.example.demo.entitys.Administrador;

public interface AdministradorService {

    Collection<Administrador> findAll();

    Optional<Administrador> findById(Long id);

    Optional<Administrador> findByUsuario(String usuario);

    void save(Administrador administrador);

    void deleteById(Long id);

    boolean existsById(Long id);

    // ── Métodos de lógica de negocio ──

    /**
     * Actualiza un administrador con validación
     * @return Map con {"success": true, "administrador": Administrador} o {"success": false, "error": String}
     */
    Map<String, Object> updateAdministrador(Long id, String usuario, String contrasena, String currentPassword);

    /**
     * Valida las credenciales de un administrador
     * @return true si las credenciales son correctas, false si no
     */
    boolean validateCredentials(String usuario, String contrasena);
}
