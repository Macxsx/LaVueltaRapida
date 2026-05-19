package com.example.demo.service;

import java.util.Collection;
import java.util.EnumSet;
import java.util.NoSuchElementException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.demo.entitys.Carrito;
import com.example.demo.entitys.Cliente;
import com.example.demo.entitys.Rol;
import com.example.demo.entitys.Usuario;
import com.example.demo.exception.CuentaDesactivadaException;
import com.example.demo.entitys.EstadoPedido;
import com.example.demo.repository.CarritoRepository;
import com.example.demo.repository.ClienteRepository;
import com.example.demo.repository.PedidoRepository;
import com.example.demo.repository.RolRepository;
import com.example.demo.repository.UsuarioRepository;

@Service
public class ClienteServiceImpl implements ClienteService {

    @Autowired private ClienteRepository repo;
    @Autowired private CarritoRepository carritoRepo;
    @Autowired private PedidoRepository pedidoRepo;
    @Autowired private RolRepository rolRepo;
    @Autowired private UsuarioRepository usuarioRepo;
    @Autowired private PasswordEncoder passwordEncoder;

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
    public Cliente create(String name, String apellido, String email,
                          String username, String password, String direccion, String telefono) {
        if (usuarioRepo.findByUsername(username).isPresent()) {
            throw new IllegalStateException(
                    "El nombre de usuario '" + username + "' ya está en uso.");
        }
        if (repo.findByEmail(email) != null) {
            throw new IllegalStateException(
                    "El correo '" + email + "' ya está registrado.");
        }

        Rol rolCliente = rolRepo.findByNombre("CLIENTE")
                .orElseThrow(() -> new IllegalStateException("Rol CLIENTE no encontrado en la base de datos."));
        Usuario usuario = new Usuario(username, passwordEncoder.encode(password), rolCliente);
        Cliente cliente = new Cliente(name, apellido, email, usuario, direccion, telefono);
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

        if (currentPassword != null && !passwordEncoder.matches(currentPassword, stored.getUsuario().getPassword())) {
            throw new SecurityException("La contraseña actual es incorrecta.");
        }

        usuarioRepo.findByUsername(username)
                .filter(u -> !u.getId().equals(stored.getUsuario().getId()))
                .ifPresent(u -> { throw new IllegalStateException(
                        "El nombre de usuario '" + username + "' ya está en uso."); });

        Cliente byEmail = repo.findByEmail(email);
        if (byEmail != null && !byEmail.getId().equals(id)) {
            throw new IllegalStateException(
                    "El correo '" + email + "' ya está registrado.");
        }

        stored.setName(name);
        stored.setApellido(apellido);
        stored.setEmail(email);
        stored.getUsuario().setUsername(username);
        if (password != null && !password.isBlank()) {
            stored.getUsuario().setPassword(passwordEncoder.encode(password));
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
        if (cliente == null || !passwordEncoder.matches(password, cliente.getUsuario().getPassword())) {
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
