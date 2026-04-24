package com.example.demo.controller;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;

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
    public ResponseEntity<?> add(@RequestBody Operador operador) {
        if (operadorRepository.findByUsuario(operador.getUsuario()).isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("error", "Ya existe un operador con el usuario '" + operador.getUsuario() + "'."));
        }
        return ResponseEntity.ok(operadorRepository.save(operador));
    }

    static class UpdateOperadorRequest {
        public String nombre;
        public String usuario;
        public String contrasena;
        public String currentPassword;
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody UpdateOperadorRequest req) {
        Optional<Operador> existing = operadorRepository.findById(id);
        if (existing.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        Operador stored = existing.get();
        if (req.currentPassword != null && !stored.getContrasena().equals(req.currentPassword)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        if (req.usuario != null && !req.usuario.equals(stored.getUsuario())
                && operadorRepository.findByUsuario(req.usuario).isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("error", "Ya existe un operador con el usuario '" + req.usuario + "'."));
        }
        stored.setNombre(req.nombre);
        stored.setUsuario(req.usuario);
        if (req.contrasena != null && !req.contrasena.isBlank()) {
            stored.setContrasena(req.contrasena);
        }
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
