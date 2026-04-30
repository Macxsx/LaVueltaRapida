package com.example.demo.service;

import java.util.List;
import java.util.NoSuchElementException;
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
    public List<Operador> findAll() {
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
    public Operador create(Operador operador) {
        if (repo.findByUsuario(operador.getUsuario()).isPresent()) {
            throw new IllegalStateException(
                    "Ya existe un operador con el usuario '" + operador.getUsuario() + "'.");
        }
        return repo.save(operador);
    }

    @Override
    public Operador update(Long id, String nombre, String usuario, String contrasena, String currentPassword) {
        Operador stored = repo.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Operador no encontrado."));

        if (currentPassword != null && !stored.getContrasena().equals(currentPassword)) {
            throw new SecurityException("La contraseña actual es incorrecta.");
        }
        repo.findByUsuario(usuario)
                .filter(o -> !o.getId().equals(id))
                .ifPresent(o -> { throw new IllegalStateException(
                        "Ya existe un operador con el usuario '" + usuario + "'."); });

        stored.setNombre(nombre);
        stored.setUsuario(usuario);
        if (contrasena != null && !contrasena.isBlank()) {
            stored.setContrasena(contrasena);
        }
        return repo.save(stored);
    }

    @Override
    public void delete(Long id) {
        if (!repo.existsById(id)) {
            throw new NoSuchElementException("Operador no encontrado.");
        }
        repo.deleteById(id);
    }
}
