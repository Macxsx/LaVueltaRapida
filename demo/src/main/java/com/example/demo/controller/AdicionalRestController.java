package com.example.demo.controller;

import java.util.Collection;
import java.util.Map;
import java.util.NoSuchElementException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.dto.AdicionalRequest;
import com.example.demo.entitys.Adicional;
import com.example.demo.entitys.Categoria;
import com.example.demo.service.AdicionalService;

@CrossOrigin(origins = {"http://localhost:5000", "http://127.0.0.1:5000"})
@RestController
@RequestMapping("/adicionales")
public class AdicionalRestController {

    @Autowired
    private AdicionalService adicionalService;

    @GetMapping
    public Collection<Adicional> findAll() {
        return adicionalService.findAll();
    }

    @GetMapping("/categoria/{categoriaId}")
    public Collection<Adicional> findByCategoriaId(@PathVariable Long categoriaId) {
        return adicionalService.findByCategoriaId(categoriaId);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Adicional> findById(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(adicionalService.findById(id));
        } catch (NoSuchElementException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/{id}/categorias")
    public Collection<Categoria> findCategorias(@PathVariable Long id) {
        return adicionalService.findCategorias(id);
    }

    @PostMapping
    public ResponseEntity<?> add(@RequestBody AdicionalRequest req) {
        try {
            return ResponseEntity.ok(
                    adicionalService.create(req.name, req.price, req.available, req.categoriaIds));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody AdicionalRequest req) {
        try {
            return ResponseEntity.ok(
                    adicionalService.update(id, req.name, req.price, req.available, req.categoriaIds));
        } catch (NoSuchElementException e) {
            return ResponseEntity.notFound().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        try {
            adicionalService.delete(id);
            return ResponseEntity.noContent().build();
        } catch (NoSuchElementException e) {
            return ResponseEntity.notFound().build();
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("error", e.getMessage()));
        }
    }
}
