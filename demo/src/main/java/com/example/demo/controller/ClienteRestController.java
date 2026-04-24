package com.example.demo.controller;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.entitys.Cliente;
import com.example.demo.service.ClienteService;

@CrossOrigin(origins = {"http://localhost:5000", "http://127.0.0.1:5000"})
@RestController
@RequestMapping("/clientes")
public class ClienteRestController {

    @Autowired
    private ClienteService clienteService;

    @GetMapping
    public Collection<Cliente> findAll() {
        return clienteService.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Cliente> findById(@PathVariable Long id) {
        Cliente cliente = clienteService.findById(id);
        return cliente == null ? ResponseEntity.notFound().build() : ResponseEntity.ok(cliente);
    }

    @PostMapping
    public ResponseEntity<Cliente> add(@RequestBody Cliente cliente) {
        clienteService.save(cliente);
        return ResponseEntity.ok(cliente);
    }

    static class UpdateClienteRequest {
        public String name;
        public String apellido;
        public String email;
        public String username;
        public String password;
        public String direccion;
        public String telefono;
        public String currentPassword;
    }

    @PutMapping("/{id}")
    public ResponseEntity<Cliente> update(@PathVariable Long id, @RequestBody UpdateClienteRequest req) {
        Cliente stored = clienteService.findById(id);
        if (stored == null) {
            return ResponseEntity.notFound().build();
        }
        if (req.currentPassword != null && !stored.getPassword().equals(req.currentPassword)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        stored.setName(req.name);
        stored.setApellido(req.apellido);
        stored.setEmail(req.email);
        stored.setUsername(req.username);
        if (req.password != null && !req.password.isBlank()) {
            stored.setPassword(req.password);
        }
        stored.setDireccion(req.direccion);
        stored.setTelefono(req.telefono);
        clienteService.save(stored);
        return ResponseEntity.ok(stored);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (clienteService.findById(id) == null) {
            return ResponseEntity.notFound().build();
        }
        clienteService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
