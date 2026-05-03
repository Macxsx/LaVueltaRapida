package com.example.demo.controller;

import java.util.Map;
import java.util.NoSuchElementException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.dto.AddProductoRequest;
import com.example.demo.entitys.Carrito;
import com.example.demo.service.CarritoService;

@RestController
@RequestMapping("/carrito")
public class CarritoRestController {

    @Autowired
    private CarritoService carritoService;

    @GetMapping("/{id}")
    public ResponseEntity<Carrito> findById(@PathVariable Long id) {
        return carritoService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/cliente/{clienteId}")
    public ResponseEntity<Carrito> findByCliente(@PathVariable Long clienteId) {
        return carritoService.findByClienteId(clienteId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/{id}/productos")
    public ResponseEntity<?> addProducto(@PathVariable Long id,
                                         @RequestBody AddProductoRequest body) {
        try {
            return ResponseEntity.ok(carritoService.addProducto(
                    id, body.comidaId, body.cantidad, body.adicionalesIds));
        } catch (NoSuchElementException e) {
            return ResponseEntity.notFound().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/{id}/productos/{lineaId}")
    public ResponseEntity<?> removeProducto(@PathVariable Long id, @PathVariable Long lineaId) {
        try {
            return ResponseEntity.ok(carritoService.removeProducto(id, lineaId));
        } catch (NoSuchElementException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PatchMapping("/{id}/productos/{lineaId}/aumentar")
    public ResponseEntity<?> aumentar(@PathVariable Long id, @PathVariable Long lineaId) {
        try {
            return ResponseEntity.ok(carritoService.aumentarCantidad(id, lineaId));
        } catch (NoSuchElementException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PatchMapping("/{id}/productos/{lineaId}/disminuir")
    public ResponseEntity<?> disminuir(@PathVariable Long id, @PathVariable Long lineaId) {
        try {
            return ResponseEntity.ok(carritoService.disminuirCantidad(id, lineaId));
        } catch (NoSuchElementException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}/vaciar")
    public ResponseEntity<?> vaciar(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(carritoService.vaciar(id));
        } catch (NoSuchElementException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
