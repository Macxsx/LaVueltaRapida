package com.example.demo.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;

import com.example.demo.entitys.Adicional;
import com.example.demo.entitys.Carrito;
import com.example.demo.entitys.Cliente;
import com.example.demo.entitys.Comida;
import com.example.demo.entitys.Domiciliario;
import com.example.demo.entitys.EstadoPedido;
import com.example.demo.entitys.LineaPedido;
import com.example.demo.entitys.LineaPedidoAdicional;
import com.example.demo.entitys.MetodoPago;
import com.example.demo.entitys.Pedido;
import com.example.demo.repository.CarritoRepository;
import com.example.demo.repository.ClienteRepository;
import com.example.demo.repository.PedidoRepository;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
public class PedidoServiceTestMock {

    @InjectMocks
    private PedidoServiceImpl service;

    @Mock
    PedidoRepository pedidoRepository;

    @Mock
    CarritoRepository carritoRepository;

    @Mock
    ClienteRepository clienteRepository;

    @Mock
    DomiciliarioService domiciliarioService;


    private Pedido buildPedido(Long id, EstadoPedido estado, Domiciliario domi) {
        Pedido p = new Pedido();
        p.setId(id);
        p.setEstado(estado);
        p.setFechaCreacion(LocalDateTime.now());
        p.setDomiciliario(domi);
        return p;
    }


    @Test
    public void PedidoService_findAll_DelegatesPageableWithSortDescByFechaCreacion() {
        Page<Pedido> page = new PageImpl<>(List.of(buildPedido(1L, EstadoPedido.RECIBIDO, null)));
        when(pedidoRepository.findAll(any(Pageable.class))).thenReturn(page);

        Page<Pedido> result = service.findAll(0, 10);

        Assertions.assertThat(result).isSameAs(page);
        ArgumentCaptor<Pageable> captor = ArgumentCaptor.forClass(Pageable.class);
        verify(pedidoRepository).findAll(captor.capture());
        Pageable used = captor.getValue();
        Assertions.assertThat(used.getPageNumber()).isEqualTo(0);
        Assertions.assertThat(used.getPageSize()).isEqualTo(10);
        Assertions.assertThat(used.getSort().getOrderFor("fechaCreacion")).isNotNull();
        Assertions.assertThat(used.getSort().getOrderFor("fechaCreacion").isDescending()).isTrue();
    }

    @Test
    public void PedidoService_findAll_ClampsNegativePageToZero() {
        when(pedidoRepository.findAll(any(Pageable.class))).thenReturn(Page.empty());

        service.findAll(-5, 10);

        ArgumentCaptor<Pageable> captor = ArgumentCaptor.forClass(Pageable.class);
        verify(pedidoRepository).findAll(captor.capture());
        Assertions.assertThat(captor.getValue().getPageNumber()).isEqualTo(0);
    }

    @Test
    public void PedidoService_findAll_ClampsZeroOrNegativeSizeToTwenty() {
        when(pedidoRepository.findAll(any(Pageable.class))).thenReturn(Page.empty());

        service.findAll(0, 0);
        service.findAll(0, -3);

        ArgumentCaptor<Pageable> captor = ArgumentCaptor.forClass(Pageable.class);
        verify(pedidoRepository, org.mockito.Mockito.times(2)).findAll(captor.capture());
        Assertions.assertThat(captor.getAllValues().get(0).getPageSize()).isEqualTo(20);
        Assertions.assertThat(captor.getAllValues().get(1).getPageSize()).isEqualTo(20);
    }

    @Test
    public void PedidoService_findAll_ClampsSizeAboveOneHundredToOneHundred() {
        when(pedidoRepository.findAll(any(Pageable.class))).thenReturn(Page.empty());

        service.findAll(0, 999);

        ArgumentCaptor<Pageable> captor = ArgumentCaptor.forClass(Pageable.class);
        verify(pedidoRepository).findAll(captor.capture());
        Assertions.assertThat(captor.getValue().getPageSize()).isEqualTo(100);
    }


    @Test
    public void PedidoService_findActivos_DelegatesUsingEntregadoAsExclusion() {
        List<Pedido> activos = List.of(
                buildPedido(1L, EstadoPedido.RECIBIDO, null),
                buildPedido(2L, EstadoPedido.COCINANDO, null)
        );
        when(pedidoRepository.findByEstadoNotOrderByFechaCreacionAsc(EstadoPedido.ENTREGADO))
                .thenReturn(activos);

        List<Pedido> result = service.findActivos();

        Assertions.assertThat(result).hasSize(2);
    }

    @Test
    public void PedidoService_findById_ReturnsPedidoWhenExists() {
        Pedido p = buildPedido(1L, EstadoPedido.RECIBIDO, null);
        when(pedidoRepository.findById(1L)).thenReturn(Optional.of(p));

        Optional<Pedido> result = service.findById(1L);

        Assertions.assertThat(result).isPresent();
    }

    @Test
    public void PedidoService_findById_ReturnsEmptyWhenNotExists() {
        when(pedidoRepository.findById(999L)).thenReturn(Optional.empty());

        Optional<Pedido> result = service.findById(999L);

        Assertions.assertThat(result).isEmpty();
    }


    @Test
    public void PedidoService_findByClienteId_ReturnsPedidosWhenClienteExists() {
        when(clienteRepository.existsById(1L)).thenReturn(true);
        when(pedidoRepository.findByClienteIdOrderByFechaCreacionDesc(1L))
                .thenReturn(List.of(buildPedido(1L, EstadoPedido.RECIBIDO, null)));

        List<Pedido> result = service.findByClienteId(1L);

        Assertions.assertThat(result).hasSize(1);
    }

    @Test
    public void PedidoService_findByClienteId_ThrowsWhenClienteDoesNotExist() {
        when(clienteRepository.existsById(999L)).thenReturn(false);

        Assertions.assertThatThrownBy(() -> service.findByClienteId(999L))
                .isInstanceOf(NoSuchElementException.class);

        verify(pedidoRepository, never()).findByClienteIdOrderByFechaCreacionDesc(999L);
    }


    @Test
    public void PedidoService_desdeCarrito_CreatesPedidoCopiesLineasAndClearsCarrito() {
        Cliente cliente = new Cliente();
        cliente.setId(7L);
        Carrito carrito = new Carrito(cliente);
        carrito.setId(1L);

        Comida comida = new Comida();
        comida.setId(10L);
        Adicional adicional = new Adicional(5L, "Queso", 3000.0, true);
        LineaPedido carritoLinea = new LineaPedido(2, comida, carrito, null);
        carritoLinea.getAdicionales().add(new LineaPedidoAdicional(carritoLinea, adicional));
        carrito.getLineasPedido().add(carritoLinea);

        when(carritoRepository.findById(1L)).thenReturn(Optional.of(carrito));
        when(pedidoRepository.save(any(Pedido.class))).thenAnswer(inv -> {
            Pedido p = inv.getArgument(0);
            p.setId(99L);
            return p;
        });

        Pedido result = service.desdeCarrito(1L, null);

        Assertions.assertThat(result.getId()).isEqualTo(99L);
        Assertions.assertThat(result.getEstado()).isEqualTo(EstadoPedido.RECIBIDO);
        Assertions.assertThat(result.getFechaCreacion()).isNotNull();
        Assertions.assertThat(result.getCliente()).isSameAs(cliente);
        Assertions.assertThat(result.getLineasPedido()).hasSize(1);

        LineaPedido nueva = result.getLineasPedido().get(0);
        Assertions.assertThat(nueva.getComida()).isSameAs(comida);
        Assertions.assertThat(nueva.getCantidad()).isEqualTo(2);
        Assertions.assertThat(nueva.getPedido()).isSameAs(result);
        Assertions.assertThat(nueva.getAdicionales()).hasSize(1);
        Assertions.assertThat(nueva.getAdicionales().get(0).getAdicional()).isSameAs(adicional);
        Assertions.assertThat(nueva.getAdicionales().get(0).getLineaPedido()).isSameAs(nueva);

        Assertions.assertThat(carrito.getLineasPedido()).isEmpty();
        verify(carritoRepository).save(carrito);
    }

    @Test
    public void PedidoService_desdeCarrito_ThrowsWhenCarritoDoesNotExist() {
        when(carritoRepository.findById(999L)).thenReturn(Optional.empty());

        Assertions.assertThatThrownBy(() -> service.desdeCarrito(999L, null))
                .isInstanceOf(NoSuchElementException.class);

        verify(pedidoRepository, never()).save(any(Pedido.class));
    }

    @Test
    public void PedidoService_desdeCarrito_ThrowsWhenCarritoIsEmpty() {
        Carrito carrito = new Carrito();
        carrito.setId(1L);
        carrito.setLineasPedido(new ArrayList<>());
        when(carritoRepository.findById(1L)).thenReturn(Optional.of(carrito));

        Assertions.assertThatThrownBy(() -> service.desdeCarrito(1L, null))
                .isInstanceOf(IllegalArgumentException.class);

        verify(pedidoRepository, never()).save(any(Pedido.class));
    }


    @Test
    public void PedidoService_actualizarEstado_AdvancesFromRecibidoToCocinando() {
        Pedido pedido = buildPedido(1L, EstadoPedido.RECIBIDO, null);
        when(pedidoRepository.findById(1L)).thenReturn(Optional.of(pedido));
        when(pedidoRepository.save(pedido)).thenReturn(pedido);

        Pedido result = service.actualizarEstado(1L, "cocinando");

        Assertions.assertThat(result.getEstado()).isEqualTo(EstadoPedido.COCINANDO);
        Assertions.assertThat(result.getDomiciliario()).isNull();
    }

    @Test
    public void PedidoService_actualizarEstado_AssignsDomiciliarioWhenAdvancingToEnviado() {
        Pedido pedido = buildPedido(1L, EstadoPedido.COCINANDO, null);
        Domiciliario domi = new Domiciliario(5L, "Juan", "111", "3001", true);

        when(pedidoRepository.findById(1L)).thenReturn(Optional.of(pedido));
        when(domiciliarioService.findFirstDisponible()).thenReturn(Optional.of(domi));
        when(pedidoRepository.save(pedido)).thenReturn(pedido);

        Pedido result = service.actualizarEstado(1L, "ENVIADO");

        Assertions.assertThat(result.getEstado()).isEqualTo(EstadoPedido.ENVIADO);
        Assertions.assertThat(result.getDomiciliario()).isSameAs(domi);
        Assertions.assertThat(domi.isDisponible()).isFalse();
        verify(domiciliarioService).save(domi);
    }

    @Test
    public void PedidoService_actualizarEstado_ThrowsWhenAdvancingToEnviadoAndNoDomiciliarioAvailable() {
        Pedido pedido = buildPedido(1L, EstadoPedido.COCINANDO, null);
        when(pedidoRepository.findById(1L)).thenReturn(Optional.of(pedido));
        when(domiciliarioService.findFirstDisponible()).thenReturn(Optional.empty());

        Assertions.assertThatThrownBy(() -> service.actualizarEstado(1L, "ENVIADO"))
                .isInstanceOf(IllegalStateException.class);

        Assertions.assertThat(pedido.getEstado()).isEqualTo(EstadoPedido.COCINANDO);
        verify(pedidoRepository, never()).save(any(Pedido.class));
    }

    @Test
    public void PedidoService_actualizarEstado_ReleasesDomiciliarioAndSetsFechaEntregaWhenAdvancingToEntregado() {
        Domiciliario domi = new Domiciliario(5L, "Juan", "111", "3001", false);
        Pedido pedido = buildPedido(1L, EstadoPedido.ENVIADO, domi);
        pedido.setEstadoPago("APROBADO");

        when(pedidoRepository.findById(1L)).thenReturn(Optional.of(pedido));
        when(pedidoRepository.save(pedido)).thenReturn(pedido);

        Pedido result = service.actualizarEstado(1L, "ENTREGADO");

        Assertions.assertThat(result.getEstado()).isEqualTo(EstadoPedido.ENTREGADO);
        Assertions.assertThat(result.getDomiciliario()).isNull();
        Assertions.assertThat(result.getFechaEntrega()).isNotNull();
        Assertions.assertThat(domi.isDisponible()).isTrue();
        verify(domiciliarioService).save(domi);
    }

    @Test
    public void PedidoService_actualizarEstado_ThrowsWhenEstadoStrIsNull() {
        Assertions.assertThatThrownBy(() -> service.actualizarEstado(1L, null))
                .isInstanceOf(IllegalArgumentException.class);

        verify(pedidoRepository, never()).findById(any());
    }

    @Test
    public void PedidoService_actualizarEstado_ThrowsWhenPedidoDoesNotExist() {
        when(pedidoRepository.findById(999L)).thenReturn(Optional.empty());

        Assertions.assertThatThrownBy(() -> service.actualizarEstado(999L, "COCINANDO"))
                .isInstanceOf(NoSuchElementException.class);
    }

    @Test
    public void PedidoService_actualizarEstado_ThrowsWhenEstadoStrIsInvalid() {
        Pedido pedido = buildPedido(1L, EstadoPedido.RECIBIDO, null);
        when(pedidoRepository.findById(1L)).thenReturn(Optional.of(pedido));

        Assertions.assertThatThrownBy(() -> service.actualizarEstado(1L, "INEXISTENTE"))
                .isInstanceOf(IllegalArgumentException.class);

        verify(pedidoRepository, never()).save(any(Pedido.class));
    }

    @Test
    public void PedidoService_actualizarEstado_ThrowsOnInvalidTransition() {
        Pedido pedido = buildPedido(1L, EstadoPedido.RECIBIDO, null);
        when(pedidoRepository.findById(1L)).thenReturn(Optional.of(pedido));

        Assertions.assertThatThrownBy(() -> service.actualizarEstado(1L, "ENVIADO"))
                .isInstanceOf(IllegalArgumentException.class);

        verify(pedidoRepository, never()).save(any(Pedido.class));
    }

    @Test
    public void PedidoService_actualizarEstado_ThrowsWhenAlreadyEntregado() {
        Pedido pedido = buildPedido(1L, EstadoPedido.ENTREGADO, null);
        when(pedidoRepository.findById(1L)).thenReturn(Optional.of(pedido));

        Assertions.assertThatThrownBy(() -> service.actualizarEstado(1L, "ENTREGADO"))
                .isInstanceOf(IllegalArgumentException.class);

        verify(pedidoRepository, never()).save(any(Pedido.class));
    }

    @Test
    public void PedidoService_actualizarEstado_ThrowsWhenPagoNoAprobadoAlPasarAEntregado() {
        Domiciliario domi = new Domiciliario(5L, "Juan", "111", "3001", false);
        Pedido pedido = buildPedido(1L, EstadoPedido.ENVIADO, domi);
        // estadoPago queda en "PENDIENTE" (default), no "APROBADO"
        when(pedidoRepository.findById(1L)).thenReturn(Optional.of(pedido));

        Assertions.assertThatThrownBy(() -> service.actualizarEstado(1L, "ENTREGADO"))
                .isInstanceOf(IllegalArgumentException.class);

        verify(pedidoRepository, never()).save(any(Pedido.class));
    }


    // ── confirmarPresencial ───────────────────────────────────────────────────

    @Test
    public void PedidoService_confirmarPresencial_ThrowsWhenMetodoPagoIsNull() {
        Assertions.assertThatThrownBy(() -> service.confirmarPresencial(1L, null))
                .isInstanceOf(IllegalArgumentException.class);

        verify(pedidoRepository, never()).findById(any());
    }

    @Test
    public void PedidoService_confirmarPresencial_ThrowsWhenMetodoPagoIsBlank() {
        Assertions.assertThatThrownBy(() -> service.confirmarPresencial(1L, "   "))
                .isInstanceOf(IllegalArgumentException.class);

        verify(pedidoRepository, never()).findById(any());
    }

    @Test
    public void PedidoService_confirmarPresencial_ThrowsWhenMetodoPagoInvalido() {
        Assertions.assertThatThrownBy(() -> service.confirmarPresencial(1L, "TELEPATICO"))
                .isInstanceOf(IllegalArgumentException.class);

        verify(pedidoRepository, never()).findById(any());
    }

    @Test
    public void PedidoService_confirmarPresencial_ThrowsWhenMetodoPagoNoEsPresencial() {
        Assertions.assertThatThrownBy(() -> service.confirmarPresencial(1L, "MP_ONLINE"))
                .isInstanceOf(IllegalArgumentException.class);

        verify(pedidoRepository, never()).findById(any());
    }

    @Test
    public void PedidoService_confirmarPresencial_ThrowsWhenPedidoNotFound() {
        when(pedidoRepository.findById(999L)).thenReturn(Optional.empty());

        Assertions.assertThatThrownBy(() -> service.confirmarPresencial(999L, "EFECTIVO"))
                .isInstanceOf(NoSuchElementException.class);

        verify(pedidoRepository, never()).save(any(Pedido.class));
    }

    @Test
    public void PedidoService_confirmarPresencial_SetsMetodoPagoYEstadoPago() {
        Pedido pedido = buildPedido(1L, EstadoPedido.RECIBIDO, null);
        when(pedidoRepository.findById(1L)).thenReturn(Optional.of(pedido));
        when(pedidoRepository.save(pedido)).thenReturn(pedido);

        Pedido result = service.confirmarPresencial(1L, "EFECTIVO");

        Assertions.assertThat(result.getMetodoPago()).isEqualTo(MetodoPago.EFECTIVO);
        Assertions.assertThat(result.getEstadoPago()).isEqualTo("PENDIENTE_DE_PAGO");
        verify(pedidoRepository).save(pedido);
    }


    // ── confirmarPago ─────────────────────────────────────────────────────────

    @Test
    public void PedidoService_confirmarPago_ThrowsWhenPedidoNotFound() {
        when(pedidoRepository.findById(999L)).thenReturn(Optional.empty());

        Assertions.assertThatThrownBy(() -> service.confirmarPago(999L))
                .isInstanceOf(NoSuchElementException.class);
    }

    @Test
    public void PedidoService_confirmarPago_ThrowsWhenMetodoPagoIsNotPresencial() {
        Pedido pedido = buildPedido(1L, EstadoPedido.RECIBIDO, null);
        // metodoPago es null por defecto → no es presencial
        when(pedidoRepository.findById(1L)).thenReturn(Optional.of(pedido));

        Assertions.assertThatThrownBy(() -> service.confirmarPago(1L))
                .isInstanceOf(IllegalArgumentException.class);

        verify(pedidoRepository, never()).save(any(Pedido.class));
    }

    @Test
    public void PedidoService_confirmarPago_ThrowsWhenYaPagado() {
        Pedido pedido = buildPedido(1L, EstadoPedido.ENVIADO, null);
        pedido.setMetodoPago(MetodoPago.EFECTIVO);
        pedido.setEstadoPago("APROBADO");
        when(pedidoRepository.findById(1L)).thenReturn(Optional.of(pedido));

        Assertions.assertThatThrownBy(() -> service.confirmarPago(1L))
                .isInstanceOf(IllegalStateException.class);

        verify(pedidoRepository, never()).save(any(Pedido.class));
    }

    @Test
    public void PedidoService_confirmarPago_MarksAsAprobadoAndSetsFechaPagoYTotal() {
        Comida comida = new Comida();
        comida.setId(10L);
        comida.setPrice(54900);
        Pedido pedido = buildPedido(1L, EstadoPedido.ENVIADO, null);
        pedido.setMetodoPago(MetodoPago.EFECTIVO);
        LineaPedido linea = new LineaPedido(2, comida, null, pedido);
        pedido.getLineasPedido().add(linea);
        when(pedidoRepository.findById(1L)).thenReturn(Optional.of(pedido));
        when(pedidoRepository.save(pedido)).thenReturn(pedido);

        Pedido result = service.confirmarPago(1L);

        Assertions.assertThat(result.getEstadoPago()).isEqualTo("APROBADO");
        Assertions.assertThat(result.getFechaPago()).isNotNull();
        Assertions.assertThat(result.getTotalPagado()).isNotNull();
        Assertions.assertThat(result.getTotalPagado().doubleValue()).isEqualTo(109800.0);
        verify(pedidoRepository).save(pedido);
    }
}
