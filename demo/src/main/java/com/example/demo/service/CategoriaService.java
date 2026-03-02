package com.example.demo.service;
import java.util.Collection;
import com.example.demo.entitys.Categoria;

public interface CategoriaService {

    public Collection<Categoria> findAll();

    public Categoria findById(Integer id);
    
}
