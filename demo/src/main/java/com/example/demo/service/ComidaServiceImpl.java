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
    public Comida findById(int id) {
        return repo.findById(id);
    }

    @Override
    public Collection<Comida> findTop2ByIdGreaterThanOrderByIdAsc(int id) {
        return repo.findTop2ByIdGreaterThanOrderByIdAsc(id);
    }
}

