package com.example.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.entitys.Administrador;
import com.example.demo.entitys.Cliente;
import com.example.demo.entitys.Operador;

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

    @Override
    public LoginResult login(String usuario, String contrasena) {
        if (usuario == null || contrasena == null) {
            throw new IllegalArgumentException("Se requieren usuario y contraseña.");
        }
        Administrador admin = administradorService.findByUsuario(usuario).orElse(null);
        if (admin != null && admin.getContrasena().equals(contrasena)) {
            return new LoginResult(admin.getUsuario(), "admin");
        }

        Operador operador = operadorService.findByUsuario(usuario).orElse(null);
        if (operador != null && operador.getContrasena().equals(contrasena)) {
            return new LoginResult(operador.getUsuario(), "operador");
        }

        Cliente cliente = clienteService.findByUsername(usuario);
        if (cliente != null && cliente.getPassword().equals(contrasena)) {
            if (!cliente.isActivo()) {
                throw new IllegalStateException("Esta cuenta ha sido desactivada.");
            }
            Long carritoId = carritoService.findByClienteId(cliente.getId())
                    .map(c -> c.getId())
                    .orElse(null);
            return new LoginResult(cliente.getUsername(), "cliente", cliente.getId(), carritoId);
        }

        throw new SecurityException("Usuario o contraseña incorrectos.");
    }
}
