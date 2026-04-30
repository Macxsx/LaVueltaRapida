package com.example.demo.service;

import java.util.Optional;
import java.util.List;
import java.util.Map;

import com.example.demo.entitys.Carrito;
import com.example.demo.entitys.LineaPedido;

public interface CarritoService {

    Optional<Carrito> findById(Long id);

    Optional<Carrito> findByClienteId(Long clienteId);

    void save(Carrito carrito);

    boolean existsById(Long id);

    // ── Métodos de lógica de negocio ──

    /**
     * Agrega un producto al carrito
     * @param carritoId ID del carrito
     * @param comidaId ID de la comida
     * @param cantidad Cantidad a agregar
     * @param adicionalesIds IDs de los adicionales (puede ser null)
     * @return Map con {"success": true, "carrito": Carrito} o {"success": false, "error": String}
     */
    Map<String, Object> addProducto(Long carritoId, Long comidaId, Integer cantidad, List<Long> adicionalesIds);

    /**
     * Elimina un producto del carrito
     * @return Map con {"success": true, "carrito": Carrito} o {"success": false, "error": String}
     */
    Map<String, Object> removeProducto(Long carritoId, Long lineaPedidoId);

    /**
     * Aumenta la cantidad de un producto en el carrito
     * @return Map con {"success": true, "carrito": Carrito} o {"success": false, "error": String}
     */
    Map<String, Object> aumentarProducto(Long carritoId, Long lineaPedidoId);

    /**
     * Disminuye la cantidad de un producto en el carrito
     * @return Map con {"success": true, "carrito": Carrito} o {"success": false, "error": String}
     */
    Map<String, Object> disminuirProducto(Long carritoId, Long lineaPedidoId);

    /**
     * Vacía el carrito
     * @return Map con {"success": true, "carrito": Carrito} o {"success": false, "error": String}
     */
    Map<String, Object> vaciarCarrito(Long carritoId);
}
