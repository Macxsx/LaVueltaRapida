package com.example.demo.service;

import java.util.Collection;
import java.util.NoSuchElementException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.entitys.Adicional;
import com.example.demo.entitys.Categoria;
import com.example.demo.repository.AdicionalRepository;
import com.example.demo.repository.CategoriaRepository;
import com.example.demo.repository.LineaPedidoAdicionalRepository;

@Service
public class AdicionalServiceImpl implements AdicionalService {

    @Autowired
    private AdicionalRepository repo;

    @Autowired
    private CategoriaRepository categoriaRepository;

    @Autowired
    private LineaPedidoAdicionalRepository lineaPedidoAdicionalRepository;

    @Autowired
    private CategoriaService categoriaService;

    @Override
    public Collection<Adicional> findAll() {
        return repo.findAll();
    }

    @Override
    public Adicional findById(Long id) {
        return repo.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Adicional no encontrado."));
    }

    @Override
    public Collection<Adicional> findByCategoriaId(Long categoriaId) {
        return repo.findByCategorias_IdAndAvailableTrue(categoriaId);
    }

    @Override
    public Collection<Categoria> findCategorias(Long adicionalId) {
        Adicional adicional = repo.findById(adicionalId).orElse(null);
        if (adicional == null) return java.util.Collections.emptyList();
        return categoriaRepository.findByAdicionalesContains(adicional);
    }

    @Override
    public boolean estaEnPedido(Long id) {
        return lineaPedidoAdicionalRepository.existsByAdicionalIdAndLineaPedidoPedidoIsNotNull(id);
    }

    @Override
    @Transactional
    public Adicional create(String name, double price, boolean available, Long[] categoriaIds) {
        validarCampos(name, price);
        Adicional adicional = repo.save(new Adicional(name, price, available));
        asignarCategorias(adicional, categoriaIds);
        return adicional;
    }

    @Override
    @Transactional
    public Adicional update(Long id, String name, double price, boolean available, Long[] categoriaIds) {
        Adicional stored = repo.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Adicional no encontrado."));
        validarCampos(name, price);
        stored.setName(name);
        stored.setPrice(price);
        stored.setAvailable(available);
        repo.save(stored);
        desasignarCategorias(stored);
        asignarCategorias(stored, categoriaIds);
        return stored;
    }

    @Override
    @Transactional
    public void delete(Long id) {
        if (repo.findById(id).isEmpty()) {
            throw new NoSuchElementException("Adicional no encontrado.");
        }
        if (lineaPedidoAdicionalRepository.existsByAdicionalIdAndLineaPedidoPedidoIsNotNull(id)) {
            throw new IllegalStateException(
                    "No se puede eliminar el adicional porque ya está incluido en uno o más pedidos.");
        }
        repo.deleteFromCategorias(id);
        repo.deleteById(id);
    }

    private void validarCampos(String name, double price) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("El nombre del adicional es obligatorio.");
        }
        if (price <= 0) {
            throw new IllegalArgumentException("El precio debe ser mayor a 0.");
        }
    }

    private void asignarCategorias(Adicional adicional, Long[] categoriaIds) {
        if (categoriaIds == null || categoriaIds.length == 0) return;
        for (Long categoriaId : categoriaIds) {
            try {
                Categoria categoria = categoriaService.findById(categoriaId);
                if (!categoria.getAdicionales().contains(adicional)) {
                    categoria.getAdicionales().add(adicional);
                    categoriaRepository.save(categoria);
                }
            } catch (NoSuchElementException e) {
                throw new IllegalArgumentException("La categoría con id " + categoriaId + " no existe.");
            }
        }
    }

    private void desasignarCategorias(Adicional adicional) {
        for (Categoria categoria : categoriaRepository.findAll()) {
            if (categoria.getAdicionales().contains(adicional)) {
                categoria.getAdicionales().remove(adicional);
                categoriaRepository.save(categoria);
            }
        }
    }
}
