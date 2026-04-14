package com.example.demo.controller;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.entitys.Comida;
import com.example.demo.service.ComidaService;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/api/comidas")
public class ComidaRestController {

    @Autowired
    private ComidaService comidaService;

    @GetMapping
    public Collection<Comida> findAll() {
        return comidaService.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Comida> findById(@PathVariable Long id) {
        Comida comida = comidaService.findById(id);
        return comida == null ? ResponseEntity.notFound().build() : ResponseEntity.ok(comida);
    }

    @PostMapping
    public ResponseEntity<Comida> add(@RequestBody Comida comida) {
        comidaService.save(comida);
        return ResponseEntity.ok(comida);
    }

    @PutMapping
    public ResponseEntity<Comida> update(@RequestBody Comida comida) {
        if (comida.getId() == null || comidaService.findById(comida.getId()) == null) {
            return ResponseEntity.notFound().build();
        }
        comidaService.save(comida);
        return ResponseEntity.ok(comida);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (comidaService.findById(id) == null) {
            return ResponseEntity.notFound().build();
        }
        comidaService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
