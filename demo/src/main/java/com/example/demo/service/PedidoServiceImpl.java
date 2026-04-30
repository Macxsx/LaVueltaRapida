package com.example.demo.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

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
import com.example.demo.entitys.Pedido;
import com.example.demo.repository.CarritoRepository;
import com.example.demo.repository.ClienteRepository;
import com.example.demo.repository.DomiciliarioRepository;
import com.example.demo.repository.PedidoRepository;

@Service
public class PedidoServiceImpl {

    @Autowired
    private PedidoRepository repo;

    @Autowired
    private CarritoRepository carritoRepository;

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private DomiciliarioRepository domiciliarioRepository;

    public Page<Pedido> findAll(int page, int size) {
        if (page < 0) page = 0;
        if (size <= 0) size = 20;
        if (size > 100) size = 100;

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "fechaCreacion"));
        return repo.findAll(pageable);
    }

    public List<Pedido> findActivos() {
        return repo.findByEstadoNotOrderByFechaCreacionAsc(EstadoPedido.ENTREGADO);
    }

    public Optional<Pedido> findById(Long id) {
        return repo.findById(id);
    }

    public List<Pedido> findByCliente(Long clienteId) {
        return repo.findByClienteIdOrderByFechaCreacionDesc(clienteId);
    }

    public void save(Pedido pedido) {
        repo.save(pedido);
    }

    // ── Métodos de lógica de negocio ──

    @Transactional
    public Map<String, Object> crearPedidoDesdeCarrito(Long carritoId) {
        Optional<Carrito> carritoOpt = carritoRepository.findById(carritoId);
        if (carritoOpt.isEmpty()) {
            return Map.of(
                "success", false,
                "error", "Carrito no encontrado."
            );
        }

        Carrito carrito = carritoOpt.get();
        if (carrito.getLineasPedido().isEmpty()) {
            return Map.of(
                "success", false,
                "error", "El carrito está vacío. Agrega productos antes de confirmar el pedido."
            );
        }

        // Crear el nuevo pedido
        Pedido pedido = new Pedido();
        pedido.setCliente(carrito.getCliente());
        pedido.setEstado(EstadoPedido.RECIBIDO);
        pedido.setFechaCreacion(LocalDateTime.now());

        // Copiar cada línea del carrito al pedido
        for (LineaPedido carritoLinea : carrito.getLineasPedido()) {
            LineaPedido nuevaLinea = new LineaPedido();
            nuevaLinea.setComida(carritoLinea.getComida());
            nuevaLinea.setCantidad(carritoLinea.getCantidad());
            nuevaLinea.setPedido(pedido);

            // Copiar los adicionales
            for (LineaPedidoAdicional lpa : carritoLinea.getAdicionales()) {
                LineaPedidoAdicional nuevaLpa = new LineaPedidoAdicional();
                nuevaLpa.setAdicional(lpa.getAdicional());
                nuevaLpa.setLineaPedido(nuevaLinea);
                nuevaLinea.getAdicionales().add(nuevaLpa);
            }

            pedido.getLineasPedido().add(nuevaLinea);
        }

        // Guardar el pedido
        Pedido pedidoGuardado = repo.save(pedido);

        // Vaciar el carrito
        carrito.getLineasPedido().clear();
        carritoRepository.save(carrito);

        return Map.of(
            "success", true,
            "pedido", pedidoGuardado
        );
    }

    @Transactional
    public Map<String, Object> actualizarEstado(Long id, String estadoStr) {
        Optional<Pedido> pedidoOpt = repo.findById(id);
        if (pedidoOpt.isEmpty()) {
            return Map.of(
                "success", false,
                "error", "Pedido no encontrado."
            );
        }

        if (estadoStr == null) {
            return Map.of(
                "success", false,
                "error", "Se requiere el campo 'estado'."
            );
        }

        EstadoPedido nuevoEstado;
        try {
            nuevoEstado = EstadoPedido.valueOf(estadoStr.toUpperCase());
        } catch (IllegalArgumentException e) {
            return Map.of(
                "success", false,
                "error", "Estado inválido: '" + estadoStr + "'. Valores válidos: RECIBIDO, COCINANDO, ENVIADO, ENTREGADO"
            );
        }

        Pedido pedido = pedidoOpt.get();
        EstadoPedido estadoActual = pedido.getEstado();

        // Validar transición
        if (nuevoEstado != estadoActual.siguiente()) {
            String siguiente = estadoActual.siguiente() != null
                    ? estadoActual.siguiente().name()
                    : "ninguno (ya está en el estado final)";
            return Map.of(
                "success", false,
                "error", "Transición inválida: un pedido en estado '" + estadoActual
                        + "' solo puede avanzar a '" + siguiente + "'."
            );
        }

        // Gestionar domiciliario
        if (nuevoEstado == EstadoPedido.ENVIADO && pedido.getDomiciliario() == null) {
            Optional<Domiciliario> disponibleOpt = domiciliarioRepository
                    .findFirstByDisponibleTrueOrderByIdAsc();
            if (disponibleOpt.isEmpty()) {
                return Map.of(
                    "success", false,
                    "error", "No hay domiciliarios disponibles para asignar al pedido."
                );
            }
            Domiciliario disponible = disponibleOpt.get();
            disponible.setDisponible(false);
            domiciliarioRepository.save(disponible);
            pedido.setDomiciliario(disponible);
        } else if (nuevoEstado != EstadoPedido.ENVIADO && pedido.getDomiciliario() != null) {
            Domiciliario asignado = pedido.getDomiciliario();
            asignado.setDisponible(true);
            domiciliarioRepository.save(asignado);
            pedido.setDomiciliario(null);
        }

        pedido.setEstado(nuevoEstado);
        if (nuevoEstado == EstadoPedido.ENTREGADO && pedido.getFechaEntrega() == null) {
            pedido.setFechaEntrega(LocalDateTime.now());
        }

        repo.save(pedido);

        return Map.of(
            "success", true,
            "pedido", pedido
        );
    }
}
