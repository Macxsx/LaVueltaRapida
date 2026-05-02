package com.example.demo.service;

import static org.mockito.Mockito.when;

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

import com.example.demo.entitys.Categoria;
import com.example.demo.repository.CategoriaRepository;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
public class CategoriaServiceTestMock {

    @InjectMocks
    private CategoriaServiceImpl service;

    @Mock
    CategoriaRepository repo;


    @Test
    public void CategoriaService_findAll_ReturnsAllCategorias() {

        when(repo.findAllByOrderByIdAsc()).thenReturn(List.of(
                new Categoria(1L, "Categoria 1"),
                new Categoria(2L, "Categoria 2"),
                new Categoria(3L, "Categoria 3"),
                new Categoria(4L, "Categoria 4"),
                new Categoria(5L, "Categoria 5")
        ));


         Collection<Categoria> categorias = service.findAll();

         Assertions.assertThat(categorias).isNotNull();
         Assertions.assertThat(categorias).hasSize(5);
    }

    @Test
    public void CategoriaService_findById_ReturnsCategoriaWhenExists() {
        Categoria categoria = new Categoria(1L, "Categoria 1");
        when(repo.findById(1L)).thenReturn(Optional.of(categoria));

        Categoria result = service.findById(1L);

        Assertions.assertThat(result).isNotNull();
        Assertions.assertThat(result.getId()).isEqualTo(1L);
        Assertions.assertThat(result.getName()).isEqualTo("Categoria 1");
    }

    @Test
    public void CategoriaService_findById_ThrowsWhenNotExists() {
        when(repo.findById(999L)).thenReturn(Optional.empty());

        Assertions.assertThatThrownBy(() -> service.findById(999L))
                .isInstanceOf(NoSuchElementException.class);
    }

}
