package com.example.demo.controller;

import java.util.Collection;
import java.util.Optional;

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

import com.example.demo.entitys.Operador;
import com.example.demo.repository.OperadorRepository;

@CrossOrigin(origins = {"http://localhost:5000", "http://127.0.0.1:5000"})
@RestController
@RequestMapping("/operadores")
public class OperadorRestController {

    @Autowired
    private OperadorRepository operadorRepository;

    @GetMapping
    public Collection<Operador> findAll() {
        return operadorRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Operador> findById(@PathVariable Long id) {
        Optional<Operador> operador = operadorRepository.findById(id);
        return operador.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Operador> add(@RequestBody Operador operador) {
        Operador saved = operadorRepository.save(operador);
        return ResponseEntity.ok(saved);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Operador> update(@PathVariable Long id, @RequestBody Operador operador) {
        Optional<Operador> existing = operadorRepository.findById(id);
        if (existing.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        Operador stored = existing.get();
        stored.setNombre(operador.getNombre());
        stored.setUsuario(operador.getUsuario());
        stored.setContrasena(operador.getContrasena());
        operadorRepository.save(stored);
        return ResponseEntity.ok(stored);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (!operadorRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        operadorRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
