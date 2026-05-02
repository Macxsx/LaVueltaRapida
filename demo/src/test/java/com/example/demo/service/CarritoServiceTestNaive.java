package com.example.demo.service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import com.example.demo.entitys.Carrito;
import com.example.demo.entitys.LineaPedido;
import com.example.demo.repository.CarritoRepository;
import com.example.demo.repository.ComidaRepository;
import com.example.demo.repository.LineaPedidoRepository;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@ActiveProfiles("test")
public class CarritoServiceTestNaive {

    @Autowired private CarritoService        service;
    @Autowired private CarritoRepository     carritoRepo;
    @Autowired private ComidaRepository      comidaRepo;
    @Autowired private LineaPedidoRepository lineaPedidoRepo;

    // ── findById ──────────────────────────────────────────────────────────────

    @Test
    public void CarritoService_findById_ReturnsPresentWhenExists() {
        Long id = carritoRepo.findAll().get(0).getId();

        Optional<Carrito> result = service.findById(id);

        Assertions.assertThat(result).isPresent();
        Assertions.assertThat(result.get().getId()).isEqualTo(id);
    }

    @Test
    public void CarritoService_findById_ReturnsEmptyWhenNotExists() {

        Optional<Carrito> result = service.findById(999L);

        Assertions.assertThat(result).isNotPresent();
    }

    // ── findByClienteId ───────────────────────────────────────────────────────

    @Test
    public void CarritoService_findByClienteId_ReturnsPresentWhenExists() {
        Long clienteId = carritoRepo.findAll().get(0).getCliente().getId();

        Optional<Carrito> result = service.findByClienteId(clienteId);

        Assertions.assertThat(result).isPresent();
        Assertions.assertThat(result.get().getCliente().getId()).isEqualTo(clienteId);
    }

    @Test
    public void CarritoService_findByClienteId_ReturnsEmptyWhenNotExists() {

        Optional<Carrito> result = service.findByClienteId(999L);

        Assertions.assertThat(result).isNotPresent();
    }

    // ── save ──────────────────────────────────────────────────────────────────

    @Test
    public void CarritoService_save_PersistsChanges() {
        Carrito carrito = carritoRepo.findAll().get(0);
        carrito.setActivo(true);

        Carrito result = service.save(carrito);

        Assertions.assertThat(result.isActivo()).isTrue();
    }

    // ── addProducto ───────────────────────────────────────────────────────────

    @Test
    public void CarritoService_addProducto_AddsNewLineaToCarrito() {
        Long carritoId = carritoRepo.findAll().get(0).getId();
        Long comidaId  = comidaRepo.findAll().get(0).getId();

        service.addProducto(carritoId, comidaId, 2, null);

        List<LineaPedido> lineas = lineaPedidoRepo.findByCarritoId(carritoId);
        Assertions.assertThat(lineas).hasSize(1);
        Assertions.assertThat(lineas.get(0).getCantidad()).isEqualTo(2);
    }

    @Test
    public void CarritoService_addProducto_IncrementsCantidadForSameComidaAndAdicionales() {
        Long carritoId = carritoRepo.findAll().get(0).getId();
        Long comidaId  = comidaRepo.findAll().get(0).getId();
        service.addProducto(carritoId, comidaId, 2, null);

        service.addProducto(carritoId, comidaId, 3, null);

        List<LineaPedido> lineas = lineaPedidoRepo.findByCarritoId(carritoId);
        Assertions.assertThat(lineas).hasSize(1);
        Assertions.assertThat(lineas.get(0).getCantidad()).isEqualTo(5);
    }

    @Test
    public void CarritoService_addProducto_ThrowsWhenComidaIdIsNull() {
        Long carritoId = carritoRepo.findAll().get(0).getId();

        Assertions.assertThatThrownBy(() -> service.addProducto(carritoId, null, 1, null))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void CarritoService_addProducto_ThrowsWhenCantidadIsZero() {
        Long carritoId = carritoRepo.findAll().get(0).getId();
        Long comidaId  = comidaRepo.findAll().get(0).getId();

        Assertions.assertThatThrownBy(() -> service.addProducto(carritoId, comidaId, 0, null))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void CarritoService_addProducto_ThrowsWhenCarritoNotFound() {
        Long comidaId = comidaRepo.findAll().get(0).getId();

        Assertions.assertThatThrownBy(() -> service.addProducto(999L, comidaId, 1, null))
                .isInstanceOf(NoSuchElementException.class);
    }

    @Test
    public void CarritoService_addProducto_ThrowsWhenComidaNotFound() {
        Long carritoId = carritoRepo.findAll().get(0).getId();

        Assertions.assertThatThrownBy(() -> service.addProducto(carritoId, 999L, 1, null))
                .isInstanceOf(NoSuchElementException.class);
    }

    // ── removeProducto ────────────────────────────────────────────────────────

    @Test
    public void CarritoService_removeProducto_RemovesLineaFromCarrito() {
        Long carritoId = carritoRepo.findAll().get(0).getId();
        Long comidaId  = comidaRepo.findAll().get(0).getId();
        service.addProducto(carritoId, comidaId, 1, null);
        Long lineaId = lineaPedidoRepo.findByCarritoId(carritoId).get(0).getId();

        service.removeProducto(carritoId, lineaId);

        Assertions.assertThat(lineaPedidoRepo.findByCarritoId(carritoId)).isEmpty();
    }

    @Test
    public void CarritoService_removeProducto_ThrowsWhenCarritoNotFound() {

        Assertions.assertThatThrownBy(() -> service.removeProducto(999L, 1L))
                .isInstanceOf(NoSuchElementException.class);
    }

    @Test
    public void CarritoService_removeProducto_ThrowsWhenLineaNotFound() {
        Long carritoId = carritoRepo.findAll().get(0).getId();

        Assertions.assertThatThrownBy(() -> service.removeProducto(carritoId, 999L))
                .isInstanceOf(NoSuchElementException.class);
    }

    // ── aumentarCantidad ──────────────────────────────────────────────────────

    @Test
    public void CarritoService_aumentarCantidad_IncreasesByOne() {
        Long carritoId = carritoRepo.findAll().get(0).getId();
        Long comidaId  = comidaRepo.findAll().get(0).getId();
        service.addProducto(carritoId, comidaId, 2, null);
        Long lineaId = lineaPedidoRepo.findByCarritoId(carritoId).get(0).getId();

        service.aumentarCantidad(carritoId, lineaId);

        Assertions.assertThat(lineaPedidoRepo.findByCarritoId(carritoId).get(0).getCantidad()).isEqualTo(3);
    }

    @Test
    public void CarritoService_aumentarCantidad_ThrowsWhenCarritoNotFound() {

        Assertions.assertThatThrownBy(() -> service.aumentarCantidad(999L, 1L))
                .isInstanceOf(NoSuchElementException.class);
    }

    @Test
    public void CarritoService_aumentarCantidad_ThrowsWhenLineaNotFound() {
        Long carritoId = carritoRepo.findAll().get(0).getId();

        Assertions.assertThatThrownBy(() -> service.aumentarCantidad(carritoId, 999L))
                .isInstanceOf(NoSuchElementException.class);
    }

    // ── disminuirCantidad ─────────────────────────────────────────────────────

    @Test
    public void CarritoService_disminuirCantidad_DecreasesByOne() {
        Long carritoId = carritoRepo.findAll().get(0).getId();
        Long comidaId  = comidaRepo.findAll().get(0).getId();
        service.addProducto(carritoId, comidaId, 3, null);
        Long lineaId = lineaPedidoRepo.findByCarritoId(carritoId).get(0).getId();

        service.disminuirCantidad(carritoId, lineaId);

        Assertions.assertThat(lineaPedidoRepo.findByCarritoId(carritoId).get(0).getCantidad()).isEqualTo(2);
    }

    @Test
    public void CarritoService_disminuirCantidad_RemovesLineaWhenReachesZero() {
        Long carritoId = carritoRepo.findAll().get(0).getId();
        Long comidaId  = comidaRepo.findAll().get(0).getId();
        service.addProducto(carritoId, comidaId, 1, null);
        Long lineaId = lineaPedidoRepo.findByCarritoId(carritoId).get(0).getId();

        service.disminuirCantidad(carritoId, lineaId);

        Assertions.assertThat(lineaPedidoRepo.findByCarritoId(carritoId)).isEmpty();
    }

    @Test
    public void CarritoService_disminuirCantidad_ThrowsWhenCarritoNotFound() {

        Assertions.assertThatThrownBy(() -> service.disminuirCantidad(999L, 1L))
                .isInstanceOf(NoSuchElementException.class);
    }

    @Test
    public void CarritoService_disminuirCantidad_ThrowsWhenLineaNotFound() {
        Long carritoId = carritoRepo.findAll().get(0).getId();

        Assertions.assertThatThrownBy(() -> service.disminuirCantidad(carritoId, 999L))
                .isInstanceOf(NoSuchElementException.class);
    }

    // ── vaciar ────────────────────────────────────────────────────────────────

    @Test
    public void CarritoService_vaciar_EmptiesCarrito() {
        Long carritoId = carritoRepo.findAll().get(0).getId();
        Long comidaId  = comidaRepo.findAll().get(0).getId();
        service.addProducto(carritoId, comidaId, 2, null);

        service.vaciar(carritoId);

        Assertions.assertThat(lineaPedidoRepo.findByCarritoId(carritoId)).isEmpty();
    }

    @Test
    public void CarritoService_vaciar_ThrowsWhenCarritoNotFound() {

        Assertions.assertThatThrownBy(() -> service.vaciar(999L))
                .isInstanceOf(NoSuchElementException.class);
    }
}
