package com.example.demo.repository;

import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import com.example.demo.entitys.Operador;

@DataJpaTest
public class OperadorRepositoryTest {

    @Autowired
    private OperadorRepository operadorRepo;

    @BeforeEach
    public void setUp() {
        operadorRepo.save(new Operador("Carlos Ruiz",  "carlos",  "pass123"));
        operadorRepo.save(new Operador("María López",  "maria",   "pass456"));
        operadorRepo.save(new Operador("Pedro Gómez",  "pedro",   "pass789"));
    }

    @Test
    public void OperadorRepository_findByUsuario_ReturnsOperadorWhenExists() {

        Optional<Operador> result = operadorRepo.findByUsuario("carlos");

        Assertions.assertThat(result).isPresent();
        Assertions.assertThat(result.get().getUsuario()).isEqualTo("carlos");
    }

    @Test
    public void OperadorRepository_findByUsuario_ReturnsEmptyWhenNotExists() {

        Optional<Operador> result = operadorRepo.findByUsuario("noexiste");

        Assertions.assertThat(result).isNotPresent();
    }
}
