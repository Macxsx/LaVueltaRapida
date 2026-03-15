package com.example.demo.service;

import java.util.Collection;
import com.example.demo.entitys.Comida;

public interface ComidaService {
    
    Collection<Comida> findAll();

    Comida findById(Long id);

    Collection<Comida> recomendadosPorCategoria(Long categoryId, Long id);

    void save(Comida comida);

    void deleteById(Long id);

    Collection<Comida> findTop5ByAvailableTrue(); 
}