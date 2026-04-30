package com.example.demo.service;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;

import com.example.demo.entitys.Operador;

public interface OperadorService {

    Collection<Operador> findAll();

    Optional<Operador> findById(Long id);

    Optional<Operador> findByUsuario(String usuario);

    void save(Operador operador);

    void deleteById(Long id);

    boolean existsById(Long id);

    // ── Métodos de lógica de negocio ──

    /**
     * Crea un nuevo operador con validación
     * @return Map con {"success": true, "operador": Operador} o {"success": false, "error": String}
     */
    Map<String, Object> createOperador(String usuario, String contrasena);

    /**
     * Actualiza un operador con validación
     * @return Map con {"success": true, "operador": Operador} o {"success": false, "error": String}
     */
    Map<String, Object> updateOperador(Long id, String usuario, String contrasena, String currentPassword);

    /**
     * Elimina un operador con validación
     * @return Map con {"success": true} o {"success": false, "error": String}
     */
    Map<String, Object> deleteOperador(Long id);

    /**
     * Valida las credenciales de un operador
     * @return true si las credenciales son correctas, false si no
     */
    boolean validateCredentials(String usuario, String contrasena);
}
