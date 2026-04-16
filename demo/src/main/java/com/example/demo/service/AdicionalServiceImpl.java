package com.example.demo.service;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.entitys.Adicional;
import com.example.demo.repository.AdicionalRepository;

@Service
public class AdicionalServiceImpl implements AdicionalService {

    @Autowired
    private AdicionalRepository repo;

    @Override
    public Collection<Adicional> findAll() {
        return repo.findAll();
    }

    @Override
    public Adicional findById(Long id) {
        return repo.findById(id).orElse(null);
    }

    @Override
    public Collection<Adicional> findByCategoriaId(Long categoriaId) {
        return repo.findByCategorias_IdAndAvailableTrue(categoriaId);
    }

    @Override
    public void save(Adicional adicional) {
        repo.save(adicional);
    }

    @Override
    public void deleteById(Long id) {
        repo.deleteById(id);
    }
}
