package com.example.demo.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.demo.entitys.Cliente;
import com.example.demo.entitys.Usuario;
import com.example.demo.repository.ClienteRepository;
import com.example.demo.repository.UsuarioRepository;

@Service
public class AuthServiceImpl implements AuthService {

    @Autowired private UsuarioRepository usuarioRepo;
    @Autowired private ClienteRepository clienteRepo;
    @Autowired private CarritoService carritoService;
    @Autowired private PasswordEncoder passwordEncoder;

    @Override
    public LoginResult login(String identifier, String contrasena) {
        if (identifier == null || contrasena == null) {
            throw new IllegalArgumentException("Se requieren usuario y contraseña.");
        }

        Usuario usuario = resolverUsuario(identifier);

        if (usuario == null || !passwordEncoder.matches(contrasena, usuario.getPassword())) {
            throw new SecurityException("Usuario o contraseña incorrectos.");
        }
        if (!usuario.isActivo()) {
            throw new IllegalStateException("Esta cuenta ha sido desactivada.");
        }

        String rolNombre = usuario.getRol().getNombre();

        if ("CLIENTE".equals(rolNombre)) {
            Cliente cliente = clienteRepo.findByUsuario(usuario)
                    .orElseThrow(() -> new SecurityException("Usuario o contraseña incorrectos."));
            Long carritoId = carritoService.findByClienteId(cliente.getId())
                    .map(c -> c.getId()).orElse(null);
            return new LoginResult(usuario.getUsername(), rolNombre.toLowerCase(), cliente.getId(), carritoId);
        }

        return new LoginResult(usuario.getUsername(), rolNombre.toLowerCase());
    }

    @Override
    public boolean verify(String username, String role) {
        if (username == null || role == null) return false;
        return usuarioRepo.findByUsername(username)
                .map(u -> u.getRol().getNombre().equalsIgnoreCase(role))
                .orElse(false);
    }

    private Usuario resolverUsuario(String identifier) {
        Optional<Usuario> porUsername = usuarioRepo.findByUsername(identifier);
        if (porUsername.isPresent()) return porUsername.get();

        if (identifier.contains("@")) {
            Cliente cliente = clienteRepo.findByEmail(identifier);
            if (cliente != null) return cliente.getUsuario();
        }

        return null;
    }
}
