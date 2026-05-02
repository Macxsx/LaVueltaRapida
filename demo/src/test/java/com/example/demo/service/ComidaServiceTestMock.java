package com.example.demo.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collection;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

import com.example.demo.entitys.Categoria;
import com.example.demo.entitys.Comida;
import com.example.demo.repository.ComidaRepository;
import com.example.demo.repository.LineaPedidoRepository;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
public class ComidaServiceTestMock {

    @InjectMocks
    private ComidaServiceImpl service;

    @Mock
    ComidaRepository repo;

    @Mock
    LineaPedidoRepository lineaPedidoRepository;

    @Mock
    CategoriaService categoriaService;


    @Test
    public void ComidaService_findAll_ReturnsAllComidas() {
        Categoria cat = new Categoria(1L, "Pizzas");
        when(repo.findAll()).thenReturn(List.of(
                new Comida(1L, "Margarita",  "desc", 25000.0, "img1", true, cat),
                new Comida(2L, "Pepperoni",  "desc", 28000.0, "img2", true, cat),
                new Comida(3L, "Hawaiana",   "desc", 27000.0, "img3", true, cat)
        ));

        Collection<Comida> result = service.findAll();

        Assertions.assertThat(result).isNotNull();
        Assertions.assertThat(result).hasSize(3);
    }

    @Test
    public void ComidaService_findById_ReturnsComidaWhenExists() {
        Categoria cat = new Categoria(1L, "Pizzas");
        Comida comida = new Comida(1L, "Margarita", "desc", 25000.0, "img", true, cat);
        when(repo.findById(1L)).thenReturn(Optional.of(comida));

        Comida result = service.findById(1L);

        Assertions.assertThat(result).isNotNull();
        Assertions.assertThat(result.getId()).isEqualTo(1L);
        Assertions.assertThat(result.getName()).isEqualTo("Margarita");
    }

    @Test
    public void ComidaService_findById_ThrowsWhenNotExists() {
        when(repo.findById(999L)).thenReturn(Optional.empty());

        Assertions.assertThatThrownBy(() -> service.findById(999L))
                .isInstanceOf(NoSuchElementException.class);
    }

    @Test
    public void ComidaService_estaEnPedido_ReturnsTrueWhenIncludedInPedido() {
        when(lineaPedidoRepository.existsByComidaIdAndPedidoIsNotNull(1L)).thenReturn(true);

        boolean result = service.estaEnPedido(1L);

        Assertions.assertThat(result).isTrue();
    }

    @Test
    public void ComidaService_estaEnPedido_ReturnsFalseWhenNotIncludedInPedido() {
        when(lineaPedidoRepository.existsByComidaIdAndPedidoIsNotNull(1L)).thenReturn(false);

        boolean result = service.estaEnPedido(1L);

        Assertions.assertThat(result).isFalse();
    }


    @Test
    public void ComidaService_create_SavesComidaWithCategoria() {
        Categoria cat = new Categoria(1L, "Pizzas");
        Comida saved = new Comida(10L, "Margarita", "desc", 25000.0, "img", true, cat);

        when(categoriaService.findById(1L)).thenReturn(cat);
        when(repo.save(any(Comida.class))).thenReturn(saved);

        Comida result = service.create("Margarita", "desc", 25000.0, "img", true, 1L);

        Assertions.assertThat(result).isSameAs(saved);
        ArgumentCaptor<Comida> captor = ArgumentCaptor.forClass(Comida.class);
        verify(repo).save(captor.capture());
        Comida nueva = captor.getValue();
        Assertions.assertThat(nueva.getName()).isEqualTo("Margarita");
        Assertions.assertThat(nueva.getDescription()).isEqualTo("desc");
        Assertions.assertThat(nueva.getPrice()).isEqualTo(25000.0);
        Assertions.assertThat(nueva.getImage()).isEqualTo("img");
        Assertions.assertThat(nueva.isAvailable()).isTrue();
        Assertions.assertThat(nueva.getCategory()).isSameAs(cat);
    }

    @Test
    public void ComidaService_create_ThrowsWhenNameIsBlank() {
        Assertions.assertThatThrownBy(
                () -> service.create("   ", "desc", 25000.0, "img", true, 1L))
                .isInstanceOf(IllegalArgumentException.class);

        verify(repo, never()).save(any(Comida.class));
    }

    @Test
    public void ComidaService_create_ThrowsWhenPriceIsZeroOrNegative() {
        Assertions.assertThatThrownBy(
                () -> service.create("Margarita", "desc", 0.0, "img", true, 1L))
                .isInstanceOf(IllegalArgumentException.class);

        Assertions.assertThatThrownBy(
                () -> service.create("Margarita", "desc", -5.0, "img", true, 1L))
                .isInstanceOf(IllegalArgumentException.class);

        verify(repo, never()).save(any(Comida.class));
    }

    @Test
    public void ComidaService_create_ThrowsWhenCategoryIdIsNull() {
        Assertions.assertThatThrownBy(
                () -> service.create("Margarita", "desc", 25000.0, "img", true, null))
                .isInstanceOf(IllegalArgumentException.class);

        verify(repo, never()).save(any(Comida.class));
    }

    @Test
    public void ComidaService_create_ThrowsWhenCategoriaDoesNotExist() {
        when(categoriaService.findById(999L))
                .thenThrow(new NoSuchElementException("Categoría no encontrada."));

        Assertions.assertThatThrownBy(
                () -> service.create("Margarita", "desc", 25000.0, "img", true, 999L))
                .isInstanceOf(IllegalArgumentException.class);

        verify(repo, never()).save(any(Comida.class));
    }


    @Test
    public void ComidaService_update_UpdatesAllFields() {
        Categoria oldCat = new Categoria(1L, "Pizzas");
        Categoria newCat = new Categoria(2L, "Especiales");
        Comida stored = new Comida(1L, "Margarita", "desc", 25000.0, "img", true, oldCat);

        when(repo.findById(1L)).thenReturn(Optional.of(stored));
        when(categoriaService.findById(2L)).thenReturn(newCat);
        when(repo.save(stored)).thenReturn(stored);

        Comida result = service.update(1L, "Suprema", "nueva desc", 32000.0, "img2", false, 2L);

        Assertions.assertThat(result.getName()).isEqualTo("Suprema");
        Assertions.assertThat(result.getDescription()).isEqualTo("nueva desc");
        Assertions.assertThat(result.getPrice()).isEqualTo(32000.0);
        Assertions.assertThat(result.getImage()).isEqualTo("img2");
        Assertions.assertThat(result.isAvailable()).isFalse();
        Assertions.assertThat(result.getCategory()).isSameAs(newCat);
        verify(repo).save(stored);
    }

    @Test
    public void ComidaService_update_ThrowsWhenComidaDoesNotExist() {
        when(repo.findById(999L)).thenReturn(Optional.empty());

        Assertions.assertThatThrownBy(
                () -> service.update(999L, "X", "d", 1000.0, "i", true, 1L))
                .isInstanceOf(NoSuchElementException.class);

        verify(repo, never()).save(any(Comida.class));
    }

    @Test
    public void ComidaService_update_ThrowsWhenNameIsBlank() {
        Categoria cat = new Categoria(1L, "Pizzas");
        Comida stored = new Comida(1L, "Margarita", "desc", 25000.0, "img", true, cat);
        when(repo.findById(1L)).thenReturn(Optional.of(stored));

        Assertions.assertThatThrownBy(
                () -> service.update(1L, "  ", "d", 1000.0, "i", true, 1L))
                .isInstanceOf(IllegalArgumentException.class);

        verify(repo, never()).save(any(Comida.class));
    }

    @Test
    public void ComidaService_update_ThrowsWhenCategoriaDoesNotExist() {
        Categoria cat = new Categoria(1L, "Pizzas");
        Comida stored = new Comida(1L, "Margarita", "desc", 25000.0, "img", true, cat);
        when(repo.findById(1L)).thenReturn(Optional.of(stored));
        when(categoriaService.findById(999L))
                .thenThrow(new NoSuchElementException("Categoría no encontrada."));

        Assertions.assertThatThrownBy(
                () -> service.update(1L, "Suprema", "d", 1000.0, "i", true, 999L))
                .isInstanceOf(IllegalArgumentException.class);

        verify(repo, never()).save(any(Comida.class));
    }


    @Test
    public void ComidaService_delete_DeletesWhenNotInPedido() {
        Categoria cat = new Categoria(1L, "Pizzas");
        Comida comida = new Comida(1L, "Margarita", "desc", 25000.0, "img", true, cat);
        when(repo.findById(1L)).thenReturn(Optional.of(comida));
        when(lineaPedidoRepository.existsByComidaIdAndPedidoIsNotNull(1L)).thenReturn(false);

        service.delete(1L);

        verify(repo).deleteById(1L);
    }

    @Test
    public void ComidaService_delete_ThrowsWhenComidaDoesNotExist() {
        when(repo.findById(999L)).thenReturn(Optional.empty());

        Assertions.assertThatThrownBy(() -> service.delete(999L))
                .isInstanceOf(NoSuchElementException.class);

        verify(repo, never()).deleteById(anyLong());
    }

    @Test
    public void ComidaService_delete_ThrowsWhenAlreadyInPedido() {
        Categoria cat = new Categoria(1L, "Pizzas");
        Comida comida = new Comida(1L, "Margarita", "desc", 25000.0, "img", true, cat);
        when(repo.findById(1L)).thenReturn(Optional.of(comida));
        when(lineaPedidoRepository.existsByComidaIdAndPedidoIsNotNull(1L)).thenReturn(true);

        Assertions.assertThatThrownBy(() -> service.delete(1L))
                .isInstanceOf(IllegalStateException.class);

        verify(repo, never()).deleteById(anyLong());
    }
}
