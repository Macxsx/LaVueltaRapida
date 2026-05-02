package com.example.demo.repository;

import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import com.example.demo.entitys.Administrador;

@DataJpaTest
public class AdministradorRepositoryTest {

    @Autowired
    private AdministradorRepository adminRepo;

    @BeforeEach
    public void setUp() {
        adminRepo.save(new Administrador("admin1", "pass123"));
        adminRepo.save(new Administrador("admin2", "pass456"));
        adminRepo.save(new Administrador("superadmin", "root999"));
    }

    @Test
    public void AdministradorRepository_findByUsuario_ReturnsAdminWhenExists() {

        Optional<Administrador> result = adminRepo.findByUsuario("admin1");

        Assertions.assertThat(result).isPresent();
        Assertions.assertThat(result.get().getUsuario()).isEqualTo("admin1");
    }

    @Test
    public void AdministradorRepository_findByUsuario_ReturnsEmptyWhenNotExists() {

        Optional<Administrador> result = adminRepo.findByUsuario("noexiste");

        Assertions.assertThat(result).isNotPresent();
    }
}
