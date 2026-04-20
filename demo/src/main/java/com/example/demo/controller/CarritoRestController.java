package com.example.demo.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.entitys.Adicional;
import com.example.demo.entitys.Carrito;
import com.example.demo.entitys.Comida;
import com.example.demo.entitys.LineaPedido;
import com.example.demo.entitys.LineaPedidoAdicional;
import com.example.demo.repository.AdicionalRepository;
import com.example.demo.repository.CarritoRepository;
import com.example.demo.repository.ComidaRepository;
import com.example.demo.repository.LineaPedidoRepository;

@CrossOrigin(origins = {"http://localhost:5000", "http://127.0.0.1:5000"})
@RestController
@RequestMapping("/carrito")
public class CarritoRestController {

    @Autowired
    private CarritoRepository carritoRepository;

    @Autowired
    private LineaPedidoRepository lineaPedidoRepository;

    @Autowired
    private ComidaRepository comidaRepository;

    @Autowired
    private AdicionalRepository adicionalRepository;

    // ── DTO ──────────────────────────────────────────────────────────────────
    static class AddProductoRequest {
        public Long comidaId;
        public Integer cantidad;
        public List<Long> adicionalesIds;
    }

    // ── GET /carrito/{id} ────────────────────────────────────────────────────
    @GetMapping("/{id}")
    public ResponseEntity<Carrito> findById(@PathVariable Long id) {
        return carritoRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // ── GET /carrito/cliente/{clienteId} ─────────────────────────────────────
    @GetMapping("/cliente/{clienteId}")
    public ResponseEntity<Carrito> findByCliente(@PathVariable Long clienteId) {
        return carritoRepository.findByClienteId(clienteId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // ── POST /carrito/{id}/productos ─────────────────────────────────────────
    // Body: { "comidaId": 1, "cantidad": 2, "adicionalesIds": [3, 7] }
    // Regla: si ya existe una línea con la misma comida Y los mismos adicionales
    //        se suma la cantidad. Si los adicionales son distintos, se crea una
    //        nueva línea separada.
    @Transactional
    @PostMapping("/{id}/productos")
    public ResponseEntity<?> addProducto(@PathVariable Long id,
                                         @RequestBody AddProductoRequest body) {
        Carrito carrito = carritoRepository.findById(id).orElse(null);
        if (carrito == null) {
            return ResponseEntity.notFound().build();
        }
        if (body.comidaId == null || body.cantidad == null || body.cantidad <= 0) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Se requieren 'comidaId' y 'cantidad' (> 0)"));
        }

        Comida comida = comidaRepository.findById(body.comidaId).orElse(null);
        if (comida == null) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Comida con id " + body.comidaId + " no encontrada"));
        }

        // Normalizar la lista de adicionales entrante (ordenada, sin nulos)
        Set<Long> incomingIds = new TreeSet<>();
        if (body.adicionalesIds != null) {
            incomingIds.addAll(body.adicionalesIds);
        }

        // Validar que todos los adicionales existan antes de tocar nada
        List<Adicional> adicionales = new ArrayList<>();
        for (Long adicionalId : incomingIds) {
            Adicional adicional = adicionalRepository.findById(adicionalId).orElse(null);
            if (adicional == null) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Adicional con id " + adicionalId + " no encontrado"));
            }
            adicionales.add(adicional);
        }

        // Buscar si ya existe una línea con la misma comida
        List<LineaPedido> lineasExistentes =
                lineaPedidoRepository.findByCarritoIdAndComidaId(id, body.comidaId);

        for (LineaPedido linea : lineasExistentes) {
            Set<Long> existingIds = linea.getAdicionales().stream()
                    .map(lpa -> lpa.getAdicional().getId())
                    .collect(Collectors.toCollection(TreeSet::new));

            if (existingIds.equals(incomingIds)) {
                // Mismo conjunto de adicionales → solo se suma la cantidad
                linea.setCantidad(linea.getCantidad() + body.cantidad);
                lineaPedidoRepository.save(linea);
                return ResponseEntity.ok(carritoRepository.findById(id).get());
            }
        }

        // Adicionales distintos (o primera vez) → nueva línea
        LineaPedido nuevaLinea = new LineaPedido(body.cantidad, comida, carrito, null);
        for (Adicional adicional : adicionales) {
            LineaPedidoAdicional lpa = new LineaPedidoAdicional(nuevaLinea, adicional);
            nuevaLinea.getAdicionales().add(lpa);
        }
        lineaPedidoRepository.save(nuevaLinea);

        return ResponseEntity.ok(carritoRepository.findById(id).get());
    }

    // ── DELETE /carrito/{id}/productos/{lineaId} ─────────────────────────────
    @Transactional
    @DeleteMapping("/{id}/productos/{lineaId}")
    public ResponseEntity<?> removeProducto(@PathVariable Long id,
                                            @PathVariable Long lineaId) {
        if (!carritoRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        LineaPedido linea = lineaPedidoRepository.findById(lineaId).orElse(null);
        if (linea == null || linea.getCarrito() == null
                || !linea.getCarrito().getId().equals(id)) {
            return ResponseEntity.notFound().build();
        }
        lineaPedidoRepository.deleteById(lineaId);
        return ResponseEntity.ok(carritoRepository.findById(id).get());
    }

    // ── PATCH /carrito/{id}/productos/{lineaId}/aumentar ─────────────────────
    @Transactional
    @PatchMapping("/{id}/productos/{lineaId}/aumentar")
    public ResponseEntity<?> aumentar(@PathVariable Long id,
                                      @PathVariable Long lineaId) {
        return cambiarCantidad(id, lineaId, +1);
    }

    // ── PATCH /carrito/{id}/productos/{lineaId}/disminuir ────────────────────
    // Si la cantidad llega a 0, elimina la línea automáticamente.
    @Transactional
    @PatchMapping("/{id}/productos/{lineaId}/disminuir")
    public ResponseEntity<?> disminuir(@PathVariable Long id,
                                       @PathVariable Long lineaId) {
        return cambiarCantidad(id, lineaId, -1);
    }

    // ── DELETE /carrito/{id}/vaciar ──────────────────────────────────────────
    @Transactional
    @DeleteMapping("/{id}/vaciar")
    public ResponseEntity<?> vaciar(@PathVariable Long id) {
        Carrito carrito = carritoRepository.findById(id).orElse(null);
        if (carrito == null) {
            return ResponseEntity.notFound().build();
        }
        carrito.getLineasPedido().clear();
        carritoRepository.save(carrito);
        return ResponseEntity.ok(carritoRepository.findById(id).get());
    }

    // ── Helper ───────────────────────────────────────────────────────────────
    private ResponseEntity<?> cambiarCantidad(Long carritoId, Long lineaId, int delta) {
        if (!carritoRepository.existsById(carritoId)) {
            return ResponseEntity.notFound().build();
        }
        LineaPedido linea = lineaPedidoRepository.findById(lineaId).orElse(null);
        if (linea == null || linea.getCarrito() == null
                || !linea.getCarrito().getId().equals(carritoId)) {
            return ResponseEntity.notFound().build();
        }
        int nueva = linea.getCantidad() + delta;
        if (nueva <= 0) {
            lineaPedidoRepository.deleteById(lineaId);
        } else {
            linea.setCantidad(nueva);
            lineaPedidoRepository.save(linea);
        }
        return ResponseEntity.ok(carritoRepository.findById(carritoId).get());
    }
}
