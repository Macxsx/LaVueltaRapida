package com.example.demo.repository;

import java.util.List;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import com.example.demo.entitys.Adicional;
import com.example.demo.entitys.Categoria;

@DataJpaTest
public class CategoriaRepositoryTest {

    @Autowired
    private CategoriaRepository catRepo;

    @Autowired
    private AdicionalRepository adicionalRepo;


    @BeforeEach
    public void setUp() {
     catRepo.save(new Categoria("Especiales"));
     catRepo.save(new Categoria("Picantes"));
     catRepo.save(new Categoria("Bebidas"));
     catRepo.save(new Categoria("Postres"));
    }

    @Test
    public void CategoriaRepository_findAllByOrderByIdAsc_OrderedList() {

        List<Categoria> categorias = catRepo.findAllByOrderByIdAsc();

        Assertions.assertThat(categorias).isNotEmpty();
        Assertions.assertThat(categorias).isSortedAccordingTo((c1, c2) -> c1.getId().compareTo(c2.getId()));
    }

    @Test
    public void CategoriaRepository_findByAdicionalesContains_ReturnsMatchingCategorias() {

        Adicional adicional = adicionalRepo.save(new Adicional("Extra queso", 2000, true));

        Categoria categoria = new Categoria("Clásicas");
            categoria.getAdicionales().addAll(List.of(
                   adicional
            ));
        Categoria categoriaConAdicional = catRepo.save(categoria);
    

        List<Categoria> result = catRepo.findByAdicionalesContains(adicional);

        Assertions.assertThat(result).isNotEmpty();
        Assertions.assertThat(result).hasSize(1);
        Assertions.assertThat(result.get(0).getName()).isEqualTo(categoriaConAdicional.getName());
    }
}
