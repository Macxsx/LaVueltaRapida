package com.example.demo.service;

import java.util.Collection;
import java.util.NoSuchElementException;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import com.example.demo.entitys.Categoria;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@ActiveProfiles("test")
public class CategoriaServiceTestNaive {

    @Autowired
    private CategoriaService service;


    @Test
    public void CategoriaService_findAll_ReturnsAllCategorias() {
         Collection<Categoria> categorias = service.findAll();

         Assertions.assertThat(categorias).isNotNull();
         Assertions.assertThat(categorias).hasSize(5);
    }

    @Test
    public void CategoriaService_findById_ReturnsCategoriaWhenExists() {
        Long id = service.findAll().iterator().next().getId();

        Categoria result = service.findById(id);

        Assertions.assertThat(result).isNotNull();
        Assertions.assertThat(result.getId()).isEqualTo(id);
    }

    @Test
    public void CategoriaService_findById_ThrowsWhenNotExists() {
        Assertions.assertThatThrownBy(() -> service.findById(999L))
                .isInstanceOf(NoSuchElementException.class);
    }

}
