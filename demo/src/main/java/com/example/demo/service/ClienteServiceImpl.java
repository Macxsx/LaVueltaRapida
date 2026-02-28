package com.example.demo.service;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;

import com.example.demo.entitys.Cliente;
import com.example.demo.repository.ClienteRepository;

public class ClienteServiceImpl implements ClienteService {
    
    @Autowired
    ClienteRepository repo;

    @Override
    public Collection<Cliente> findAll() {
        return repo.findAll();
    }

    @Override
    public void save(Cliente cliente) {
        repo.save(cliente);
        
    }

    @Override
    public void deleteById(int id) {
        repo.deleteById(id);
        
    }

    @Override
    public Cliente findById(int id) {
        return repo.findById(id);
    }

    
}
