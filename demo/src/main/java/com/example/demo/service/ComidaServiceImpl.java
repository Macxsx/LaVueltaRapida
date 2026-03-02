package com.example.demo.service;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.entitys.Comida;
import com.example.demo.repository.ComidaRepository;

@Service
public class ComidaServiceImpl  implements ComidaService{

    @Autowired
    ComidaRepository repo;
    
    @Override
    public Collection<Comida> findAll(){
        return repo.findAll();
    }

    @Override
    public Comida findById(Integer id) {
        return repo.findById(id);
    }

    @Override
    public Collection<Comida> findTop2ByIdGreaterThanOrderByIdAsc(Integer id) {
        return repo.findTop2ByIdGreaterThanOrderByIdAsc(id);
    }

    @Override
    public Collection<Comida> Recomendados(Integer id) {
        return repo.Recomendados(id);
    }

    @Override
    public void save(Comida comida) {
        repo.save(comida);
        
    }

    @Override
    public void deleteById(Integer id) {
        repo.deleteById(id);
        
    }

    @Override
    public Collection<Comida> findTop5Available() {
        return repo.findTop5Available();
    }
}

