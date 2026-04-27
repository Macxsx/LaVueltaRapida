package com.example.demo.controller;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.entitys.Administrador;
import com.example.demo.entitys.Cliente;
import com.example.demo.entitys.Operador;
import com.example.demo.repository.AdministradorRepository;
import com.example.demo.repository.CarritoRepository;
import com.example.demo.repository.OperadorRepository;
import com.example.demo.service.ClienteService;
import com.fasterxml.jackson.annotation.JsonInclude;

@CrossOrigin(origins = {"http://localhost:5000", "http://127.0.0.1:5000"})
@RestController
@RequestMapping("/auth")
public class AuthRestController {

    @Autowired
    private AdministradorRepository administradorRepository;

    @Autowired
    private OperadorRepository operadorRepository;

    @Autowired
    private ClienteService clienteService;

    @Autowired
    private CarritoRepository carritoRepository;

    static class LoginRequest {
        public String usuario;
        public String contrasena;
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    static class LoginResponse {
        public String username;
        public String role;
        public Long clienteId;
        public Long carritoId;

        LoginResponse(String username, String role) {
            this.username = username;
            this.role = role;
        }

        LoginResponse(String username, String role, Long clienteId, Long carritoId) {
            this.username = username;
            this.role = role;
            this.clienteId = clienteId;
            this.carritoId = carritoId;
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        if (request == null || request.usuario == null || request.contrasena == null) {
            return ResponseEntity.badRequest().build();
        }

        Optional<Administrador> admin = administradorRepository.findByUsuario(request.usuario);
        if (admin.isPresent() && admin.get().getContrasena().equals(request.contrasena)) {
            return ResponseEntity.ok(new LoginResponse(admin.get().getUsuario(), "admin"));
        }

        Optional<Operador> operador = operadorRepository.findByUsuario(request.usuario);
        if (operador.isPresent() && operador.get().getContrasena().equals(request.contrasena)) {
            return ResponseEntity.ok(new LoginResponse(operador.get().getUsuario(), "operador"));
        }

        Cliente cliente = clienteService.findByUsername(request.usuario);
        if (cliente != null && cliente.getPassword().equals(request.contrasena)) {
            if (!cliente.isActivo()) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("error", "Esta cuenta ha sido desactivada."));
            }
            Long carritoId = carritoRepository.findByClienteId(cliente.getId())
                    .map(c -> c.getId())
                    .orElse(null);
            return ResponseEntity.ok(new LoginResponse(cliente.getUsername(), "cliente", cliente.getId(), carritoId));
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }
}
