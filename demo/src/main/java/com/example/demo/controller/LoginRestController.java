package com.example.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.entitys.Cliente;
import com.example.demo.service.ClienteService;

@CrossOrigin(origins = {"http://localhost:5000", "http://127.0.0.1:5000"})
@RestController
@RequestMapping("/api")
public class LoginRestController {

    @Autowired
    private ClienteService clienteService;

    public static class LoginRequest {
        public String username;
        public String password;

        public LoginRequest() {
        }
    }

    @PostMapping("/login")
    public ResponseEntity<Cliente> login(@RequestBody LoginRequest request) {
        if (request == null || request.username == null || request.password == null) {
            return ResponseEntity.badRequest().build();
        }
        if (clienteService.validateCredentials(request.username, request.password)) {
            Cliente cliente = clienteService.findByUsername(request.username);
            return ResponseEntity.ok(cliente);
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }
}
