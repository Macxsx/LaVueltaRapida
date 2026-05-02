package com.example.demo.service;

import java.util.Collection;
import java.util.NoSuchElementException;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import com.example.demo.entitys.Comida;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@ActiveProfiles("test")
public class ComidaServiceTestNaive {

    @Autowired
    private ComidaService service;

    @Autowired
    private CategoriaService categoriaService;

    // ── findAll ───────────────────────────────────────────────────────────────

    @Test
    public void ComidaService_findAll_ReturnsAllComidas() {

        Collection<Comida> result = service.findAll();

        Assertions.assertThat(result).isNotNull();
        Assertions.assertThat(result).isNotEmpty();
    }

    // ── findById ──────────────────────────────────────────────────────────────

    @Test
    public void ComidaService_findById_ReturnsComidaWhenExists() {
        Long id = service.findAll().iterator().next().getId();

        Comida result = service.findById(id);

        Assertions.assertThat(result).isNotNull();
        Assertions.assertThat(result.getId()).isEqualTo(id);
    }

    @Test
    public void ComidaService_findById_ThrowsWhenNotExists() {

        Assertions.assertThatThrownBy(() -> service.findById(999L))
                .isInstanceOf(NoSuchElementException.class);
    }

    // ── estaEnPedido ──────────────────────────────────────────────────────────

    @Test
    public void ComidaService_estaEnPedido_ReturnsTrueWhenInPedido() {
        Long id = service.findAll().iterator().next().getId();

        Assertions.assertThat(service.estaEnPedido(id)).isTrue();
    }

    @Test
    public void ComidaService_estaEnPedido_ReturnsFalseWhenNotInPedido() {
        Long categoryId = categoriaService.findAll().iterator().next().getId();
        Comida nueva = service.create("Pizza Sin Pedido", "desc", 30000, null, true, categoryId);

        Assertions.assertThat(service.estaEnPedido(nueva.getId())).isFalse();
    }

    // ── create ────────────────────────────────────────────────────────────────

    @Test
    public void ComidaService_create_CreatesSuccessfully() {
        Long categoryId = categoriaService.findAll().iterator().next().getId();

        Comida result = service.create("Pizza Nueva", "desc", 50000, null, true, categoryId);

        Assertions.assertThat(result.getId()).isNotNull();
        Assertions.assertThat(result.getName()).isEqualTo("Pizza Nueva");
    }

    @Test
    public void ComidaService_create_ThrowsWhenNameIsBlank() {
        Long categoryId = categoriaService.findAll().iterator().next().getId();

        Assertions.assertThatThrownBy(() -> service.create("", "desc", 50000, null, true, categoryId))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void ComidaService_create_ThrowsWhenPriceIsZero() {
        Long categoryId = categoriaService.findAll().iterator().next().getId();

        Assertions.assertThatThrownBy(() -> service.create("Pizza", "desc", 0, null, true, categoryId))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void ComidaService_create_ThrowsWhenCategoryIdIsNull() {

        Assertions.assertThatThrownBy(() -> service.create("Pizza", "desc", 50000, null, true, null))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void ComidaService_create_ThrowsWhenCategoryNotExists() {

        Assertions.assertThatThrownBy(() -> service.create("Pizza", "desc", 50000, null, true, 999L))
                .isInstanceOf(IllegalArgumentException.class);
    }

    // ── update ────────────────────────────────────────────────────────────────

    @Test
    public void ComidaService_update_UpdatesSuccessfully() {
        Long id = service.findAll().iterator().next().getId();
        Long categoryId = categoriaService.findAll().iterator().next().getId();

        Comida result = service.update(id, "Nombre Actualizado", "nueva desc", 60000, null, true, categoryId);

        Assertions.assertThat(result.getName()).isEqualTo("Nombre Actualizado");
        Assertions.assertThat(result.getPrice()).isEqualTo(60000);
    }

    @Test
    public void ComidaService_update_ThrowsWhenIdNotFound() {
        Long categoryId = categoriaService.findAll().iterator().next().getId();

        Assertions.assertThatThrownBy(() -> service.update(999L, "Pizza", "desc", 50000, null, true, categoryId))
                .isInstanceOf(NoSuchElementException.class);
    }

    @Test
    public void ComidaService_update_ThrowsWhenNameIsBlank() {
        Long id = service.findAll().iterator().next().getId();
        Long categoryId = categoriaService.findAll().iterator().next().getId();

        Assertions.assertThatThrownBy(() -> service.update(id, "", "desc", 50000, null, true, categoryId))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void ComidaService_update_ThrowsWhenCategoryNotExists() {
        Long id = service.findAll().iterator().next().getId();

        Assertions.assertThatThrownBy(() -> service.update(id, "Pizza", "desc", 50000, null, true, 999L))
                .isInstanceOf(IllegalArgumentException.class);
    }

    // ── delete ────────────────────────────────────────────────────────────────

    @Test
    public void ComidaService_delete_DeletesSuccessfully() {
        Long categoryId = categoriaService.findAll().iterator().next().getId();
        Comida nueva = service.create("Para Eliminar", "desc", 30000, null, true, categoryId);

        service.delete(nueva.getId());

        Assertions.assertThatThrownBy(() -> service.findById(nueva.getId()))
                .isInstanceOf(NoSuchElementException.class);
    }

    @Test
    public void ComidaService_delete_ThrowsWhenInPedido() {
        Long id = service.findAll().iterator().next().getId();

        Assertions.assertThatThrownBy(() -> service.delete(id))
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    public void ComidaService_delete_ThrowsWhenNotFound() {

        Assertions.assertThatThrownBy(() -> service.delete(999L))
                .isInstanceOf(NoSuchElementException.class);
    }
}
