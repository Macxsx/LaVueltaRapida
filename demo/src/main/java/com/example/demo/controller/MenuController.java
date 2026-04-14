package com.example.demo.controller;

import com.example.demo.entitys.Comida;
import com.example.demo.entitys.Categoria;
import com.example.demo.service.CategoriaService;
import com.example.demo.service.ComidaService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/menu")
@CrossOrigin(origins = "http://localhost:5000")
public class MenuController {

    @Autowired
    private ComidaService comidaService;

    @Autowired
    private CategoriaService categoriaService;

    // 🍔 GET ALL COMIDA
    @GetMapping
public List<Comida> getAllComidas() {
    return new java.util.ArrayList<>(comidaService.findAll());
}

    // 🍔 GET COMIDA BY ID + RECOMMENDATIONS
    @GetMapping("/{id}")
    public Map<String, Object> getComidaById(@PathVariable Long id) {
        Map<String, Object> response = new HashMap<>();

        Comida comida = comidaService.findById(id);

        response.put("comida", comida);
        response.put(
            "recomendaciones",
            comidaService.recomendadosPorCategoria(
                comida.getCategory().getId(), id
            )
        );

        return response;
    }

    // 📂 GET ALL CATEGORIES
    @GetMapping("/categorias")
public List<Categoria> getCategorias() {
    return new java.util.ArrayList<>(categoriaService.findAll());
}

    // ➕ ADD OR UPDATE COMIDA
    @PostMapping
    public Map<String, Object> saveComida(
            @RequestBody Comida comida,
            @RequestParam Long categoryId) {

        Map<String, Object> response = new HashMap<>();

        boolean esEdicion = comida.getId() != null;

        comida.setCategory(categoriaService.findById(categoryId));
        comidaService.save(comida);

        response.put("success", true);
        response.put("action", esEdicion ? "updated" : "created");
        response.put("comida", comida);

        return response;
    }

    // 🗑️ DELETE COMIDA
    @DeleteMapping("/{id}")
    public Map<String, Object> deleteComida(@PathVariable Long id) {
        Map<String, Object> response = new HashMap<>();

        try {
            comidaService.deleteById(id);
            response.put("success", true);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error deleting comida");
        }

        return response;
    }
}