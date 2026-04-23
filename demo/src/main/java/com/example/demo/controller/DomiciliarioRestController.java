package com.example.demo.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
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

import com.example.demo.entitys.Domiciliario;
import com.example.demo.repository.DomiciliarioRepository;

@CrossOrigin(origins = {"http://localhost:5000", "http://127.0.0.1:5000"})
@RestController
@RequestMapping("/domiciliarios")
public class DomiciliarioRestController {

    @Autowired
    private DomiciliarioRepository domiciliarioRepository;

    // ── GET /domiciliarios ───────────────────────────────────────────────────
    @GetMapping
    public List<Domiciliario> findAll() {
        return domiciliarioRepository.findAll();
    }

    // ── GET /domiciliarios/{id} ──────────────────────────────────────────────
    @GetMapping("/{id}")
    public ResponseEntity<Domiciliario> findById(@PathVariable Long id) {
        return domiciliarioRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // ── POST /domiciliarios ──────────────────────────────────────────────────
    @PostMapping
    public ResponseEntity<Domiciliario> add(@RequestBody Domiciliario domiciliario) {
        domiciliario.setId(null);
        Domiciliario guardado = domiciliarioRepository.save(domiciliario);
        return ResponseEntity.ok(guardado);
    }

    // ── PUT /domiciliarios/{id} ──────────────────────────────────────────────
    @PutMapping("/{id}")
    public ResponseEntity<Domiciliario> update(@PathVariable Long id,
                                               @RequestBody Domiciliario domiciliario) {
        Domiciliario stored = domiciliarioRepository.findById(id).orElse(null);
        if (stored == null) {
            return ResponseEntity.notFound().build();
        }
        stored.setNombre(domiciliario.getNombre());
        stored.setCedula(domiciliario.getCedula());
        stored.setCelular(domiciliario.getCelular());
        stored.setDisponible(domiciliario.isDisponible());
        return ResponseEntity.ok(domiciliarioRepository.save(stored));
    }

    // ── DELETE /domiciliarios/{id} ───────────────────────────────────────────
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (!domiciliarioRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        domiciliarioRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
