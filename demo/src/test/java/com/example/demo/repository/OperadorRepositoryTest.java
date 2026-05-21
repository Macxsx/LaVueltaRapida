package com.example.demo.repository;

import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import com.example.demo.entitys.Operador;
import com.example.demo.entitys.Rol;
import com.example.demo.entitys.Usuario;

@DataJpaTest
public class OperadorRepositoryTest {

    @Autowired
    private OperadorRepository operadorRepo;

    @Autowired
    private RolRepository rolRepo;

    private Rol rolOperador;

    @BeforeEach
    public void setUp() {
        rolOperador = rolRepo.save(new Rol("OPERADOR"));
        operadorRepo.save(new Operador("Carlos Ruiz", new Usuario("carlos", "pass123", rolOperador)));
        operadorRepo.save(new Operador("María López", new Usuario("maria",  "pass456", rolOperador)));
        operadorRepo.save(new Operador("Pedro Gómez", new Usuario("pedro",  "pass789", rolOperador)));
    }

    @Test
    public void OperadorRepository_findByUsuario_ReturnsOperadorWhenExists() {

        Optional<Operador> result = operadorRepo.findByUsuario("carlos");

        Assertions.assertThat(result).isPresent();
        Assertions.assertThat(result.get().getUsuario().getUsername()).isEqualTo("carlos");
    }

    @Test
    public void OperadorRepository_findByUsuario_ReturnsEmptyWhenNotExists() {

        Optional<Operador> result = operadorRepo.findByUsuario("noexiste");

        Assertions.assertThat(result).isNotPresent();
    }
}
