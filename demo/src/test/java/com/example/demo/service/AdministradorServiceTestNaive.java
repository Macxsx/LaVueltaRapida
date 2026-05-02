package com.example.demo.service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import com.example.demo.entitys.Administrador;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@ActiveProfiles("test")
public class AdministradorServiceTestNaive {

    @Autowired
    private AdministradorService service;

    @Test
    public void AdministradorService_findAll_ReturnsAllAdministradores() {

        List<Administrador> result = service.findAll();

        Assertions.assertThat(result).isNotNull();
        Assertions.assertThat(result).hasSize(5);
    }

    @Test
    public void AdministradorService_findById_ReturnsPresentWhenExists() {
        Long id = service.findAll().get(0).getId();

        Optional<Administrador> result = service.findById(id);

        Assertions.assertThat(result).isPresent();
        Assertions.assertThat(result.get().getId()).isEqualTo(id);
    }

    @Test
    public void AdministradorService_findById_ReturnsEmptyWhenNotExists() {

        Optional<Administrador> result = service.findById(999L);

        Assertions.assertThat(result).isNotPresent();
    }

    @Test
    public void AdministradorService_findByUsuario_ReturnsPresentWhenExists() {

        Optional<Administrador> result = service.findByUsuario("admin1");

        Assertions.assertThat(result).isPresent();
        Assertions.assertThat(result.get().getUsuario()).isEqualTo("admin1");
    }

    @Test
    public void AdministradorService_findByUsuario_ReturnsEmptyWhenNotExists() {

        Optional<Administrador> result = service.findByUsuario("noexiste");

        Assertions.assertThat(result).isNotPresent();
    }

    @Test
    public void AdministradorService_update_UpdatesSuccessfully() {
        Long id = service.findAll().get(0).getId();

        Administrador result = service.update(id, "adminActualizado", "nuevaPass", "123");

        Assertions.assertThat(result.getUsuario()).isEqualTo("adminActualizado");
        Assertions.assertThat(result.getContrasena()).isEqualTo("nuevaPass");
    }

    @Test
    public void AdministradorService_update_ThrowsWhenIdNotFound() {

        Assertions.assertThatThrownBy(() -> service.update(999L, "admin1", "123", "123"))
                .isInstanceOf(NoSuchElementException.class);
    }

    @Test
    public void AdministradorService_update_ThrowsWhenCurrentPasswordIsWrong() {
        Long id = service.findAll().get(0).getId();

        Assertions.assertThatThrownBy(() -> service.update(id, "admin1", "nuevaPass", "wrongPass"))
                .isInstanceOf(SecurityException.class);
    }

    @Test
    public void AdministradorService_update_ThrowsWhenUsuarioAlreadyTaken() {
        Long id = service.findAll().get(0).getId();

        Assertions.assertThatThrownBy(() -> service.update(id, "admin2", "123", "123"))
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    public void AdministradorService_delete_DeletesSuccessfully() {
        Long id = service.findAll().get(0).getId();

        service.delete(id);

        Assertions.assertThat(service.findById(id)).isNotPresent();
    }

    @Test
    public void AdministradorService_delete_ThrowsWhenNotFound() {

        Assertions.assertThatThrownBy(() -> service.delete(999L))
                .isInstanceOf(NoSuchElementException.class);
    }
}
