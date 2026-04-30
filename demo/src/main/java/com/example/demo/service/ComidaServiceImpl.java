package com.example.demo.service;

import java.util.Collection;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.entitys.Categoria;
import com.example.demo.entitys.Comida;
import com.example.demo.repository.ComidaRepository;
import com.example.demo.repository.LineaPedidoRepository;

@Service
public class ComidaServiceImpl implements ComidaService {

    @Autowired
    private ComidaRepository repo;

    @Autowired
    private LineaPedidoRepository lineaPedidoRepository;

    @Autowired
    private CategoriaService categoriaService;

    @Override
    public Collection<Comida> findAll() {
        return repo.findAll();
    }

    @Override
    public Comida findById(Long id) {
        return repo.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Producto no encontrado."));
    }

    @Override
    public boolean estaEnPedido(Long id) {
        return lineaPedidoRepository.existsByComidaIdAndPedidoIsNotNull(id);
    }

    @Override
    public Comida create(String name, String description, double price,
                         String image, boolean available, Long categoryId) {
        validarCampos(name, price, categoryId);
        Categoria categoria = buscarCategoria(categoryId);
        return repo.save(new Comida(name, description, price, image, available, categoria));
    }

    @Override
    public Comida update(Long id, String name, String description, double price,
                         String image, boolean available, Long categoryId) {
        Comida stored = repo.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Producto no encontrado."));
        validarCampos(name, price, categoryId);
        Categoria categoria = buscarCategoria(categoryId);
        stored.setName(name);
        stored.setDescription(description);
        stored.setPrice(price);
        stored.setImage(image);
        stored.setAvailable(available);
        stored.setCategory(categoria);
        return repo.save(stored);
    }

    @Override
    public void delete(Long id) {
        if (repo.findById(id).isEmpty()) {
            throw new NoSuchElementException("Producto no encontrado.");
        }
        if (lineaPedidoRepository.existsByComidaIdAndPedidoIsNotNull(id)) {
            throw new IllegalStateException(
                    "No se puede eliminar el producto porque ya está incluido en uno o más pedidos.");
        }
        repo.deleteById(id);
    }

    private void validarCampos(String name, double price, Long categoryId) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("El nombre del producto es obligatorio.");
        }
        if (price <= 0) {
            throw new IllegalArgumentException("El precio debe ser mayor a 0.");
        }
        if (categoryId == null) {
            throw new IllegalArgumentException("Debes seleccionar una categoría.");
        }
    }

    private Categoria buscarCategoria(Long categoryId) {
        try {
            return categoriaService.findById(categoryId);
        } catch (NoSuchElementException e) {
            throw new IllegalArgumentException("La categoría seleccionada no existe.");
        }
    }
}
