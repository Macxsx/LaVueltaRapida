package com.example.demo.repository;

import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import com.example.demo.entitys.Administrador;
import com.example.demo.entitys.Rol;
import com.example.demo.entitys.Usuario;

@DataJpaTest
public class AdministradorRepositoryTest {

    @Autowired
    private AdministradorRepository adminRepo;

    @Autowired
    private RolRepository rolRepo;

    private Rol rolAdmin;

    @BeforeEach
    public void setUp() {
        rolAdmin = rolRepo.save(new Rol("ADMIN"));
        adminRepo.save(new Administrador(new Usuario("admin1",     "pass123", rolAdmin)));
        adminRepo.save(new Administrador(new Usuario("admin2",     "pass456", rolAdmin)));
        adminRepo.save(new Administrador(new Usuario("superadmin", "root999", rolAdmin)));
    }

    @Test
    public void AdministradorRepository_findByUsuario_ReturnsAdminWhenExists() {

        Optional<Administrador> result = adminRepo.findByUsuario("admin1");

        Assertions.assertThat(result).isPresent();
        Assertions.assertThat(result.get().getUsuario().getUsername()).isEqualTo("admin1");
    }

    @Test
    public void AdministradorRepository_findByUsuario_ReturnsEmptyWhenNotExists() {

        Optional<Administrador> result = adminRepo.findByUsuario("noexiste");

        Assertions.assertThat(result).isNotPresent();
    }
}
