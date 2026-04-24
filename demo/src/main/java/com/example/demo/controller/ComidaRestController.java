package com.example.demo.controller;

import java.util.Collection;
import java.util.Map;

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

import com.example.demo.entitys.Categoria;
import com.example.demo.entitys.Comida;
import com.example.demo.service.CategoriaService;
import com.example.demo.service.ComidaService;

@CrossOrigin(origins = {"http://localhost:5000", "http://127.0.0.1:5000"})
@RestController
@RequestMapping("/comidas")
public class ComidaRestController {

    @Autowired
    private ComidaService comidaService;

    @Autowired
    private CategoriaService categoriaService;

    @GetMapping
    public Collection<Comida> findAll() {
        return comidaService.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Comida> findById(@PathVariable Long id) {
        Comida comida = comidaService.findById(id);
        return comida == null ? ResponseEntity.notFound().build() : ResponseEntity.ok(comida);
    }

    public static class ComidaRequest {
        private String name;
        private String description;
        private double price;
        private String image;
        private boolean available;
        private Long categoryId;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public double getPrice() {
            return price;
        }

        public void setPrice(double price) {
            this.price = price;
        }

        public String getImage() {
            return image;
        }

        public void setImage(String image) {
            this.image = image;
        }

        public boolean isAvailable() {
            return available;
        }

        public void setAvailable(boolean available) {
            this.available = available;
        }

        public Long getCategoryId() {
            return categoryId;
        }

        public void setCategoryId(Long categoryId) {
            this.categoryId = categoryId;
        }
    }

    @PostMapping
    public ResponseEntity<?> add(@RequestBody ComidaRequest request) {
        ResponseEntity<?> error = validarComida(request);
        if (error != null) return error;

        Categoria categoria = categoriaService.findById(request.getCategoryId());
        if (categoria == null) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "La categoría seleccionada no existe."));
        }

        Comida comida = new Comida(
                request.getName(),
                request.getDescription(),
                request.getPrice(),
                request.getImage(),
                request.isAvailable(),
                categoria);
        comidaService.save(comida);
        return ResponseEntity.ok(comida);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody ComidaRequest request) {
        Comida stored = comidaService.findById(id);
        if (stored == null) {
            return ResponseEntity.notFound().build();
        }

        ResponseEntity<?> error = validarComida(request);
        if (error != null) return error;

        Categoria categoria = categoriaService.findById(request.getCategoryId());
        if (categoria == null) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "La categoría seleccionada no existe."));
        }

        stored.setName(request.getName());
        stored.setDescription(request.getDescription());
        stored.setPrice(request.getPrice());
        stored.setImage(request.getImage());
        stored.setAvailable(request.isAvailable());
        stored.setCategory(categoria);
        comidaService.save(stored);
        return ResponseEntity.ok(stored);
    }

    private ResponseEntity<?> validarComida(ComidaRequest req) {
        if (req.getName() == null || req.getName().isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("error", "El nombre del producto es obligatorio."));
        }
        if (req.getPrice() <= 0) {
            return ResponseEntity.badRequest().body(Map.of("error", "El precio debe ser mayor a 0."));
        }
        if (req.getCategoryId() == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "Debes seleccionar una categoría."));
        }
        return null;
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
