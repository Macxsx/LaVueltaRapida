package com.example.demo.controller;

import java.util.Collection;
import java.util.NoSuchElementException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.dto.UpdateOperadorRequest;
import com.example.demo.entitys.Operador;
import com.example.demo.error.ApiError;
import com.example.demo.service.OperadorService;

@RestController
@RequestMapping("/operadores")
public class OperadorRestController {

    @Autowired
    private OperadorService operadorService;

    @GetMapping
    public ResponseEntity<Collection<Operador>> findAll() {
        return ResponseEntity.ok(operadorService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Operador> findById(@PathVariable Long id) {
        return operadorService.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<?> add(@RequestBody Operador operador) {
        try {
            return ResponseEntity.ok(operadorService.create(operador));
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(ApiError.of(e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody UpdateOperadorRequest req) {
        try {
            return ResponseEntity.ok(
                    operadorService.update(id, req.nombre, req.usuario, req.contrasena, req.currentPassword));
        } catch (NoSuchElementException e) {
            return ResponseEntity.notFound().build();
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ApiError.of(e.getMessage()));
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(ApiError.of(e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        try {
            operadorService.delete(id);
            return ResponseEntity.noContent().build();
        } catch (NoSuchElementException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
