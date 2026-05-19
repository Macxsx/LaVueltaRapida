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

import com.example.demo.entitys.Operador;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@ActiveProfiles("test")
public class OperadorServiceTestNaive {

    @Autowired
    private OperadorService service;

    // ── findAll ───────────────────────────────────────────────────────────────

    @Test
    public void OperadorService_findAll_ReturnsAllOperadores() {

        List<Operador> result = service.findAll();

        Assertions.assertThat(result).isNotNull();
        Assertions.assertThat(result).hasSize(20);
    }

    // ── findById ──────────────────────────────────────────────────────────────

    @Test
    public void OperadorService_findById_ReturnsPresentWhenExists() {
        Long id = service.findAll().get(0).getId();

        Optional<Operador> result = service.findById(id);

        Assertions.assertThat(result).isPresent();
        Assertions.assertThat(result.get().getId()).isEqualTo(id);
    }

    @Test
    public void OperadorService_findById_ReturnsEmptyWhenNotExists() {

        Optional<Operador> result = service.findById(999L);

        Assertions.assertThat(result).isNotPresent();
    }

    // ── findByUsuario ─────────────────────────────────────────────────────────

    @Test
    public void OperadorService_findByUsuario_ReturnsPresentWhenExists() {

        Optional<Operador> result = service.findByUsuario("op1");

        Assertions.assertThat(result).isPresent();
        Assertions.assertThat(result.get().getUsuario()).isEqualTo("op1");
    }

    @Test
    public void OperadorService_findByUsuario_ReturnsEmptyWhenNotExists() {

        Optional<Operador> result = service.findByUsuario("noexiste");

        Assertions.assertThat(result).isNotPresent();
    }

    // ── create ────────────────────────────────────────────────────────────────

    @Test
    public void OperadorService_create_CreatesSuccessfully() {
        Operador result = service.create("Nuevo Operador", "opNuevo", "pass123");

        Assertions.assertThat(result.getId()).isNotNull();
        Assertions.assertThat(result.getUsuario()).isEqualTo("opNuevo");
    }

    @Test
    public void OperadorService_create_ThrowsWhenUsuarioTaken() {
        Assertions.assertThatThrownBy(() -> service.create("Otro", "op1", "pass"))
                .isInstanceOf(IllegalStateException.class);
    }

    // ── update ────────────────────────────────────────────────────────────────

    @Test
    public void OperadorService_update_UpdatesSuccessfully() {
        Long id = service.findByUsuario("op1").get().getId();

        Operador result = service.update(id, "Nombre Nuevo", "op1", "nuevaPass", "123");

        Assertions.assertThat(result.getNombre()).isEqualTo("Nombre Nuevo");
        Assertions.assertThat(result.getContrasena()).isEqualTo("nuevaPass");
    }

    @Test
    public void OperadorService_update_ThrowsWhenIdNotFound() {

        Assertions.assertThatThrownBy(() -> service.update(999L, "X", "opX", "pass", "pass"))
                .isInstanceOf(NoSuchElementException.class);
    }

    @Test
    public void OperadorService_update_ThrowsWhenCurrentPasswordIsWrong() {
        Long id = service.findByUsuario("op1").get().getId();

        Assertions.assertThatThrownBy(() -> service.update(id, "X", "op1", "nuevaPass", "wrongPass"))
                .isInstanceOf(SecurityException.class);
    }

    @Test
    public void OperadorService_update_ThrowsWhenUsuarioTaken() {
        Long id = service.findByUsuario("op1").get().getId();

        Assertions.assertThatThrownBy(() -> service.update(id, "X", "op2", "nuevaPass", "123"))
                .isInstanceOf(IllegalStateException.class);
    }

    // ── delete ────────────────────────────────────────────────────────────────

    @Test
    public void OperadorService_delete_DeletesSuccessfully() {
        Long id = service.findByUsuario("op1").get().getId();

        service.delete(id);

        Assertions.assertThat(service.findById(id)).isNotPresent();
    }

    @Test
    public void OperadorService_delete_ThrowsWhenNotFound() {

        Assertions.assertThatThrownBy(() -> service.delete(999L))
                .isInstanceOf(NoSuchElementException.class);
    }
}
