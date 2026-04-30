package com.example.demo.service;

import java.util.Collection;
import java.util.NoSuchElementException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.entitys.Categoria;
import com.example.demo.repository.CategoriaRepository;

@Service
public class CategoriaServiceImpl implements CategoriaService {
    
    @Autowired
    private CategoriaRepository repo;

    @Override
    public Collection<Categoria> findAll() {
        return repo.findAllByOrderByIdAsc();
    }

    @Override
    public Categoria findById(Long id) {
        return repo.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Categoría no encontrada."));
    }
}