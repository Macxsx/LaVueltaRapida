package com.example.demo.service;

import java.util.Collection;

import com.example.demo.entitys.Comida;

public interface ComidaService {
    
    public Collection<Comida> findAll();

    public Comida findById(int id);

    public Collection<Comida> findTop2ByIdGreaterThanOrderByIdAsc(int id);

    public Collection<Comida> Recomendados(int id);

    public void save(Comida comida);
    public void deleteById(int id);

}
