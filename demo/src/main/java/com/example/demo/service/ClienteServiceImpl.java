package com.example.demo.service;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.entitys.Carrito;
import com.example.demo.entitys.Cliente;
import com.example.demo.repository.CarritoRepository;
import com.example.demo.repository.ClienteRepository;

@Service
public class ClienteServiceImpl implements ClienteService {

    @Autowired
    ClienteRepository repo;

    @Autowired
    CarritoRepository carritoRepo;

    @Override
    public Collection<Cliente> findAll() {
        return repo.findAll();
    }

    @Override
    public void save(Cliente cliente) {
        boolean esNuevo = cliente.getId() == null;
        Cliente guardado = repo.save(cliente);
        cliente.setId(guardado.getId());

        if (esNuevo && carritoRepo.findByClienteId(guardado.getId()).isEmpty()) {
            Carrito carrito = new Carrito(guardado);
            carrito.setActivo(false);
            carritoRepo.save(carrito);
        }
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

    @Override
    public boolean isEmailTaken(String email) {
        return repo.findByEmail(email) != null;
    }

    @Override
    public boolean isEmailTakenByOther(String email, Long currentId) {
        Cliente existing = repo.findByEmail(email);
        return existing != null && !existing.getId().equals(currentId);
    }
}
