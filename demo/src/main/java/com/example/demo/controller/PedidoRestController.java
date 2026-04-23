package com.example.demo.controller;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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

@CrossOrigin(origins = {"http://localhost:5000", "http://127.0.0.1:5000"})
@RestController
@RequestMapping("/pedido")
public class PedidoRestController {

    @Autowired
    private PedidoRepository pedidoRepository;

    @Autowired
    private CarritoRepository carritoRepository;

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private DomiciliarioRepository domiciliarioRepository;

    // ── GET /pedido ───────────────────────────────────────────────────────────
    // Paginado, ordenado por fecha de creación descendente.
    // Query params opcionales: page (default 0), size (default 20).
    @GetMapping
    public ResponseEntity<Map<String, Object>> findAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        if (page < 0) page = 0;
        if (size <= 0) size = 20;
        if (size > 100) size = 100;

        Pageable pageable = PageRequest.of(page, size,
                Sort.by(Sort.Direction.DESC, "fechaCreacion"));
        Page<Pedido> resultado = pedidoRepository.findAll(pageable);

        Map<String, Object> body = Map.of(
                "content", resultado.getContent(),
                "page", resultado.getNumber(),
                "size", resultado.getSize(),
                "totalElements", resultado.getTotalElements(),
                "totalPages", resultado.getTotalPages());
        return ResponseEntity.ok(body);
    }

    // ── GET /pedido/activos ───────────────────────────────────────────────────
    // Devuelve los pedidos cuyo estado sea distinto de ENTREGADO,
    // ordenados por fecha de creación descendente.
    @GetMapping("/activos")
    public ResponseEntity<List<Pedido>> findActivos() {
        return ResponseEntity.ok(
                pedidoRepository.findByEstadoNotOrderByFechaCreacionDesc(EstadoPedido.ENTREGADO));
    }

    // ── GET /pedido/{id} ──────────────────────────────────────────────────────
    @GetMapping("/{id}")
    public ResponseEntity<Pedido> findById(@PathVariable Long id) {
        return pedidoRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // ── GET /pedido/cliente/{clienteId} ───────────────────────────────────────
    // Devuelve los pedidos del cliente, ordenados por fecha de creación
    // (más recientes primero) para que el frontend no tenga que reordenar.
    @GetMapping("/cliente/{clienteId}")
    public ResponseEntity<List<Pedido>> findByCliente(@PathVariable Long clienteId) {
        if (!clienteRepository.existsById(clienteId)) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(
                pedidoRepository.findByClienteIdOrderByFechaCreacionDesc(clienteId));
    }

    // ── POST /pedido/desde-carrito/{carritoId} ────────────────────────────────
    // Convierte el carrito activo en un pedido real.
    // Copia todas las líneas (comida + cantidad + adicionales) y vacía el carrito.
    @Transactional
    @PostMapping("/desde-carrito/{carritoId}")
    public ResponseEntity<?> desdeCarrito(@PathVariable Long carritoId) {
        Carrito carrito = carritoRepository.findById(carritoId).orElse(null);
        if (carrito == null) {
            return ResponseEntity.notFound().build();
        }
        if (carrito.getLineasPedido().isEmpty()) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "El carrito está vacío. Agrega productos antes de confirmar el pedido."));
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

            // Copiar los adicionales de cada línea
            for (LineaPedidoAdicional lpa : carritoLinea.getAdicionales()) {
                LineaPedidoAdicional nuevaLpa = new LineaPedidoAdicional();
                nuevaLpa.setAdicional(lpa.getAdicional());
                nuevaLpa.setLineaPedido(nuevaLinea);
                nuevaLinea.getAdicionales().add(nuevaLpa);
            }

            pedido.getLineasPedido().add(nuevaLinea);
        }

        // Guardar el pedido (cascade guarda líneas y adicionales)
        Pedido pedidoGuardado = pedidoRepository.save(pedido);

        // Vaciar el carrito
        carrito.getLineasPedido().clear();
        carritoRepository.save(carrito);

        return ResponseEntity.ok(pedidoGuardado);
    }

    // ── PATCH /pedido/{id}/estado ─────────────────────────────────────────────
    // Body: { "estado": "COCINANDO" }
    // Valores válidos: RECIBIDO, COCINANDO, ENVIADO, ENTREGADO
    @Transactional
    @PatchMapping("/{id}/estado")
    public ResponseEntity<?> actualizarEstado(@PathVariable Long id,
                                              @RequestBody Map<String, String> body) {
        Pedido pedido = pedidoRepository.findById(id).orElse(null);
        if (pedido == null) {
            return ResponseEntity.notFound().build();
        }

        String estadoStr = body.get("estado");
        if (estadoStr == null) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Se requiere el campo 'estado'."));
        }

        EstadoPedido nuevoEstado;
        try {
            nuevoEstado = EstadoPedido.valueOf(estadoStr.toUpperCase());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Estado inválido: '" + estadoStr + "'. Valores válidos: RECIBIDO, COCINANDO, ENVIADO, ENTREGADO"));
        }

        EstadoPedido estadoAnterior = pedido.getEstado();

        // Al pasar a ENVIADO: asignar un domiciliario disponible y marcarlo ocupado.
        if (nuevoEstado == EstadoPedido.ENVIADO
                && estadoAnterior != EstadoPedido.ENVIADO
                && pedido.getDomiciliario() == null) {
            Domiciliario disponible = domiciliarioRepository
                    .findFirstByDisponibleTrueOrderByIdAsc()
                    .orElse(null);
            if (disponible == null) {
                return ResponseEntity.status(409).body(Map.of(
                        "error", "No hay domiciliarios disponibles para asignar al pedido."));
            }
            disponible.setDisponible(false);
            domiciliarioRepository.save(disponible);
            pedido.setDomiciliario(disponible);
        }

        // Al pasar a ENTREGADO: liberar al domiciliario asignado.
        if (nuevoEstado == EstadoPedido.ENTREGADO
                && estadoAnterior != EstadoPedido.ENTREGADO
                && pedido.getDomiciliario() != null) {
            Domiciliario asignado = pedido.getDomiciliario();
            asignado.setDisponible(true);
            domiciliarioRepository.save(asignado);
        }

        pedido.setEstado(nuevoEstado);
        if (nuevoEstado == EstadoPedido.ENTREGADO && pedido.getFechaEntrega() == null) {
            pedido.setFechaEntrega(LocalDateTime.now());
        }

        pedidoRepository.save(pedido);
        return ResponseEntity.ok(pedido);
    }
}
