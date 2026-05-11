package com.example.demo.service;

import java.time.LocalDateTime;
import java.util.EnumSet;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.entitys.Carrito;
import com.example.demo.entitys.Domiciliario;
import com.example.demo.entitys.EstadoPedido;
import com.example.demo.entitys.LineaPedido;
import com.example.demo.entitys.LineaPedidoAdicional;
import com.example.demo.entitys.MetodoPago;
import com.example.demo.entitys.Pedido;
import com.example.demo.repository.CarritoRepository;
import com.example.demo.repository.ClienteRepository;
import com.example.demo.repository.PedidoRepository;

@Service
public class PedidoServiceImpl implements PedidoService {

    private static final Set<MetodoPago> METODOS_PRESENCIALES = EnumSet.of(
            MetodoPago.EFECTIVO, MetodoPago.DATAFONO, MetodoPago.NEQUI,
            MetodoPago.DAVIPLATA, MetodoPago.TRANSFERENCIA, MetodoPago.LLAVE);

    @Autowired
    private PedidoRepository pedidoRepository;

    @Autowired
    private CarritoRepository carritoRepository;

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private DomiciliarioService domiciliarioService;

    @Override
    public Page<Pedido> findAll(int page, int size) {
        if (page < 0) page = 0;
        if (size <= 0) size = 20;
        if (size > 100) size = 100;
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "fechaCreacion"));
        return pedidoRepository.findAll(pageable);
    }

    private static final java.util.Set<EstadoPedido> ESTADOS_ACTIVOS = EnumSet.of(
            EstadoPedido.RECIBIDO, EstadoPedido.COCINANDO, EstadoPedido.ENVIADO);

    @Override
    public List<Pedido> findActivos() {
        return pedidoRepository.findByEstadoInOrderByFechaCreacionAsc(ESTADOS_ACTIVOS);
    }

    @Override
    public Optional<Pedido> findById(Long id) {
        return pedidoRepository.findById(id);
    }

    @Override
    public List<Pedido> findByClienteId(Long clienteId) {
        if (!clienteRepository.existsById(clienteId)) {
            throw new NoSuchElementException("Cliente no encontrado.");
        }
        return pedidoRepository.findByClienteIdOrderByFechaCreacionDesc(clienteId);
    }

    @Override
    @Transactional
    public Pedido desdeCarrito(Long carritoId, String metodoPagoStr) {
        MetodoPago metodoPago = null;
        if (metodoPagoStr != null && !metodoPagoStr.isBlank()) {
            try {
                metodoPago = MetodoPago.valueOf(metodoPagoStr.toUpperCase());
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException(
                        "metodoPago inválido. Valores: " + java.util.Arrays.toString(MetodoPago.values()));
            }
        }

        Carrito carrito = carritoRepository.findById(carritoId)
                .orElseThrow(() -> new NoSuchElementException("Carrito no encontrado."));
        if (carrito.getLineasPedido().isEmpty()) {
            throw new IllegalArgumentException(
                    "El carrito está vacío. Agrega productos antes de confirmar el pedido.");
        }

        Pedido pedido = new Pedido();
        pedido.setCliente(carrito.getCliente());
        pedido.setEstado(EstadoPedido.RECIBIDO);
        pedido.setFechaCreacion(LocalDateTime.now());
        pedido.setMetodoPago(metodoPago);

        for (LineaPedido carritoLinea : carrito.getLineasPedido()) {
            LineaPedido nuevaLinea = new LineaPedido();
            nuevaLinea.setComida(carritoLinea.getComida());
            nuevaLinea.setCantidad(carritoLinea.getCantidad());
            nuevaLinea.setPedido(pedido);
            for (LineaPedidoAdicional lpa : carritoLinea.getAdicionales()) {
                LineaPedidoAdicional nuevaLpa = new LineaPedidoAdicional();
                nuevaLpa.setAdicional(lpa.getAdicional());
                nuevaLpa.setLineaPedido(nuevaLinea);
                nuevaLinea.getAdicionales().add(nuevaLpa);
            }
            pedido.getLineasPedido().add(nuevaLinea);
        }

        Pedido guardado = pedidoRepository.save(pedido);
        carrito.getLineasPedido().clear();
        carritoRepository.save(carrito);
        return guardado;
    }

    @Override
    @Transactional
    public Pedido confirmarPresencial(Long id, String metodoPagoStr) {
        if (metodoPagoStr == null || metodoPagoStr.isBlank()) {
            throw new IllegalArgumentException("Se requiere el campo 'metodoPago'.");
        }

        MetodoPago metodoPago;
        try {
            metodoPago = MetodoPago.valueOf(metodoPagoStr.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(
                    "metodoPago inválido: '" + metodoPagoStr + "'. " +
                    "Valores presenciales: EFECTIVO, DATAFONO, NEQUI, DAVIPLATA, TRANSFERENCIA, LLAVE");
        }

        if (!METODOS_PRESENCIALES.contains(metodoPago)) {
            throw new IllegalArgumentException(
                    "'" + metodoPago + "' no es un método de pago presencial. " +
                    "Use: EFECTIVO, DATAFONO, NEQUI, DAVIPLATA, TRANSFERENCIA, LLAVE");
        }

        Pedido pedido = pedidoRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Pedido no encontrado."));

        pedido.setMetodoPago(metodoPago);
        pedido.setEstadoPago("PENDIENTE_DE_PAGO");

        return pedidoRepository.save(pedido);
    }

    @Override
    @Transactional
    public Pedido confirmarPago(Long id) {
        Pedido pedido = pedidoRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Pedido no encontrado."));

        if (pedido.getMetodoPago() == null || !METODOS_PRESENCIALES.contains(pedido.getMetodoPago())) {
            throw new IllegalArgumentException(
                    "Solo se puede confirmar el pago manualmente en pedidos con método de pago contra entrega.");
        }

        if ("APROBADO".equals(pedido.getEstadoPago())) {
            throw new IllegalStateException("El pedido ya está marcado como pagado.");
        }

        pedido.setEstadoPago("APROBADO");
        pedido.setFechaPago(LocalDateTime.now());

        if (pedido.getTotalPagado() == null && pedido.getLineasPedido() != null) {
            double total = pedido.getLineasPedido().stream().mapToDouble(lp -> {
                double base = lp.getComida() != null ? lp.getComida().getPrice() : 0.0;
                double adics = lp.getAdicionales() != null
                        ? lp.getAdicionales().stream()
                              .mapToDouble(a -> a.getAdicional() != null ? a.getAdicional().getPrice() : 0.0)
                              .sum()
                        : 0.0;
                int cantidad = lp.getCantidad() != null ? lp.getCantidad() : 0;
                return (base + adics) * cantidad;
            }).sum();
            pedido.setTotalPagado(java.math.BigDecimal.valueOf(total));
        }

        return pedidoRepository.save(pedido);
    }

    @Override
    @Transactional
    public Pedido actualizarEstado(Long id, String estadoStr) {
        if (estadoStr == null) {
            throw new IllegalArgumentException("Se requiere el campo 'estado'.");
        }
        Pedido pedido = pedidoRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Pedido no encontrado."));

        EstadoPedido nuevoEstado;
        try {
            nuevoEstado = EstadoPedido.valueOf(estadoStr.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(
                    "Estado inválido: '" + estadoStr + "'. Valores válidos: RECIBIDO, COCINANDO, ENVIADO, ENTREGADO, CANCELADO");
        }

        EstadoPedido estadoActual = pedido.getEstado();

        if (nuevoEstado == EstadoPedido.CANCELADO) {
            throw new IllegalArgumentException(
                    "El estado CANCELADO es asignado automáticamente por el sistema de pagos y no puede establecerse manualmente.");
        }

        if (nuevoEstado != estadoActual.siguiente()) {
            String siguiente = estadoActual.siguiente() != null
                    ? estadoActual.siguiente().name()
                    : "ninguno (ya está en el estado final)";
            throw new IllegalArgumentException(
                    "Transición inválida: un pedido en estado '" + estadoActual
                    + "' solo puede avanzar a '" + siguiente + "'.");
        }

        if (nuevoEstado == EstadoPedido.ENTREGADO && !"APROBADO".equals(pedido.getEstadoPago())) {
            throw new IllegalArgumentException(
                    "El pedido no puede marcarse como entregado: el pago aún no ha sido confirmado.");
        }

        if (nuevoEstado == EstadoPedido.ENVIADO && pedido.getDomiciliario() == null) {
            Domiciliario disponible = domiciliarioService.findFirstDisponible().orElse(null);
            if (disponible == null) {
                throw new IllegalStateException(
                        "No hay domiciliarios disponibles para asignar al pedido.");
            }
            disponible.setDisponible(false);
            domiciliarioService.save(disponible);
            pedido.setDomiciliario(disponible);
        } else if (nuevoEstado != EstadoPedido.ENVIADO && pedido.getDomiciliario() != null) {
            Domiciliario asignado = pedido.getDomiciliario();
            asignado.setDisponible(true);
            domiciliarioService.save(asignado);
            pedido.setDomiciliario(null);
        }

        pedido.setEstado(nuevoEstado);
        if (nuevoEstado == EstadoPedido.ENTREGADO && pedido.getFechaEntrega() == null) {
            pedido.setFechaEntrega(LocalDateTime.now());
        }

        return pedidoRepository.save(pedido);
    }

    @Override
    @Transactional
    public void cancelarPorPago(Long pedidoId) {
        Pedido pedido = pedidoRepository.findById(pedidoId)
                .orElseThrow(() -> new NoSuchElementException("Pedido no encontrado."));
        if (pedido.getEstado() == EstadoPedido.CANCELADO || pedido.getEstado() == EstadoPedido.ENTREGADO) {
            return;
        }
        if (pedido.getDomiciliario() != null) {
            Domiciliario asignado = pedido.getDomiciliario();
            asignado.setDisponible(true);
            domiciliarioService.save(asignado);
            pedido.setDomiciliario(null);
        }
        pedido.setEstado(EstadoPedido.CANCELADO);
        pedidoRepository.save(pedido);
    }
}
