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
    public Collection<Comida> Recomendados(Long id) {
        List<Comida> recomendados = new ArrayList<>();
        recomendados.addAll(repo.findTop3ByIdGreaterThanOrderByIdAsc(id));
        recomendados.addAll(repo.findTop3ByIdLessThanOrderByIdDesc(id));
        return recomendados.stream().limit(5).collect(java.util.stream.Collectors.toList());
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