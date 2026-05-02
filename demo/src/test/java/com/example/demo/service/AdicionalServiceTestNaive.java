package com.example.demo.service;

import java.util.Collection;
import java.util.NoSuchElementException;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import com.example.demo.entitys.Adicional;
import com.example.demo.entitys.Categoria;
import com.example.demo.repository.AdicionalRepository;
import com.example.demo.repository.CategoriaRepository;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@ActiveProfiles("test")
public class AdicionalServiceTestNaive {

    @Autowired private AdicionalService    service;
    @Autowired private AdicionalRepository adicionalRepo;
    @Autowired private CategoriaRepository categoriaRepo;

    // ── findAll ───────────────────────────────────────────────────────────────

    @Test
    public void AdicionalService_findAll_ReturnsAllAdicionales() {

        Collection<Adicional> result = service.findAll();

        Assertions.assertThat(result).isNotNull();
        Assertions.assertThat(result).isNotEmpty();
    }

    // ── findById ──────────────────────────────────────────────────────────────

    @Test
    public void AdicionalService_findById_ReturnsWhenExists() {
        Long id = adicionalRepo.findAll().get(0).getId();

        Adicional result = service.findById(id);

        Assertions.assertThat(result).isNotNull();
        Assertions.assertThat(result.getId()).isEqualTo(id);
    }

    @Test
    public void AdicionalService_findById_ThrowsWhenNotExists() {

        Assertions.assertThatThrownBy(() -> service.findById(999L))
                .isInstanceOf(NoSuchElementException.class);
    }

    // ── findByCategoriaId ─────────────────────────────────────────────────────

    @Test
    public void AdicionalService_findByCategoriaId_ReturnsAvailableAdicionales() {
        Long categoriaId = categoriaRepo.findAll().get(0).getId();

        Collection<Adicional> result = service.findByCategoriaId(categoriaId);

        Assertions.assertThat(result).isNotEmpty();
        Assertions.assertThat(result).allMatch(Adicional::isAvailable);
    }

    @Test
    public void AdicionalService_findByCategoriaId_ReturnsEmptyForUnknownCategoria() {

        Collection<Adicional> result = service.findByCategoriaId(999L);

        Assertions.assertThat(result).isEmpty();
    }

    // ── findCategorias ────────────────────────────────────────────────────────

    @Test
    public void AdicionalService_findCategorias_ReturnsCategoriasForAdicional() {
        Long id = adicionalRepo.findAll().get(0).getId();

        Collection<Categoria> result = service.findCategorias(id);

        Assertions.assertThat(result).isNotEmpty();
    }

    @Test
    public void AdicionalService_findCategorias_ReturnsEmptyForUnknownAdicional() {

        Collection<Categoria> result = service.findCategorias(999L);

        Assertions.assertThat(result).isEmpty();
    }

    // ── estaEnPedido ──────────────────────────────────────────────────────────

    @Test
    public void AdicionalService_estaEnPedido_ReturnsTrueWhenInPedido() {
        Long id = adicionalRepo.findAll().get(0).getId();

        Assertions.assertThat(service.estaEnPedido(id)).isTrue();
    }

    @Test
    public void AdicionalService_estaEnPedido_ReturnsFalseWhenNotInPedido() {
        Adicional nuevo = service.create("Sin Pedido", 1000, true, null);

        Assertions.assertThat(service.estaEnPedido(nuevo.getId())).isFalse();
    }

    // ── create ────────────────────────────────────────────────────────────────

    @Test
    public void AdicionalService_create_CreatesSuccessfully() {

        Adicional result = service.create("Nuevo Adicional", 2500, true, null);

        Assertions.assertThat(result.getId()).isNotNull();
        Assertions.assertThat(result.getName()).isEqualTo("Nuevo Adicional");
        Assertions.assertThat(result.getPrice()).isEqualTo(2500);
    }

    @Test
    public void AdicionalService_create_AssignsCategoryWhenProvided() {
        Long categoriaId = categoriaRepo.findAll().get(0).getId();

        Adicional result = service.create("Con Categoria", 2500, true, new Long[]{categoriaId});

        Assertions.assertThat(service.findCategorias(result.getId())).isNotEmpty();
    }

    @Test
    public void AdicionalService_create_ThrowsWhenNameIsBlank() {

        Assertions.assertThatThrownBy(() -> service.create("", 2500, true, null))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void AdicionalService_create_ThrowsWhenPriceIsZero() {

        Assertions.assertThatThrownBy(() -> service.create("Test", 0, true, null))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void AdicionalService_create_ThrowsWhenCategoriaNotExists() {

        Assertions.assertThatThrownBy(() -> service.create("Test", 2500, true, new Long[]{999L}))
                .isInstanceOf(IllegalArgumentException.class);
    }

    // ── update ────────────────────────────────────────────────────────────────

    @Test
    public void AdicionalService_update_UpdatesSuccessfully() {
        Adicional adicional = service.create("Para Actualizar", 1000, true, null);

        Adicional result = service.update(adicional.getId(), "Actualizado", 3000, false, null);

        Assertions.assertThat(adicionalRepo.findById(result.getId()).get().getName()).isEqualTo("Actualizado");
        Assertions.assertThat(adicionalRepo.findById(result.getId()).get().getPrice()).isEqualTo(3000);
    }

    @Test
    public void AdicionalService_update_ThrowsWhenIdNotFound() {

        Assertions.assertThatThrownBy(() -> service.update(999L, "Test", 2500, true, null))
                .isInstanceOf(NoSuchElementException.class);
    }

    @Test
    public void AdicionalService_update_ThrowsWhenNameIsBlank() {
        Adicional adicional = service.create("Para Actualizar", 1000, true, null);

        Assertions.assertThatThrownBy(() -> service.update(adicional.getId(), "", 2500, true, null))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void AdicionalService_update_ThrowsWhenPriceIsZero() {
        Adicional adicional = service.create("Para Actualizar", 1000, true, null);

        Assertions.assertThatThrownBy(() -> service.update(adicional.getId(), "Test", 0, true, null))
                .isInstanceOf(IllegalArgumentException.class);
    }

    // ── delete ────────────────────────────────────────────────────────────────

    @Test
    public void AdicionalService_delete_DeletesSuccessfully() {
        Adicional adicional = service.create("Para Eliminar", 1000, true, null);
        Long id = adicional.getId();

        service.delete(id);

        Assertions.assertThat(adicionalRepo.findById(id)).isNotPresent();
    }

    @Test
    public void AdicionalService_delete_ThrowsWhenInPedido() {
        Long id = adicionalRepo.findAll().get(0).getId();

        Assertions.assertThatThrownBy(() -> service.delete(id))
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    public void AdicionalService_delete_ThrowsWhenNotFound() {

        Assertions.assertThatThrownBy(() -> service.delete(999L))
                .isInstanceOf(NoSuchElementException.class);
    }
}
