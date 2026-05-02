package com.example.demo.repository;

import java.util.List;
import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import com.example.demo.entitys.Domiciliario;

@DataJpaTest
public class DomiciliarioRepositoryTest {

    @Autowired
    private DomiciliarioRepository domiciliarioRepo;

    private Domiciliario dom1;

    @BeforeEach
    public void setUp() {
        dom1 = domiciliarioRepo.save(new Domiciliario("Carlos Ruiz",  "123456", "3101000001", true));
        domiciliarioRepo.save(new Domiciliario("Luis Mora",    "789012", "3101000002", true));
        domiciliarioRepo.save(new Domiciliario("Pedro Díaz",          "345678", "3101000003", false));
        domiciliarioRepo.save(new Domiciliario("Andrés Castro",       "901234", "3101000004", false));
    }

    @Test
    public void DomiciliarioRepository_findByCedula_ReturnsWhenExists() {

        Optional<Domiciliario> result = domiciliarioRepo.findByCedula("123456");

        Assertions.assertThat(result).isPresent();
        Assertions.assertThat(result.get().getCedula()).isEqualTo("123456");
    }

    @Test
    public void DomiciliarioRepository_findByCedula_ReturnsEmptyWhenNotExists() {

        Optional<Domiciliario> result = domiciliarioRepo.findByCedula("000000");

        Assertions.assertThat(result).isNotPresent();
    }

    @Test
    public void DomiciliarioRepository_findByCelular_ReturnsWhenExists() {

        Optional<Domiciliario> result = domiciliarioRepo.findByCelular("3101000002");

        Assertions.assertThat(result).isPresent();
        Assertions.assertThat(result.get().getCelular()).isEqualTo("3101000002");
    }

    @Test
    public void DomiciliarioRepository_findByCelular_ReturnsEmptyWhenNotExists() {

        Optional<Domiciliario> result = domiciliarioRepo.findByCelular("0000000000");

        Assertions.assertThat(result).isNotPresent();
    }

    @Test
    public void DomiciliarioRepository_findByDisponibleTrue_ReturnsOnlyAvailable() {

        List<Domiciliario> result = domiciliarioRepo.findByDisponibleTrue();

        Assertions.assertThat(result).isNotEmpty();
        Assertions.assertThat(result).hasSize(2);
        Assertions.assertThat(result).allMatch(Domiciliario::isDisponible);
    }

    @Test
    public void DomiciliarioRepository_findFirstByDisponibleTrueOrderByIdAsc_ReturnsFirstAvailable() {

        Optional<Domiciliario> result = domiciliarioRepo.findFirstByDisponibleTrueOrderByIdAsc();

        Assertions.assertThat(result).isPresent();
        Assertions.assertThat(result.get().getId()).isEqualTo(dom1.getId());
        Assertions.assertThat(result.get().isDisponible()).isTrue();
    }
}
