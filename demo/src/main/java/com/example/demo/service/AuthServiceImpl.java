package com.example.demo.service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.entitys.Administrador;
import com.example.demo.entitys.Cliente;
import com.example.demo.entitys.Operador;
import com.example.demo.repository.CarritoRepository;

@Service
public class AuthServiceImpl implements AuthService {

    @Autowired
    private AdministradorService administradorService;

    @Autowired
    private OperadorService operadorService;

    @Autowired
    private ClienteService clienteService;

    @Autowired
    private CarritoRepository carritoRepository;

    @Override
    public Map<String, Object> login(String usuario, String contrasena) {
        if (usuario == null || contrasena == null) {
            return Map.of(
                "success", false,
                "error", "Usuario y contraseña son requeridos."
            );
        }

        // Intentar autenticación como administrador
        if (administradorService.validateCredentials(usuario, contrasena)) {
            Optional<Administrador> adminOpt = administradorService.findByUsuario(usuario);
            if (adminOpt.isPresent()) {
                return Map.of(
                    "success", true,
                    "username", adminOpt.get().getUsuario(),
                    "role", "admin"
                );
            }
        }

        // Intentar autenticación como operador
        if (operadorService.validateCredentials(usuario, contrasena)) {
            Optional<Operador> operadorOpt = operadorService.findByUsuario(usuario);
            if (operadorOpt.isPresent()) {
                return Map.of(
                    "success", true,
                    "username", operadorOpt.get().getUsuario(),
                    "role", "operador"
                );
            }
        }

        // Intentar autenticación como cliente
        Cliente cliente = clienteService.findByUsername(usuario);
        if (cliente != null && cliente.getPassword().equals(contrasena)) {
            if (!cliente.isActivo()) {
                return Map.of(
                    "success", false,
                    "error", "Esta cuenta ha sido desactivada."
                );
            }

            Long carritoId = carritoRepository.findByClienteId(cliente.getId())
                    .map(c -> c.getId())
                    .orElse(null);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("username", cliente.getUsername());
            response.put("role", "cliente");
            response.put("clienteId", cliente.getId());
            if (carritoId != null) {
                response.put("carritoId", carritoId);
            }
            return response;
        }

        return Map.of(
            "success", false,
            "error", "Credenciales inválidas."
        );
    }
}
