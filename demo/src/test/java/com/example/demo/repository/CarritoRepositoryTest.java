package com.example.demo.repository;

import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import com.example.demo.entitys.Carrito;
import com.example.demo.entitys.Cliente;
import com.example.demo.entitys.Rol;
import com.example.demo.entitys.Usuario;

@DataJpaTest
public class CarritoRepositoryTest {

    @Autowired
    private CarritoRepository carritoRepo;

    @Autowired
    private ClienteRepository clienteRepo;

    @Autowired
    private RolRepository rolRepo;

    private Cliente cliente1;
    private Cliente cliente2;

    @BeforeEach
    public void setUp() {
        Rol rol = rolRepo.save(new Rol("CLIENTE"));
        cliente1 = clienteRepo.save(new Cliente("Juan", "Pérez", "juan@mail.com", new Usuario("juanp", "pass1", rol), "Calle 1", "3001000000"));
        cliente2 = clienteRepo.save(new Cliente("Ana",  "Gómez", "ana@mail.com",  new Usuario("anag",  "pass2", rol), "Calle 2", "3002000000"));

        carritoRepo.save(new Carrito(cliente1));
        carritoRepo.save(new Carrito(cliente2));
    }

    @Test
    public void CarritoRepository_findByClienteId_ReturnsCarritoWhenExists() {

        Optional<Carrito> result = carritoRepo.findByClienteId(cliente1.getId());

        Assertions.assertThat(result).isPresent();
        Assertions.assertThat(result.get().getCliente().getId()).isEqualTo(cliente1.getId());
    }

    @Test
    public void CarritoRepository_findByClienteId_ReturnsEmptyWhenNotExists() {

        Optional<Carrito> result = carritoRepo.findByClienteId(999L);

        Assertions.assertThat(result).isNotPresent();
    }
}
