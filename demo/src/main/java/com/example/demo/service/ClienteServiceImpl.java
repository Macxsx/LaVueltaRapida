package com.example.demo.service;

import java.util.Collection;
import java.util.EnumSet;
import java.util.NoSuchElementException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.entitys.Carrito;
import com.example.demo.entitys.Cliente;
import com.example.demo.exception.CuentaDesactivadaException;
import com.example.demo.entitys.EstadoPedido;
import com.example.demo.repository.CarritoRepository;
import com.example.demo.repository.ClienteRepository;
import com.example.demo.repository.PedidoRepository;

@Service
public class ClienteServiceImpl implements ClienteService {

    @Autowired
    private ClienteRepository repo;

    @Autowired
    private CarritoRepository carritoRepo;

    @Autowired
    private PedidoRepository pedidoRepo;

    @Override
    public Collection<Cliente> findAll() {
        return repo.findAll();
    }

    @Override
    public Cliente findById(Long id) {
        return repo.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Cliente no encontrado."));
    }

    @Override
    public Cliente findByUsername(String username) {
        return repo.findByUsername(username);
    }

    @Override
    public Cliente findByEmail(String email) {
        return repo.findByEmail(email);
    }

    @Override
    public Cliente create(Cliente cliente) {
        if (repo.findByUsername(cliente.getUsername()) != null) {
            throw new IllegalStateException(
                    "El nombre de usuario '" + cliente.getUsername() + "' ya está en uso.");
        }
        if (repo.findByEmail(cliente.getEmail()) != null) {
            throw new IllegalStateException(
                    "El correo '" + cliente.getEmail() + "' ya está registrado.");
        }
        Cliente guardado = repo.save(cliente);
        if (carritoRepo.findByClienteId(guardado.getId()).isEmpty()) {
            Carrito carrito = new Carrito(guardado);
            carrito.setActivo(false);
            carritoRepo.save(carrito);
        }
        return guardado;
    }

    @Override
    public Cliente update(Long id, String name, String apellido, String email, String username,
                          String password, String direccion, String telefono, String currentPassword) {
        Cliente stored = repo.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Cliente no encontrado."));

        if (currentPassword != null && !stored.getPassword().equals(currentPassword)) {
            throw new SecurityException("La contraseña actual es incorrecta.");
        }
        Cliente byUsername = repo.findByUsername(username);
        if (byUsername != null && !byUsername.getId().equals(id)) {
            throw new IllegalStateException(
                    "El nombre de usuario '" + username + "' ya está en uso.");
        }
        Cliente byEmail = repo.findByEmail(email);
        if (byEmail != null && !byEmail.getId().equals(id)) {
            throw new IllegalStateException(
                    "El correo '" + email + "' ya está registrado.");
        }

        stored.setName(name);
        stored.setApellido(apellido);
        stored.setEmail(email);
        stored.setUsername(username);
        if (password != null && !password.isBlank()) {
            stored.setPassword(password);
        }
        stored.setDireccion(direccion);
        stored.setTelefono(telefono);
        return repo.save(stored);
    }

    @Override
    public Cliente loginCliente(String username, String password) {
        if (username == null || password == null) {
            throw new IllegalArgumentException("Se requieren usuario y contraseña.");
        }
        Cliente cliente = repo.findByUsername(username);
        if (cliente == null || !cliente.getPassword().equals(password)) {
            throw new SecurityException("Usuario o contraseña incorrectos.");
        }
        if (!cliente.isActivo()) {
            throw new IllegalStateException("Esta cuenta ha sido desactivada.");
        }
        return cliente;
    }

    @Override
    public void deleteOrDeactivate(Long id) {
        Cliente stored = repo.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Cliente no encontrado."));

        if (pedidoRepo.existsByClienteIdAndEstadoNotIn(id, EnumSet.of(EstadoPedido.ENTREGADO, EstadoPedido.CANCELADO))) {
            throw new IllegalStateException(
                    "No se puede eliminar la cuenta porque tiene pedidos activos.");
        }
        if (pedidoRepo.existsByClienteId(id)) {
            stored.setActivo(false);
            repo.save(stored);
            throw new CuentaDesactivadaException(
                    "Cuenta desactivada. El historial de pedidos se ha conservado.");
        }
        repo.deleteById(id);
    }
}
