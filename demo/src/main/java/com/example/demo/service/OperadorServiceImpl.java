package com.example.demo.service;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.entitys.Operador;
import com.example.demo.repository.OperadorRepository;

@Service
public class OperadorServiceImpl implements OperadorService {

    @Autowired
    private OperadorRepository repo;

    @Override
    public Collection<Operador> findAll() {
        return repo.findAll();
    }

    @Override
    public Optional<Operador> findById(Long id) {
        return repo.findById(id);
    }

    @Override
    public Optional<Operador> findByUsuario(String usuario) {
        return repo.findByUsuario(usuario);
    }

    @Override
    public void save(Operador operador) {
        repo.save(operador);
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
    public Map<String, Object> createOperador(String usuario, String contrasena) {
        if (usuario == null || usuario.isBlank()) {
            return Map.of(
                "success", false,
                "error", "El usuario es obligatorio."
            );
        }

        if (findByUsuario(usuario).isPresent()) {
            return Map.of(
                "success", false,
                "error", "Ya existe un operador con el usuario '" + usuario + "'."
            );
        }

        Operador operador = new Operador(usuario, contrasena);
        save(operador);

        return Map.of(
            "success", true,
            "operador", operador
        );
    }

    @Override
    public Map<String, Object> updateOperador(Long id, String usuario, String contrasena, String currentPassword) {
        Optional<Operador> existing = findById(id);
        if (existing.isEmpty()) {
            return Map.of(
                "success", false,
                "error", "El operador no existe."
            );
        }

        Operador stored = existing.get();

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
                "error", "Ya existe un operador con el usuario '" + usuario + "'."
            );
        }

        stored.setUsuario(usuario);
        if (contrasena != null && !contrasena.isBlank()) {
            stored.setContrasena(contrasena);
        }
        save(stored);

        return Map.of(
            "success", true,
            "operador", stored
        );
    }

    @Override
    public Map<String, Object> deleteOperador(Long id) {
        Optional<Operador> operador = findById(id);
        if (operador.isEmpty()) {
            return Map.of(
                "success", false,
                "error", "El operador no existe."
            );
        }

        deleteById(id);
        return Map.of("success", true);
    }

    @Override
    public boolean validateCredentials(String usuario, String contrasena) {
        Optional<Operador> operador = findByUsuario(usuario);
        return operador.isPresent() && operador.get().getContrasena().equals(contrasena);
    }
}
