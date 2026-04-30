package com.example.demo.service;

import java.util.Collection;

import com.example.demo.entitys.Cliente;

public interface ClienteService {

    Collection<Cliente> findAll();

    /** Lanza NoSuchElementException si no existe. */
    Cliente findById(Long id);

    Cliente findByUsername(String username);

    /** Lanza IllegalStateException si el usuario o email ya están en uso. */
    Cliente create(Cliente cliente);

    /**
     * Lanza NoSuchElementException si no existe.
     * Lanza SecurityException si currentPassword es incorrecto.
     * Lanza IllegalStateException si el nuevo usuario o email ya pertenecen a otro cliente.
     */
    Cliente update(Long id, String name, String apellido, String email, String username,
                   String password, String direccion, String telefono, String currentPassword);

    /**
     * Lanza IllegalArgumentException si username o password son nulos.
     * Lanza SecurityException si las credenciales son incorrectas.
     * Lanza IllegalStateException si la cuenta está desactivada.
     */
    Cliente loginCliente(String username, String password);

    /**
     * Lanza NoSuchElementException si no existe.
     * Lanza IllegalStateException si tiene pedidos activos.
     * Lanza CuentaDesactivadaException si la cuenta fue desactivada en lugar de eliminada.
     */
    void deleteOrDeactivate(Long id);
}
