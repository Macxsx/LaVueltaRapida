package com.example.demo.service;

import java.util.Collection;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.entitys.Comida;
import com.example.demo.repository.ComidaRepository;

@Service
public class ComidaServiceImpl implements ComidaService {

    @Autowired
    private ComidaRepository repo;

    @Override
    public Collection<Comida> findAll() {
        return repo.findAll();
    }

    @Override
    public Comida findById(Long id) {
        return repo.findById(id).orElse(null);
    }

    @Override
    public Collection<Comida> findTop2ByIdGreaterThanOrderByIdAsc(Long id) {
        return repo.findTop2ByIdGreaterThanOrderByIdAsc(id);
    }

    @Override
    public Collection<Comida> Recomendados(Long id) {
        return repo.findTop5ByAvailableTrueAndIdNot(id);
    }

    @Override
    public void save(Comida comida) {
        repo.save(comida);
    }

    @Override
    public void deleteById(Long id) {
        repo.deleteById(id);
    }

    @Override
    public Collection<Comida> findTop5ByAvailableTrue() {
        return repo.findTop5ByAvailableTrue();
    }
}