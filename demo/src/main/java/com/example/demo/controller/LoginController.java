package com.example.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.example.demo.entitys.Cliente;
import com.example.demo.service.ClienteService;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:5000")
public class LoginController {

    @Autowired
    private ClienteService clienteService;

    // 🔐 LOGIN
    @PostMapping("/login")
    public Map<String, Object> login(@RequestParam String username,
                                     @RequestParam String password) {

        Map<String, Object> response = new HashMap<>();

        // Admin login (kept from your original logic)
        if (username.equals("admin") && password.equals("admin")) {
            response.put("success", true);
            response.put("role", "admin");
            response.put("user", "Admin");
            response.put("userId", 0);
            return response;
        }

        // Cliente login
        if (clienteService.validateCredentials(username, password)) {
            Cliente cliente = clienteService.findByUsername(username);

            response.put("success", true);
            response.put("role", "cliente");
            response.put("user", cliente.getName());
            response.put("userId", cliente.getId());
            response.put("cliente", cliente);

        } else {
            response.put("success", false);
            response.put("message", "Invalid credentials");
        }

        return response;
    }

    // 📝 REGISTER
    @PostMapping("/register")
    public Map<String, Object> register(@RequestBody Cliente cliente) {

        Map<String, Object> response = new HashMap<>();

        if (clienteService.isUsernameTaken(cliente.getUsername())) {
            response.put("success", false);
            response.put("message", "Username already taken");
            return response;
        }

        clienteService.save(cliente);

        response.put("success", true);
        response.put("user", cliente.getName());
        response.put("userId", cliente.getId());
        response.put("cliente", cliente);

        return response;
    }

    // 👤 GET PROFILE
    @GetMapping("/perfil/{id}")
    public Cliente getPerfil(@PathVariable Long id) {
        return clienteService.findById(id);
    }

    // ✏️ UPDATE PROFILE
    @PutMapping("/perfil/{id}")
    public Map<String, Object> updatePerfil(@PathVariable Long id,
                                            @RequestBody Cliente cliente) {

        Map<String, Object> response = new HashMap<>();

        Cliente stored = clienteService.findById(id);

        if (!stored.getPassword().equals(cliente.getPassword())) {
            response.put("success", false);
            response.put("message", "Wrong password");
            return response;
        }

        if (clienteService.isUsernameTakenByOther(cliente.getUsername(), id)) {
            response.put("success", false);
            response.put("message", "Username already taken");
            return response;
        }

        stored.setName(cliente.getName());
        stored.setApellido(cliente.getApellido());
        stored.setEmail(cliente.getEmail());
        stored.setUsername(cliente.getUsername());
        stored.setDireccion(cliente.getDireccion());
        stored.setTelefono(cliente.getTelefono());

        clienteService.save(stored);

        response.put("success", true);
        response.put("cliente", stored);

        return response;
    }

    // 🗑️ DELETE PROFILE
    @DeleteMapping("/perfil/{id}")
    public Map<String, Object> deletePerfil(@PathVariable Long id) {

        Map<String, Object> response = new HashMap<>();

        try {
            clienteService.deleteById(id);
            response.put("success", true);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error deleting user");
        }

        return response;
    }
}