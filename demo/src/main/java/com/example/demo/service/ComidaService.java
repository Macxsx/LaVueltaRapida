package com.example.demo.service;

import java.util.Collection;

import com.example.demo.entitys.Comida;

public interface ComidaService {
    
    public Collection<Comida> findAll();

    public Comida findById(Long id);

    public Collection<Comida> findTop2ByIdGreaterThanOrderByIdAsc(Long id);

    public Collection<Comida> Recomendados(Long id);

    public void save(Comida comida);
    public void deleteById(Long id);

    public Collection<Comida> findTop5Available();

}
