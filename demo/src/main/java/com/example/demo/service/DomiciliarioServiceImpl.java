package com.example.demo.service;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.entitys.Domiciliario;
import com.example.demo.entitys.EstadoPedido;
import com.example.demo.repository.DomiciliarioRepository;
import com.example.demo.repository.PedidoRepository;

@Service
public class DomiciliarioServiceImpl implements DomiciliarioService {

    @Autowired
    private DomiciliarioRepository repo;

    @Autowired
    private PedidoRepository pedidoRepository;

    @Override
    public Collection<Domiciliario> findAll() {
        return repo.findAll();
    }

    @Override
    public Optional<Domiciliario> findById(Long id) {
        return repo.findById(id);
    }

    @Override
    public Optional<Domiciliario> findByCedula(String cedula) {
        return repo.findByCedula(cedula);
    }

    @Override
    public Optional<Domiciliario> findByCelular(String celular) {
        return repo.findByCelular(celular);
    }

    @Override
    public void save(Domiciliario domiciliario) {
        repo.save(domiciliario);
    }

    @Override
    public void deleteById(Long id) {
        repo.deleteById(id);
    }

    @Override
    public boolean existsById(Long id) {
        return repo.existsById(id);
    }

    // ── Implementación de lógica de negocio ──

    @Override
    public Map<String, Object> createDomiciliario(String nombre, String cedula, String celular, String ciudad, String direccion) {
        // Validar unicidad de cédula
        if (findByCedula(cedula).isPresent()) {
            return Map.of(
                "success", false,
                "error", "Ya existe un domiciliario con la cédula '" + cedula + "'."
            );
        }

        // Validar unicidad de celular
        if (findByCelular(celular).isPresent()) {
            return Map.of(
                "success", false,
                "error", "Ya existe un domiciliario con el celular '" + celular + "'."
            );
        }

        Domiciliario domiciliario = new Domiciliario(nombre, cedula, celular, ciudad, direccion, true);
        save(domiciliario);

        return Map.of(
            "success", true,
            "domiciliario", domiciliario
        );
    }

    @Override
    public Map<String, Object> updateDomiciliario(Long id, String nombre, String cedula, String celular, String ciudad, String direccion) {
        Optional<Domiciliario> existing = findById(id);
        if (existing.isEmpty()) {
            return Map.of(
                "success", false,
                "error", "El domiciliario no existe."
            );
        }

        Domiciliario stored = existing.get();

        // Validar unicidad de cédula
        if (!cedula.equals(stored.getCedula()) && findByCedula(cedula).isPresent()) {
            return Map.of(
                "success", false,
                "error", "Ya existe un domiciliario con la cédula '" + cedula + "'."
            );
        }

        // Validar unicidad de celular
        if (!celular.equals(stored.getCelular()) && findByCelular(celular).isPresent()) {
            return Map.of(
                "success", false,
                "error", "Ya existe un domiciliario con el celular '" + celular + "'."
            );
        }

        stored.setNombre(nombre);
        stored.setCedula(cedula);
        stored.setCelular(celular);
        save(stored);

        return Map.of(
            "success", true,
            "domiciliario", stored
        );
    }

    @Override
    public Map<String, Object> deleteDomiciliario(Long id) {
        Optional<Domiciliario> domiciliario = findById(id);
        if (domiciliario.isEmpty()) {
            return Map.of(
                "success", false,
                "error", "El domiciliario no existe."
            );
        }

        if (pedidoRepository.existsByDomiciliarioIdAndEstadoNot(id, EstadoPedido.ENTREGADO)) {
            return Map.of(
                "success", false,
                "error", "No se puede eliminar el domiciliario porque tiene un pedido en curso."
            );
        }

        deleteById(id);
        return Map.of("success", true);
    }
}
