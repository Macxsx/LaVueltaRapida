package com.example.demo.service;

import java.util.Collection;

import com.example.demo.entitys.Cliente;

public interface ClienteService {

    public Collection<Cliente> findAll();
    
    public void save(Cliente cliente);
    
    public void deleteById(int id);
    
    public Cliente findById(int id);
    
    
}
