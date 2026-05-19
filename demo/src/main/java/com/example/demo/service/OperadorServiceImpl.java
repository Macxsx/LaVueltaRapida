package com.example.demo.service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.demo.entitys.Operador;
import com.example.demo.entitys.Rol;
import com.example.demo.entitys.Usuario;
import com.example.demo.repository.OperadorRepository;
import com.example.demo.repository.RolRepository;
import com.example.demo.repository.UsuarioRepository;

@Service
public class OperadorServiceImpl implements OperadorService {

    @Autowired private OperadorRepository repo;
    @Autowired private RolRepository rolRepo;
    @Autowired private UsuarioRepository usuarioRepo;
    @Autowired private PasswordEncoder passwordEncoder;

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
    public Operador create(String nombre, String usuario, String contrasena) {
        if (usuarioRepo.findByUsername(usuario).isPresent()) {
            throw new IllegalStateException(
                    "Ya existe un operador con el usuario '" + usuario + "'.");
        }
        Rol rolOperador = rolRepo.findByNombre("OPERADOR")
                .orElseThrow(() -> new IllegalStateException("Rol OPERADOR no encontrado en la base de datos."));
        Usuario u = new Usuario(usuario, passwordEncoder.encode(contrasena), rolOperador);
        return repo.save(new Operador(nombre, u));
    }

    @Override
    public Operador update(Long id, String nombre, String usuario, String contrasena, String currentPassword) {
        Operador stored = repo.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Operador no encontrado."));

        if (currentPassword != null && !passwordEncoder.matches(currentPassword, stored.getUsuario().getPassword())) {
            throw new SecurityException("La contraseña actual es incorrecta.");
        }

        usuarioRepo.findByUsername(usuario)
                .filter(u -> !u.getId().equals(stored.getUsuario().getId()))
                .ifPresent(u -> { throw new IllegalStateException(
                        "Ya existe un operador con el usuario '" + usuario + "'."); });

        stored.setNombre(nombre);
        stored.getUsuario().setUsername(usuario);
        if (contrasena != null && !contrasena.isBlank()) {
            stored.getUsuario().setPassword(passwordEncoder.encode(contrasena));
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
