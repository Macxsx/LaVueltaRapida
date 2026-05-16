package com.example.demo.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.example.demo.entitys.Cliente;
import com.example.demo.repository.AdministradorRepository;
import com.example.demo.repository.ClienteRepository;
import com.example.demo.repository.OperadorRepository;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired private AdministradorRepository adminRepo;
    @Autowired private OperadorRepository operadorRepo;
    @Autowired private ClienteRepository clienteRepo;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Administrador
        var admin = adminRepo.findByUsuario(username);
        if (admin.isPresent()) return admin.get();

        // Operador
        var operador = operadorRepo.findByUsuario(username);
        if (operador.isPresent()) return operador.get();

        // Cliente (por username o email)
        Cliente cliente = username.contains("@")
                ? clienteRepo.findByEmail(username)
                : clienteRepo.findByUsername(username);
        if (cliente != null) return cliente;

        throw new UsernameNotFoundException("Usuario no encontrado: " + username);
    }
}
