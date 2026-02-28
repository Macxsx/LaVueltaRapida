package com.example.demo.service;

import java.util.Collection;

import com.example.demo.entitys.Comida;

public interface ComidaService {
    
    public Collection<Comida> findAll();

    public Comida findById(Integer id);

    public Collection<Comida> findTop2ByIdGreaterThanOrderByIdAsc(Integer id);

    public Collection<Comida> Recomendados(Integer id);

    public void save(Comida comida);
    public void deleteById(Integer id);

}
