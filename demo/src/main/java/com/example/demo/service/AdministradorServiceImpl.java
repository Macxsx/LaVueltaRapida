package com.example.demo.service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.demo.entitys.Administrador;
import com.example.demo.repository.AdministradorRepository;
import com.example.demo.repository.UsuarioRepository;

@Service
public class AdministradorServiceImpl implements AdministradorService {

    @Autowired private AdministradorRepository repo;
    @Autowired private UsuarioRepository usuarioRepo;
    @Autowired private PasswordEncoder passwordEncoder;

    @Override
    public List<Administrador> findAll() {
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
    public Administrador update(Long id, String usuario, String contrasena, String currentPassword) {
        Administrador stored = repo.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Administrador no encontrado."));

        if (currentPassword != null && !passwordEncoder.matches(currentPassword, stored.getUsuario().getPassword())) {
            throw new SecurityException("La contraseña actual es incorrecta.");
        }

        usuarioRepo.findByUsername(usuario)
                .filter(u -> !u.getId().equals(stored.getUsuario().getId()))
                .ifPresent(u -> { throw new IllegalStateException(
                        "Ya existe un administrador con el usuario '" + usuario + "'."); });

        stored.getUsuario().setUsername(usuario);
        if (contrasena != null && !contrasena.isBlank()) {
            stored.getUsuario().setPassword(passwordEncoder.encode(contrasena));
        }
        return repo.save(stored);
    }

    @Override
    public void delete(Long id) {
        if (!repo.existsById(id)) {
            throw new NoSuchElementException("Administrador no encontrado.");
        }
        repo.deleteById(id);
    }
}
