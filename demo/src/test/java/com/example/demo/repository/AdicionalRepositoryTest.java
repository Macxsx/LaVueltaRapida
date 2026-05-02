package com.example.demo.repository;

import java.util.List;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import com.example.demo.entitys.Adicional;
import com.example.demo.entitys.Categoria;

@DataJpaTest
public class AdicionalRepositoryTest {

    @Autowired
    private AdicionalRepository adicionalRepo;

    @Autowired
    private CategoriaRepository catRepo;

    @BeforeEach
    public void setUp() {
        adicionalRepo.save(new Adicional("Extra queso", 2000, true));
        adicionalRepo.save(new Adicional("Doble pepperoni", 3000, true));
        adicionalRepo.save(new Adicional("Salsa picante", 1000, false));
        adicionalRepo.save(new Adicional("Champiñones", 1500, true));
    }

    @Test
    public void AdicionalRepository_findByCategorias_IdAndAvailableTrue_ReturnsOnlyAvailable() {

        Adicional disponible1 = adicionalRepo.save(new Adicional("Orégano", 500, true));
        Adicional disponible2 = adicionalRepo.save(new Adicional("Ajo", 600, true));
        Adicional noDisponible = adicionalRepo.save(new Adicional("Trufa", 8000, false));

        Categoria categoria = new Categoria("Gourmet");
        categoria.getAdicionales().addAll(List.of(disponible1, disponible2, noDisponible));
        Categoria savedCategoria = catRepo.save(categoria);

        List<Adicional> result = adicionalRepo.findByCategorias_IdAndAvailableTrue(savedCategoria.getId());

        Assertions.assertThat(result).isNotEmpty();
        Assertions.assertThat(result).hasSize(2);
        Assertions.assertThat(result).allMatch(Adicional::isAvailable);
    }

    @Test
    public void AdicionalRepository_deleteFromCategorias_RemovesLinkWithCategoria() {

        Adicional adicional = adicionalRepo.save(new Adicional("Bacon extra", 2500, true));

        Categoria categoria = new Categoria("Especiales");
        categoria.getAdicionales().add(adicional);
        Categoria savedCategoria = catRepo.save(categoria);

        List<Adicional> before = adicionalRepo.findByCategorias_IdAndAvailableTrue(savedCategoria.getId());
        Assertions.assertThat(before).hasSize(1);

        adicionalRepo.deleteFromCategorias(adicional.getId());

        List<Adicional> after = adicionalRepo.findByCategorias_IdAndAvailableTrue(savedCategoria.getId());
        Assertions.assertThat(after).isEmpty();
    }
}
