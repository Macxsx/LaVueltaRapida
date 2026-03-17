package com.example.demo.service;

import java.util.Collection;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.entitys.Carrito;
import com.example.demo.entitys.Cliente;
import com.example.demo.entitys.Pedido;
import com.example.demo.repository.CarritoRepository;
import com.example.demo.repository.ClienteRepository;
import com.example.demo.repository.PedidoRepository;

@Service
public class ClienteServiceImpl implements ClienteService {

    @Autowired
    ClienteRepository repo;

    @Autowired
    PedidoRepository pedidoRepo;

    @Autowired
    CarritoRepository carritoRepo;

    @Override
    public Collection<Cliente> findAll() {
        return repo.findAll();
    }

    @Override
    public void save(Cliente cliente) {
        repo.save(cliente);
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        Cliente cliente = repo.findById(id).orElse(null);
        if (cliente == null) return;

        // 1. Eliminar todos los pedidos de este cliente (su cascade elimina sus lineasPedido)
        List<Pedido> pedidos = pedidoRepo.findByClienteId(id);
        pedidoRepo.deleteAll(pedidos);

        // 2. Encontrar y desvincular el carrito (romper FK circular carrito → cliente)
        Carrito carrito = carritoRepo.findByClienteId(id).orElse(null);
        if (carrito != null) {
            carrito.setCliente(null);
            carritoRepo.save(carrito);
        }

        // 3. Eliminar el cliente (ya no hay FKs apuntando a él)
        repo.deleteById(id);

        // 4. Eliminar el carrito huérfano (cascade elimina sus lineasPedido)
        if (carrito != null) {
            carritoRepo.delete(carrito);
        }
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
