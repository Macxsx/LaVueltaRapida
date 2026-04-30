package com.example.demo.service;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.entitys.Administrador;
import com.example.demo.repository.AdministradorRepository;

@Service
public class AdministradorServiceImpl implements AdministradorService {

    @Autowired
    private AdministradorRepository repo;

    @Override
    public Collection<Administrador> findAll() {
        return repo.findAll();
    }

    @Override
    public Optional<Administrador> findById(Long id) {
        return repo.findById(id);
    }

    @Override
    public Optional<Administrador> findByUsuario(String usuario) {
        return repo.findByUsuario(usuario);
    }

    @Override
    public void save(Administrador administrador) {
        repo.save(administrador);
    }

    @Override
    public void deleteById(Long id) {
        repo.deleteById(id);
    }

    @Override
    public boolean existsById(Long id) {
        return repo.existsById(id);
    }

    // ── Implementación de lógica de negocio ──

    @Override
    public Map<String, Object> updateAdministrador(Long id, String usuario, String contrasena, String currentPassword) {
        Optional<Administrador> existing = findById(id);
        if (existing.isEmpty()) {
            return Map.of(
                "success", false,
                "error", "El administrador no existe."
            );
        }

        Administrador stored = existing.get();

        if (currentPassword != null && !stored.getContrasena().equals(currentPassword)) {
            return Map.of(
                "success", false,
                "error", "La contraseña actual es incorrecta."
            );
        }

        if (usuario != null && !usuario.equals(stored.getUsuario())
                && findByUsuario(usuario).isPresent()) {
            return Map.of(
                "success", false,
                "error", "Ya existe un administrador con el usuario '" + usuario + "'."
            );
        }

        stored.setUsuario(usuario);
        if (contrasena != null && !contrasena.isBlank()) {
            stored.setContrasena(contrasena);
        }
        save(stored);

        return Map.of(
            "success", true,
            "administrador", stored
        );
    }

    @Override
    public boolean validateCredentials(String usuario, String contrasena) {
        Optional<Administrador> admin = findByUsuario(usuario);
        return admin.isPresent() && admin.get().getContrasena().equals(contrasena);
    }
}
