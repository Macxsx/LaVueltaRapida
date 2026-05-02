package com.example.demo.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

import com.example.demo.entitys.Adicional;
import com.example.demo.entitys.Categoria;
import com.example.demo.repository.AdicionalRepository;
import com.example.demo.repository.CategoriaRepository;
import com.example.demo.repository.LineaPedidoAdicionalRepository;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
public class AdicionalServiceTestMock {

    @InjectMocks
    private AdicionalServiceImpl service;

    @Mock
    AdicionalRepository repo;

    @Mock
    CategoriaRepository categoriaRepository;

    @Mock
    LineaPedidoAdicionalRepository lineaPedidoAdicionalRepository;

    @Mock
    CategoriaService categoriaService;


    @Test
    public void AdicionalService_findAll_ReturnsAllAdicionales() {

        when(repo.findAll()).thenReturn(List.of(
                new Adicional(1L, "Queso extra",      3000.0, true),
                new Adicional(2L, "Pepperoni extra",  3500.0, true),
                new Adicional(3L, "Jamón extra",      3000.0, true),
                new Adicional(4L, "Champiñones",      2500.0, true),
                new Adicional(5L, "Aceitunas negras", 2500.0, true)
        ));

        Collection<Adicional> adicionales = service.findAll();

        Assertions.assertThat(adicionales).isNotNull();
        Assertions.assertThat(adicionales).hasSize(5);
    }

    @Test
    public void AdicionalService_findById_ReturnsAdicionalWhenExists() {
        Adicional adicional = new Adicional(1L, "Queso extra", 3000.0, true);
        when(repo.findById(1L)).thenReturn(Optional.of(adicional));

        Adicional result = service.findById(1L);

        Assertions.assertThat(result).isNotNull();
        Assertions.assertThat(result.getId()).isEqualTo(1L);
        Assertions.assertThat(result.getName()).isEqualTo("Queso extra");
        Assertions.assertThat(result.getPrice()).isEqualTo(3000.0);
        Assertions.assertThat(result.isAvailable()).isTrue();
    }

    @Test
    public void AdicionalService_findById_ThrowsWhenNotExists() {
        when(repo.findById(999L)).thenReturn(Optional.empty());

        Assertions.assertThatThrownBy(() -> service.findById(999L))
                .isInstanceOf(NoSuchElementException.class);
    }

    @Test
    public void AdicionalService_findByCategoriaId_ReturnsAvailableAdicionalesOfCategoria() {
        when(repo.findByCategorias_IdAndAvailableTrue(1L)).thenReturn(List.of(
                new Adicional(1L, "Queso extra",     3000.0, true),
                new Adicional(2L, "Pepperoni extra", 3500.0, true)
        ));

        Collection<Adicional> result = service.findByCategoriaId(1L);

        Assertions.assertThat(result).isNotNull();
        Assertions.assertThat(result).hasSize(2);
    }

    @Test
    public void AdicionalService_findCategorias_ReturnsCategoriasWhenAdicionalExists() {
        Adicional adicional = new Adicional(1L, "Queso extra", 3000.0, true);
        when(repo.findById(1L)).thenReturn(Optional.of(adicional));
        when(categoriaRepository.findByAdicionalesContains(adicional)).thenReturn(List.of(
                new Categoria(1L, "Clásicas"),
                new Categoria(2L, "Especiales")
        ));

        Collection<Categoria> result = service.findCategorias(1L);

        Assertions.assertThat(result).isNotNull();
        Assertions.assertThat(result).hasSize(2);
    }

    @Test
    public void AdicionalService_findCategorias_ReturnsEmptyWhenAdicionalDoesNotExist() {
        when(repo.findById(999L)).thenReturn(Optional.empty());

        Collection<Categoria> result = service.findCategorias(999L);

        Assertions.assertThat(result).isNotNull();
        Assertions.assertThat(result).isEmpty();
    }

    @Test
    public void AdicionalService_estaEnPedido_ReturnsTrueWhenIncludedInPedido() {
        when(lineaPedidoAdicionalRepository
                .existsByAdicionalIdAndLineaPedidoPedidoIsNotNull(1L)).thenReturn(true);

        boolean result = service.estaEnPedido(1L);

        Assertions.assertThat(result).isTrue();
    }

    @Test
    public void AdicionalService_estaEnPedido_ReturnsFalseWhenNotIncludedInPedido() {
        when(lineaPedidoAdicionalRepository
                .existsByAdicionalIdAndLineaPedidoPedidoIsNotNull(1L)).thenReturn(false);

        boolean result = service.estaEnPedido(1L);

        Assertions.assertThat(result).isFalse();
    }

    @Test
    public void AdicionalService_create_SavesAdicionalAndAssignsCategorias() {
        Adicional saved = new Adicional(10L, "Queso extra", 3000.0, true);
        Categoria categoria = new Categoria(1L, "Clásicas");
        categoria.setAdicionales(new ArrayList<>());

        when(repo.save(any(Adicional.class))).thenReturn(saved);
        when(categoriaService.findById(1L)).thenReturn(categoria);

        Adicional result = service.create("Queso extra", 3000.0, true, new Long[]{1L});

        Assertions.assertThat(result).isNotNull();
        Assertions.assertThat(result.getId()).isEqualTo(10L);
        Assertions.assertThat(categoria.getAdicionales()).contains(saved);
        verify(categoriaRepository).save(categoria);
    }

    @Test
    public void AdicionalService_create_ThrowsWhenNameIsBlank() {
        Assertions.assertThatThrownBy(
                () -> service.create("   ", 3000.0, true, null))
                .isInstanceOf(IllegalArgumentException.class);

        verify(repo, never()).save(any(Adicional.class));
    }

    @Test
    public void AdicionalService_create_ThrowsWhenPriceIsZeroOrNegative() {
        Assertions.assertThatThrownBy(
                () -> service.create("Queso extra", 0.0, true, null))
                .isInstanceOf(IllegalArgumentException.class);

        Assertions.assertThatThrownBy(
                () -> service.create("Queso extra", -10.0, true, null))
                .isInstanceOf(IllegalArgumentException.class);

        verify(repo, never()).save(any(Adicional.class));
    }

    @Test
    public void AdicionalService_create_ThrowsWhenCategoriaDoesNotExist() {
        Adicional saved = new Adicional(10L, "Queso extra", 3000.0, true);
        when(repo.save(any(Adicional.class))).thenReturn(saved);
        when(categoriaService.findById(999L))
                .thenThrow(new NoSuchElementException("Categoría no encontrada."));

        Assertions.assertThatThrownBy(
                () -> service.create("Queso extra", 3000.0, true, new Long[]{999L}))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void AdicionalService_update_UpdatesFieldsAndReassignsCategorias() {
        Adicional stored = new Adicional(1L, "Queso extra", 3000.0, true);
        Categoria nueva = new Categoria(2L, "Especiales");
        nueva.setAdicionales(new ArrayList<>());

        when(repo.findById(1L)).thenReturn(Optional.of(stored));
        when(categoriaRepository.findAll()).thenReturn(List.of());
        when(categoriaService.findById(2L)).thenReturn(nueva);

        Adicional result = service.update(1L, "Queso premium", 4500.0, false, new Long[]{2L});

        Assertions.assertThat(result.getName()).isEqualTo("Queso premium");
        Assertions.assertThat(result.getPrice()).isEqualTo(4500.0);
        Assertions.assertThat(result.isAvailable()).isFalse();
        Assertions.assertThat(nueva.getAdicionales()).contains(stored);
        verify(repo).save(stored);
    }

    @Test
    public void AdicionalService_update_ThrowsWhenAdicionalDoesNotExist() {
        when(repo.findById(999L)).thenReturn(Optional.empty());

        Assertions.assertThatThrownBy(
                () -> service.update(999L, "Algo", 1000.0, true, null))
                .isInstanceOf(NoSuchElementException.class);
    }

    @Test
    public void AdicionalService_delete_DeletesWhenNotInPedido() {
        Adicional adicional = new Adicional(1L, "Queso extra", 3000.0, true);
        when(repo.findById(1L)).thenReturn(Optional.of(adicional));
        when(lineaPedidoAdicionalRepository
                .existsByAdicionalIdAndLineaPedidoPedidoIsNotNull(1L)).thenReturn(false);

        service.delete(1L);

        verify(repo).deleteFromCategorias(1L);
        verify(repo).deleteById(1L);
    }

    @Test
    public void AdicionalService_delete_ThrowsWhenAdicionalDoesNotExist() {
        when(repo.findById(999L)).thenReturn(Optional.empty());

        Assertions.assertThatThrownBy(() -> service.delete(999L))
                .isInstanceOf(NoSuchElementException.class);

        verify(repo, never()).deleteById(any());
    }

    @Test
    public void AdicionalService_delete_ThrowsWhenAlreadyInPedido() {
        Adicional adicional = new Adicional(1L, "Queso extra", 3000.0, true);
        when(repo.findById(1L)).thenReturn(Optional.of(adicional));
        when(lineaPedidoAdicionalRepository
                .existsByAdicionalIdAndLineaPedidoPedidoIsNotNull(1L)).thenReturn(true);

        Assertions.assertThatThrownBy(() -> service.delete(1L))
                .isInstanceOf(IllegalStateException.class);

        verify(repo, never()).deleteById(any());
    }
}
