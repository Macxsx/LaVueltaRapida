package com.example.demo.controller;

import java.util.Map;
import java.util.NoSuchElementException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.entitys.Pedido;
import com.example.demo.service.PedidoService;

@RestController
@RequestMapping("/pedido")
public class PedidoRestController {

    @Autowired
    private PedidoService pedidoService;

    @GetMapping
    public ResponseEntity<Map<String, Object>> findAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Page<Pedido> resultado = pedidoService.findAll(page, size);
        return ResponseEntity.ok(Map.of(
                "content",       resultado.getContent(),
                "page",          resultado.getNumber(),
                "size",          resultado.getSize(),
                "totalElements", resultado.getTotalElements(),
                "totalPages",    resultado.getTotalPages()));
    }

    @GetMapping("/activos")
    public ResponseEntity<?> findActivos() {
        return ResponseEntity.ok(pedidoService.findActivos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Pedido> findById(@PathVariable Long id) {
        return pedidoService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/cliente/{clienteId}")
    public ResponseEntity<?> findByCliente(@PathVariable Long clienteId) {
        try {
            return ResponseEntity.ok(pedidoService.findByClienteId(clienteId));
        } catch (NoSuchElementException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/desde-carrito/{carritoId}")
    public ResponseEntity<?> desdeCarrito(@PathVariable Long carritoId) {
        try {
            return ResponseEntity.ok(pedidoService.desdeCarrito(carritoId));
        } catch (NoSuchElementException e) {
            return ResponseEntity.notFound().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PatchMapping("/{id}/estado")
    public ResponseEntity<?> actualizarEstado(@PathVariable Long id,
                                              @RequestBody Map<String, String> body) {
        try {
            return ResponseEntity.ok(pedidoService.actualizarEstado(id, body.get("estado")));
        } catch (NoSuchElementException e) {
            return ResponseEntity.notFound().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (IllegalStateException e) {
            return ResponseEntity.status(409).body(Map.of("error", e.getMessage()));
        }
    }
}
