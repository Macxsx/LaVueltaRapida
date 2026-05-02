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
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.entitys.Domiciliario;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@ActiveProfiles("test")
public class DomiciliarioServiceTestNaive {

    @Autowired
    private DomiciliarioService service;

    // ── findAll ───────────────────────────────────────────────────────────────

    @Test
    public void DomiciliarioService_findAll_ReturnsAllDomiciliarios() {

        List<Domiciliario> result = service.findAll();

        Assertions.assertThat(result).isNotNull();
        Assertions.assertThat(result).hasSize(5);
    }

    // ── findById ──────────────────────────────────────────────────────────────

    @Test
    public void DomiciliarioService_findById_ReturnsPresentWhenExists() {
        Long id = service.findAll().get(0).getId();

        Optional<Domiciliario> result = service.findById(id);

        Assertions.assertThat(result).isPresent();
        Assertions.assertThat(result.get().getId()).isEqualTo(id);
    }

    @Test
    public void DomiciliarioService_findById_ReturnsEmptyWhenNotExists() {

        Optional<Domiciliario> result = service.findById(999L);

        Assertions.assertThat(result).isNotPresent();
    }

    // ── save ──────────────────────────────────────────────────────────────────

    @Test
    public void DomiciliarioService_save_PersistsChanges() {
        Domiciliario domiciliario = service.findAll().stream()
                .filter(Domiciliario::isDisponible).findFirst().orElseThrow();
        domiciliario.setNombre("Nombre Modificado");

        Domiciliario result = service.save(domiciliario);

        Assertions.assertThat(result.getNombre()).isEqualTo("Nombre Modificado");
    }

    // ── findFirstDisponible ───────────────────────────────────────────────────

    @Test
    @Transactional
    public void DomiciliarioService_findFirstDisponible_ReturnsPresentWhenExists() {

        Optional<Domiciliario> result = service.findFirstDisponible();

        Assertions.assertThat(result).isPresent();
        Assertions.assertThat(result.get().isDisponible()).isTrue();
    }

    @Test
    @Transactional
    public void DomiciliarioService_findFirstDisponible_ReturnsEmptyWhenNoneAvailable() {
        service.findAll().stream()
                .filter(Domiciliario::isDisponible)
                .forEach(d -> { d.setDisponible(false); service.save(d); });

        Optional<Domiciliario> result = service.findFirstDisponible();

        Assertions.assertThat(result).isNotPresent();
    }

    // ── create ────────────────────────────────────────────────────────────────

    @Test
    public void DomiciliarioService_create_CreatesSuccessfully() {
        Domiciliario nuevo = new Domiciliario("Nuevo Rider", "9999999", "3009999999", true);

        Domiciliario result = service.create(nuevo);

        Assertions.assertThat(result.getId()).isNotNull();
        Assertions.assertThat(result.getCedula()).isEqualTo("9999999");
    }

    @Test
    public void DomiciliarioService_create_ThrowsWhenCedulaTaken() {
        Domiciliario duplicado = new Domiciliario("Otro", "1001001", "3009999998", true);

        Assertions.assertThatThrownBy(() -> service.create(duplicado))
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    public void DomiciliarioService_create_ThrowsWhenCelularTaken() {
        Domiciliario duplicado = new Domiciliario("Otro", "9999998", "3001111111", true);

        Assertions.assertThatThrownBy(() -> service.create(duplicado))
                .isInstanceOf(IllegalStateException.class);
    }

    // ── update ────────────────────────────────────────────────────────────────

    @Test
    public void DomiciliarioService_update_UpdatesSuccessfully() {
        Domiciliario domiciliario = service.findAll().stream()
                .filter(Domiciliario::isDisponible).findFirst().orElseThrow();
        Domiciliario data = new Domiciliario("Nombre Nuevo", "8888888", "3008888888", true);

        Domiciliario result = service.update(domiciliario.getId(), data);

        Assertions.assertThat(result.getNombre()).isEqualTo("Nombre Nuevo");
        Assertions.assertThat(result.getCedula()).isEqualTo("8888888");
    }

    @Test
    public void DomiciliarioService_update_ThrowsWhenIdNotFound() {
        Domiciliario data = new Domiciliario("X", "9999997", "3009999997", true);

        Assertions.assertThatThrownBy(() -> service.update(999L, data))
                .isInstanceOf(NoSuchElementException.class);
    }

    @Test
    public void DomiciliarioService_update_ThrowsWhenCedulaTaken() {
        Domiciliario domiciliario = service.findAll().stream()
                .filter(Domiciliario::isDisponible).findFirst().orElseThrow();
        Domiciliario data = new Domiciliario("X", "1001001", "3009999996", true);

        Assertions.assertThatThrownBy(() -> service.update(domiciliario.getId(), data))
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    public void DomiciliarioService_update_ThrowsWhenCelularTaken() {
        Domiciliario domiciliario = service.findAll().stream()
                .filter(Domiciliario::isDisponible).findFirst().orElseThrow();
        Domiciliario data = new Domiciliario("X", "9999995", "3001111111", true);

        Assertions.assertThatThrownBy(() -> service.update(domiciliario.getId(), data))
                .isInstanceOf(IllegalStateException.class);
    }

    // ── delete ────────────────────────────────────────────────────────────────

    @Test
    public void DomiciliarioService_delete_DeletesSuccessfully() {
        Domiciliario domiciliario = service.findAll().stream()
                .filter(Domiciliario::isDisponible).findFirst().orElseThrow();
        Long id = domiciliario.getId();

        service.delete(id);

        Assertions.assertThat(service.findById(id)).isNotPresent();
    }

    @Test
    public void DomiciliarioService_delete_ThrowsWhenHasActivePedido() {
        Domiciliario conPedidoActivo = service.findAll().stream()
                .filter(d -> !d.isDisponible())
                .findFirst().get();

        Assertions.assertThatThrownBy(() -> service.delete(conPedidoActivo.getId()))
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    public void DomiciliarioService_delete_ThrowsWhenNotFound() {

        Assertions.assertThatThrownBy(() -> service.delete(999L))
                .isInstanceOf(NoSuchElementException.class);
    }
}
