package com.example.demo.service;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.entitys.Cliente;
import com.example.demo.repository.ClienteRepository;

@Service
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
    public void deleteById(Long id) {
        repo.deleteById(id);
    }

    @Override
    public Cliente findById(Long id) {
        return repo.findById(id).orElse(null);
    }

    @Override
    public boolean validateCredentials(String username, String password) {
        Cliente cliente = repo.findByUsername(username);
        return cliente != null && cliente.getPassword().equals(password);
    }

    @Override
    public Cliente findByUsername(String username) {
        return repo.findByUsername(username);
    }

    @Override
    public boolean isUsernameTaken(String username) {
        return repo.findByUsername(username) != null;
    }

    @Override
    public boolean isUsernameTakenByOther(String username, Long currentId) {
        Cliente existing = repo.findByUsername(username);
        return existing != null && !existing.getId().equals(currentId);
    }
}
