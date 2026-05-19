package com.example.demo.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.example.demo.entitys.Cliente;
import com.example.demo.repository.ClienteRepository;
import com.example.demo.repository.UsuarioRepository;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired private UsuarioRepository usuarioRepo;
    @Autowired private ClienteRepository clienteRepo;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        var u = usuarioRepo.findByUsername(username);
        if (u.isPresent()) return u.get();

        // Soporte para login de clientes con email
        if (username.contains("@")) {
            Cliente cliente = clienteRepo.findByEmail(username);
            if (cliente != null) return cliente.getUsuario();
        }

        throw new UsernameNotFoundException("Usuario no encontrado: " + username);
    }
}
