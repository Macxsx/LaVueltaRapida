package com.example.demo.repository;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import com.example.demo.entitys.Cliente;

@DataJpaTest
public class ClienteRepositoryTest {

    @Autowired
    private ClienteRepository clienteRepo;

    @BeforeEach
    public void setUp() {
        clienteRepo.save(new Cliente("Juan",  "Pérez", "juan@mail.com",  "juanp",  "pass1", "Calle 1", "3001000001"));
        clienteRepo.save(new Cliente("Ana",   "Gómez", "ana@mail.com",   "anag",   "pass2", "Calle 2", "3001000002"));
        clienteRepo.save(new Cliente("Pedro", "Díaz",  "pedro@mail.com", "pedrod", "pass3", "Calle 3", "3001000003"));
    }

    @Test
    public void ClienteRepository_findByUsername_ReturnsClienteWhenExists() {

        Cliente result = clienteRepo.findByUsername("juanp");

        Assertions.assertThat(result).isNotNull();
        Assertions.assertThat(result.getUsername()).isEqualTo("juanp");
    }

    @Test
    public void ClienteRepository_findByUsername_ReturnsNullWhenNotExists() {

        Cliente result = clienteRepo.findByUsername("noexiste");

        Assertions.assertThat(result).isNull();
    }

    @Test
    public void ClienteRepository_findByEmail_ReturnsClienteWhenExists() {

        Cliente result = clienteRepo.findByEmail("ana@mail.com");

        Assertions.assertThat(result).isNotNull();
        Assertions.assertThat(result.getEmail()).isEqualTo("ana@mail.com");
    }

    @Test
    public void ClienteRepository_findByEmail_ReturnsNullWhenNotExists() {

        Cliente result = clienteRepo.findByEmail("noexiste@mail.com");

        Assertions.assertThat(result).isNull();
    }
}
