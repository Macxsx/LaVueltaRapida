package com.example.demo.service;

import java.util.Collection;
import com.example.demo.entitys.Adicional;

public interface AdicionalService {
    
    Collection<Adicional> findAll();

    Adicional findById(Long id);

    Collection<Adicional> findByCategoriaId(Long categoriaId);

    void save(Adicional adicional);

    void deleteById(Long id);
}
