package com.example.demo.service;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;

import com.example.demo.entitys.Pedido;

public interface PedidoService {

    /** Clampea page y size antes de paginar (page ≥ 0, 1 ≤ size ≤ 100). */
    Page<Pedido> findAll(int page, int size);

    List<Pedido> findActivos();

    Optional<Pedido> findById(Long id);

    /** Lanza NoSuchElementException si el cliente no existe. */
    List<Pedido> findByClienteId(Long clienteId);

    /**
     * Crea un pedido a partir del carrito.
     * @param metodoPago método de pago elegido por el cliente (puede ser null si se define después).
     * Lanza NoSuchElementException si el carrito no existe.
     * Lanza IllegalArgumentException si el carrito está vacío.
     */
    Pedido desdeCarrito(Long carritoId, String metodoPago);

    /**
     * Registra el método de pago presencial en un pedido existente y marca estadoPago = "PENDIENTE_DE_PAGO".
     * Lanza NoSuchElementException si el pedido no existe.
     * Lanza IllegalArgumentException si metodoPago es nulo o no es un método presencial válido.
     */
    Pedido confirmarPresencial(Long id, String metodoPago);

    /**
     * Marca el pago de un pedido contra entrega como aprobado.
     * Lanza NoSuchElementException si el pedido no existe.
     * Lanza IllegalArgumentException si el pedido no tiene un método de pago contra entrega.
     * Lanza IllegalStateException si el pedido ya está pagado.
     */
    Pedido confirmarPago(Long id);

    /**
     * Lanza NoSuchElementException si el pedido no existe.
     * Lanza IllegalArgumentException si estadoStr es nulo, inválido o la transición no está permitida.
     * Lanza IllegalStateException si no hay domiciliarios disponibles al pasar a ENVIADO.
     */
    Pedido actualizarEstado(Long id, String estadoStr);

    /**
     * Cancela el pedido por rechazo del sistema de pagos. No-op si ya está en estado terminal.
     * Libera el domiciliario asignado si lo hay.
     */
    void cancelarPorPago(Long pedidoId);
}
