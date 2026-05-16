package com.example.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.demo.entitys.Administrador;
import com.example.demo.entitys.Cliente;
import com.example.demo.entitys.Operador;
import com.example.demo.entitys.Role;

@Service
public class AuthServiceImpl implements AuthService {

    @Autowired
    private AdministradorService administradorService;

    @Autowired
    private OperadorService operadorService;

    @Autowired
    private ClienteService clienteService;

    @Autowired
    private CarritoService carritoService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public LoginResult login(String usuario, String contrasena) {
        if (usuario == null || contrasena == null) {
            throw new IllegalArgumentException("Se requieren usuario y contraseña.");
        }
        Administrador admin = administradorService.findByUsuario(usuario).orElse(null);
        if (admin != null && passwordEncoder.matches(contrasena, admin.getContrasena())) {
            return new LoginResult(admin.getUsuario(), Role.ADMIN.value());
        }

        Operador operador = operadorService.findByUsuario(usuario).orElse(null);
        if (operador != null && passwordEncoder.matches(contrasena, operador.getContrasena())) {
            return new LoginResult(operador.getUsuario(), Role.OPERADOR.value());
        }

        Cliente cliente = usuario.contains("@")
                ? clienteService.findByEmail(usuario)
                : clienteService.findByUsername(usuario);
        if (cliente != null && passwordEncoder.matches(contrasena, cliente.getPassword())) {
            if (!cliente.isActivo()) {
                throw new IllegalStateException("Esta cuenta ha sido desactivada.");
            }
            Long carritoId = carritoService.findByClienteId(cliente.getId())
                    .map(c -> c.getId())
                    .orElse(null);
            return new LoginResult(cliente.getUsername(), Role.CLIENTE.value(), cliente.getId(), carritoId);
        }

        throw new SecurityException("Usuario o contraseña incorrectos.");
    }

    @Override
    public boolean verify(String username, String role) {
        if (username == null || role == null) return false;
        try {
            return switch (Role.valueOf(role.toUpperCase())) {
                case ADMIN    -> administradorService.findByUsuario(username).isPresent();
                case OPERADOR -> operadorService.findByUsuario(username).isPresent();
                case CLIENTE  -> clienteService.findByUsername(username) != null;
            };
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
}
