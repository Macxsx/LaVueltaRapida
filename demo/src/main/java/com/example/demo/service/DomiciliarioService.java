package com.example.demo.service;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;

import com.example.demo.entitys.Domiciliario;

public interface DomiciliarioService {

    Collection<Domiciliario> findAll();

    Optional<Domiciliario> findById(Long id);

    Optional<Domiciliario> findByCedula(String cedula);

    Optional<Domiciliario> findByCelular(String celular);

    void save(Domiciliario domiciliario);

    void deleteById(Long id);

    boolean existsById(Long id);

    // ── Métodos de lógica de negocio ──

    /**
     * Crea un nuevo domiciliario con validación
     * @return Map con {"success": true, "domiciliario": Domiciliario} o {"success": false, "error": String}
     */
    Map<String, Object> createDomiciliario(String nombre, String cedula, String celular, String ciudad, String direccion);

    /**
     * Actualiza un domiciliario con validación
     * @return Map con {"success": true, "domiciliario": Domiciliario} o {"success": false, "error": String}
     */
    Map<String, Object> updateDomiciliario(Long id, String nombre, String cedula, String celular, String ciudad, String direccion);

    /**
     * Elimina un domiciliario con validación
     * @return Map con {"success": true} o {"success": false, "error": String}
     */
    Map<String, Object> deleteDomiciliario(Long id);
}
