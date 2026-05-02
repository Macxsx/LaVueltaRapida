package com.example.demo.repository;

import java.util.List;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import com.example.demo.entitys.Categoria;
import com.example.demo.entitys.Comida;

@DataJpaTest
public class ComidaRepositoryTest {

    @Autowired
    private ComidaRepository comidaRepo;

    @Autowired
    private CategoriaRepository catRepo;

    private Categoria clasicas;
    private Categoria bebidas;

    @BeforeEach
    public void setUp() {
        clasicas = catRepo.save(new Categoria("Clásicas"));
        bebidas = catRepo.save(new Categoria("Bebidas"));

        comidaRepo.save(new Comida("Margarita",     "Tomate y queso",   25000, null, true,  clasicas));
        comidaRepo.save(new Comida("Pepperoni",     "Pepperoni extra",  28000, null, true,  clasicas));
        comidaRepo.save(new Comida("Hawaiana",      "Piña y jamón",     27000, null, true,  clasicas));
        comidaRepo.save(new Comida("Coca-Cola",     "350ml",             5000, null, true,  bebidas));
        comidaRepo.save(new Comida("Agua",          "500ml",             3000, null, false, bebidas));
    }

    @Test
    public void ComidaRepository_findByCategoryIdAndIdNot_ExcludesGivenComida() {

        List<Comida> todasClasicas = comidaRepo.findByCategoryIdAndIdNot(clasicas.getId(), 0L);
        Comida excluida = todasClasicas.get(0);

        List<Comida> result = comidaRepo.findByCategoryIdAndIdNot(clasicas.getId(), excluida.getId());

        Assertions.assertThat(result).isNotEmpty();
        Assertions.assertThat(result).noneMatch(c -> c.getId().equals(excluida.getId()));
        Assertions.assertThat(result).allMatch(c -> c.getCategory().getId().equals(clasicas.getId()));
    }

    @Test
    public void ComidaRepository_findByCategoryIdAndIdNot_ReturnsEmptyForOtherCategory() {

        List<Comida> result = comidaRepo.findByCategoryIdAndIdNot(bebidas.getId(), 0L);

        Assertions.assertThat(result).hasSize(2);
        Assertions.assertThat(result).allMatch(c -> c.getCategory().getId().equals(bebidas.getId()));
    }

    @Test
    public void ComidaRepository_findTop5ByAvailableTrue_ReturnsMaxFiveAvailable() {

        comidaRepo.save(new Comida("BBQ Chicken",  "Pollo BBQ",   30000, null, true, clasicas));
        comidaRepo.save(new Comida("Cuatro Quesos","4 quesos",    32000, null, true, clasicas));
        comidaRepo.save(new Comida("Veggie",       "Vegetariana", 26000, null, true, clasicas));

        List<Comida> result = comidaRepo.findTop5ByAvailableTrue();

        Assertions.assertThat(result).isNotEmpty();
        Assertions.assertThat(result).hasSizeLessThanOrEqualTo(5);
        Assertions.assertThat(result).allMatch(Comida::isAvailable);
    }
}
