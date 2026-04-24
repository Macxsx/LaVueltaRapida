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
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.entitys.Administrador;
import com.example.demo.repository.AdministradorRepository;

@CrossOrigin(origins = {"http://localhost:5000", "http://127.0.0.1:5000"})
@RestController
@RequestMapping("/administradores")
public class AdministradorRestController {

    @Autowired
    private AdministradorRepository administradorRepository;

    @GetMapping
    public Collection<Administrador> findAll() {
        return administradorRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Administrador> findById(@PathVariable Long id) {
        Optional<Administrador> administrador = administradorRepository.findById(id);
        return administrador.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    static class UpdateAdminRequest {
        public String usuario;
        public String contrasena;
        public String currentPassword;
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody UpdateAdminRequest req) {
        Optional<Administrador> existing = administradorRepository.findById(id);
        if (existing.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        Administrador stored = existing.get();
        if (req.currentPassword != null && !stored.getContrasena().equals(req.currentPassword)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "La contraseña actual es incorrecta."));
        }
        if (req.usuario != null && !req.usuario.equals(stored.getUsuario())
                && administradorRepository.findByUsuario(req.usuario).isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("error", "Ya existe un administrador con el usuario '" + req.usuario + "'."));
        }
        stored.setUsuario(req.usuario);
        if (req.contrasena != null && !req.contrasena.isBlank()) {
            stored.setContrasena(req.contrasena);
        }
        administradorRepository.save(stored);
        return ResponseEntity.ok(stored);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (!administradorRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        administradorRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
