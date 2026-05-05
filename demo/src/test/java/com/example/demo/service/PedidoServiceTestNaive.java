package com.example.demo.service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import com.example.demo.entitys.Carrito;
import com.example.demo.entitys.EstadoPedido;
import com.example.demo.entitys.LineaPedido;
import com.example.demo.entitys.MetodoPago;
import com.example.demo.entitys.Pedido;
import com.example.demo.repository.CarritoRepository;
import com.example.demo.repository.ComidaRepository;
import com.example.demo.repository.LineaPedidoRepository;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@ActiveProfiles("test")
public class PedidoServiceTestNaive {

    @Autowired private PedidoService        service;
    @Autowired private DomiciliarioService  domiciliarioService;
    @Autowired private CarritoRepository    carritoRepo;
    @Autowired private LineaPedidoRepository lineaPedidoRepo;
    @Autowired private ComidaRepository     comidaRepo;

    // ── findAll ───────────────────────────────────────────────────────────────

    @Test
    public void PedidoService_findAll_ReturnsPaginatedResults() {

        Page<Pedido> result = service.findAll(0, 10);

        Assertions.assertThat(result).isNotNull();
        Assertions.assertThat(result.getContent()).isNotEmpty();
        Assertions.assertThat(result.getContent().size()).isLessThanOrEqualTo(10);
    }

    @Test
    public void PedidoService_findAll_ClampsNegativePage() {

        Page<Pedido> resultNegative = service.findAll(-5, 10);
        Page<Pedido> resultZero    = service.findAll(0, 10);

        Assertions.assertThat(resultNegative.getNumber()).isEqualTo(resultZero.getNumber());
    }

    @Test
    public void PedidoService_findAll_ClampsSizeAbove100() {

        Page<Pedido> result = service.findAll(0, 200);

        Assertions.assertThat(result.getSize()).isEqualTo(100);
    }

    @Test
    public void PedidoService_findAll_ClampsZeroSize() {

        Page<Pedido> result = service.findAll(0, 0);

        Assertions.assertThat(result.getSize()).isEqualTo(20);
    }

    // ── findActivos ───────────────────────────────────────────────────────────

    @Test
    public void PedidoService_findActivos_ReturnsOnlyNonEntregados() {

        List<Pedido> result = service.findActivos();

        Assertions.assertThat(result).isNotEmpty();
        Assertions.assertThat(result).noneMatch(p -> p.getEstado() == EstadoPedido.ENTREGADO);
    }

    @Test
    public void PedidoService_findActivos_ReturnsOrderedByFechaCreacionAsc() {

        List<Pedido> result = service.findActivos();

        Assertions.assertThat(result).isSortedAccordingTo(
                (p1, p2) -> p1.getFechaCreacion().compareTo(p2.getFechaCreacion()));
    }

    // ── findById ──────────────────────────────────────────────────────────────

    @Test
    public void PedidoService_findById_ReturnsPresentWhenExists() {
        Long id = service.findAll(0, 1).getContent().get(0).getId();

        Optional<Pedido> result = service.findById(id);

        Assertions.assertThat(result).isPresent();
        Assertions.assertThat(result.get().getId()).isEqualTo(id);
    }

    @Test
    public void PedidoService_findById_ReturnsEmptyWhenNotExists() {

        Optional<Pedido> result = service.findById(999L);

        Assertions.assertThat(result).isNotPresent();
    }

    // ── findByClienteId ───────────────────────────────────────────────────────

    @Test
    public void PedidoService_findByClienteId_ReturnsPedidosForCliente() {
        Long clienteId = service.findActivos().get(0).getCliente().getId();

        List<Pedido> result = service.findByClienteId(clienteId);

        Assertions.assertThat(result).isNotEmpty();
        Assertions.assertThat(result).allMatch(p -> p.getCliente().getId().equals(clienteId));
    }

    @Test
    public void PedidoService_findByClienteId_ThrowsWhenClienteNotFound() {

        Assertions.assertThatThrownBy(() -> service.findByClienteId(999L))
                .isInstanceOf(NoSuchElementException.class);
    }

    // ── desdeCarrito ──────────────────────────────────────────────────────────

    @Test
    public void PedidoService_desdeCarrito_ThrowsWhenCarritoNotFound() {

        Assertions.assertThatThrownBy(() -> service.desdeCarrito(999L, null))
                .isInstanceOf(NoSuchElementException.class);
    }

    @Test
    public void PedidoService_desdeCarrito_ThrowsWhenCarritoEmpty() {
        Long carritoId = carritoRepo.findAll().get(0).getId();

        Assertions.assertThatThrownBy(() -> service.desdeCarrito(carritoId, null))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void PedidoService_desdeCarrito_CreatesPedidoSuccessfully() {
        Carrito carrito = carritoRepo.findAll().get(0);
        LineaPedido linea = new LineaPedido(1, comidaRepo.findAll().get(0), carrito, null);
        lineaPedidoRepo.save(linea);

        Pedido result = service.desdeCarrito(carrito.getId(), null);

        Assertions.assertThat(result.getId()).isNotNull();
        Assertions.assertThat(result.getEstado()).isEqualTo(EstadoPedido.RECIBIDO);
        Assertions.assertThat(result.getCliente()).isEqualTo(carrito.getCliente());
    }

    // ── actualizarEstado ──────────────────────────────────────────────────────

    @Test
    public void PedidoService_actualizarEstado_AdvancesRecibidoToCocinando() {
        Pedido recibido = service.findActivos().stream()
                .filter(p -> p.getEstado() == EstadoPedido.RECIBIDO)
                .findFirst().get();

        Pedido result = service.actualizarEstado(recibido.getId(), "COCINANDO");

        Assertions.assertThat(result.getEstado()).isEqualTo(EstadoPedido.COCINANDO);
    }

    @Test
    public void PedidoService_actualizarEstado_AdvancesCocinandoToEnviado() {
        Pedido cocinando = service.findActivos().stream()
                .filter(p -> p.getEstado() == EstadoPedido.COCINANDO)
                .findFirst().get();

        Pedido result = service.actualizarEstado(cocinando.getId(), "ENVIADO");

        Assertions.assertThat(result.getEstado()).isEqualTo(EstadoPedido.ENVIADO);
        Assertions.assertThat(result.getDomiciliario()).isNotNull();
    }

    @Test
    public void PedidoService_actualizarEstado_AdvancesEnviadoToEntregado() {
        Pedido enviado = service.findActivos().stream()
                .filter(p -> p.getEstado() == EstadoPedido.ENVIADO)
                .findFirst().get();
        service.confirmarPresencial(enviado.getId(), "EFECTIVO");
        service.confirmarPago(enviado.getId());

        Pedido result = service.actualizarEstado(enviado.getId(), "ENTREGADO");

        Assertions.assertThat(result.getEstado()).isEqualTo(EstadoPedido.ENTREGADO);
        Assertions.assertThat(result.getFechaEntrega()).isNotNull();
    }

    @Test
    public void PedidoService_actualizarEstado_ThrowsWhenEstadoIsNull() {
        Long id = service.findAll(0, 1).getContent().get(0).getId();

        Assertions.assertThatThrownBy(() -> service.actualizarEstado(id, null))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void PedidoService_actualizarEstado_ThrowsWhenEstadoInvalido() {
        Long id = service.findAll(0, 1).getContent().get(0).getId();

        Assertions.assertThatThrownBy(() -> service.actualizarEstado(id, "VOLANDO"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void PedidoService_actualizarEstado_ThrowsWhenTransicionInvalida() {
        Pedido recibido = service.findActivos().stream()
                .filter(p -> p.getEstado() == EstadoPedido.RECIBIDO)
                .findFirst().get();

        Assertions.assertThatThrownBy(() -> service.actualizarEstado(recibido.getId(), "ENVIADO"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void PedidoService_actualizarEstado_ThrowsWhenNoDomiciliariosDisponibles() {
        domiciliarioService.findAll()
                .forEach(d -> { d.setDisponible(false); domiciliarioService.save(d); });
        Pedido cocinando = service.findActivos().stream()
                .filter(p -> p.getEstado() == EstadoPedido.COCINANDO)
                .findFirst().get();

        Assertions.assertThatThrownBy(() -> service.actualizarEstado(cocinando.getId(), "ENVIADO"))
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    public void PedidoService_actualizarEstado_ThrowsWhenPedidoNotFound() {

        Assertions.assertThatThrownBy(() -> service.actualizarEstado(999L, "COCINANDO"))
                .isInstanceOf(NoSuchElementException.class);
    }

    @Test
    public void PedidoService_actualizarEstado_ThrowsWhenPagoNoAprobadoAlPasarAEntregado() {
        Pedido enviado = service.findActivos().stream()
                .filter(p -> p.getEstado() == EstadoPedido.ENVIADO)
                .findFirst().get();
        // no se llama confirmarPago → estadoPago queda en "PENDIENTE"

        Assertions.assertThatThrownBy(() -> service.actualizarEstado(enviado.getId(), "ENTREGADO"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    // ── confirmarPresencial ───────────────────────────────────────────────────

    @Test
    public void PedidoService_confirmarPresencial_ThrowsWhenPedidoNotFound() {

        Assertions.assertThatThrownBy(() -> service.confirmarPresencial(999L, "EFECTIVO"))
                .isInstanceOf(NoSuchElementException.class);
    }

    @Test
    public void PedidoService_confirmarPresencial_ThrowsWhenMetodoPagoIsNull() {

        Assertions.assertThatThrownBy(() -> service.confirmarPresencial(1L, null))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void PedidoService_confirmarPresencial_ThrowsWhenMetodoPagoIsBlank() {

        Assertions.assertThatThrownBy(() -> service.confirmarPresencial(1L, "   "))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void PedidoService_confirmarPresencial_ThrowsWhenMetodoPagoInvalido() {

        Assertions.assertThatThrownBy(() -> service.confirmarPresencial(1L, "TELEPATICO"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void PedidoService_confirmarPresencial_ThrowsWhenMetodoPagoNoEsPresencial() {

        Assertions.assertThatThrownBy(() -> service.confirmarPresencial(1L, "MP_ONLINE"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void PedidoService_confirmarPresencial_SetsMetodoPagoYEstadoPago() {
        Long id = service.findActivos().stream()
                .filter(p -> p.getEstado() == EstadoPedido.RECIBIDO)
                .findFirst().get().getId();

        Pedido result = service.confirmarPresencial(id, "NEQUI");

        Assertions.assertThat(result.getMetodoPago()).isEqualTo(MetodoPago.NEQUI);
        Assertions.assertThat(result.getEstadoPago()).isEqualTo("PENDIENTE_DE_PAGO");
    }

    // ── confirmarPago ─────────────────────────────────────────────────────────

    @Test
    public void PedidoService_confirmarPago_ThrowsWhenPedidoNotFound() {

        Assertions.assertThatThrownBy(() -> service.confirmarPago(999L))
                .isInstanceOf(NoSuchElementException.class);
    }

    @Test
    public void PedidoService_confirmarPago_ThrowsWhenMetodoPagoIsNotPresencial() {
        // Los pedidos sembrados sin metodoPago (null) deben lanzar IllegalArgumentException
        Long id = service.findActivos().stream()
                .filter(p -> p.getEstado() == EstadoPedido.RECIBIDO)
                .findFirst().get().getId();

        Assertions.assertThatThrownBy(() -> service.confirmarPago(id))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void PedidoService_confirmarPago_ThrowsWhenYaPagado() {
        Long id = service.findActivos().stream()
                .filter(p -> p.getEstado() == EstadoPedido.ENVIADO)
                .findFirst().get().getId();
        service.confirmarPresencial(id, "EFECTIVO");
        service.confirmarPago(id);

        Assertions.assertThatThrownBy(() -> service.confirmarPago(id))
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    public void PedidoService_confirmarPago_MarksAsAprobadoAndSetsFechaPago() {
        Long id = service.findActivos().stream()
                .filter(p -> p.getEstado() == EstadoPedido.ENVIADO)
                .findFirst().get().getId();
        service.confirmarPresencial(id, "EFECTIVO");

        Pedido result = service.confirmarPago(id);

        Assertions.assertThat(result.getEstadoPago()).isEqualTo("APROBADO");
        Assertions.assertThat(result.getFechaPago()).isNotNull();
        Assertions.assertThat(result.getTotalPagado()).isNotNull();
    }
}
