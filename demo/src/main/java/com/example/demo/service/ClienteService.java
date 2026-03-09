package com.example.demo.service;

import java.util.Collection;

import com.example.demo.entitys.Cliente;

public interface ClienteService {

    public Collection<Cliente> findAll();
    
    public void save(Cliente cliente);
    
    public void deleteById(Long id);
    
    public Cliente findById(Long id);

    public boolean validateCredentials(String username, String password);

    public Cliente findByUsername(String username);
    
    
}
