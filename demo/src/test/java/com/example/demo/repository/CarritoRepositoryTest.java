package com.example.demo.repository;

import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import com.example.demo.entitys.Carrito;
import com.example.demo.entitys.Cliente;

@DataJpaTest
public class CarritoRepositoryTest {

    @Autowired
    private CarritoRepository carritoRepo;

    @Autowired
    private ClienteRepository clienteRepo;

    private Cliente cliente1;
    private Cliente cliente2;

    @BeforeEach
    public void setUp() {
        cliente1 = clienteRepo.save(new Cliente("Juan", "Pérez", "juan@mail.com", "juanp", "pass1", "Calle 1", "3001000000"));
        cliente2 = clienteRepo.save(new Cliente("Ana", "Gómez", "ana@mail.com", "anag", "pass2", "Calle 2", "3002000000"));

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
