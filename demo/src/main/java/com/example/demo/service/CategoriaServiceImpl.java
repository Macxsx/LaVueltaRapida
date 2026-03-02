package com.example.demo.service;
import org.springframework.stereotype.Service;
import java.util.Collection;
import org.springframework.beans.factory.annotation.Autowired;
import com.example.demo.entitys.Categoria;
import com.example.demo.repository.CategoriaRepository;

@Service
public class CategoriaServiceImpl  implements CategoriaService {
    
    @Autowired
    CategoriaRepository repo;

    @Override
    public Collection<Categoria> findAll() {
        return repo.findAll();
    }

    @Override
    public Categoria findById(Integer id) {
        return repo.findById(id);
    }
}
