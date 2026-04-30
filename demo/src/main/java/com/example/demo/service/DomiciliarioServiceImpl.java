package com.example.demo.service;

import java.util.List;
import java.util.NoSuchElementException;
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
    public List<Domiciliario> findAll() {
        return repo.findAll();
    }

    @Override
    public Optional<Domiciliario> findById(Long id) {
        return repo.findById(id);
    }

    @Override
    public Domiciliario save(Domiciliario domiciliario) {
        return repo.save(domiciliario);
    }

    @Override
    public Optional<Domiciliario> findFirstDisponible() {
        return repo.findFirstByDisponibleTrueOrderByIdAsc();
    }

    @Override
    public Domiciliario create(Domiciliario domiciliario) {
        validarUnicidad(domiciliario.getCedula(), domiciliario.getCelular(), null);
        domiciliario.setId(null);
        return repo.save(domiciliario);
    }

    @Override
    public Domiciliario update(Long id, Domiciliario data) {
        Domiciliario stored = repo.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Domiciliario no encontrado."));
        validarUnicidad(data.getCedula(), data.getCelular(), id);
        stored.setNombre(data.getNombre());
        stored.setCedula(data.getCedula());
        stored.setCelular(data.getCelular());
        stored.setDisponible(data.isDisponible());
        return repo.save(stored);
    }

    @Override
    public void delete(Long id) {
        if (!repo.existsById(id)) {
            throw new NoSuchElementException("Domiciliario no encontrado.");
        }
        if (pedidoRepository.existsByDomiciliarioIdAndEstadoNot(id, EstadoPedido.ENTREGADO)) {
            throw new IllegalStateException(
                    "No se puede eliminar el domiciliario porque tiene un pedido en curso.");
        }
        repo.deleteById(id);
    }

    private void validarUnicidad(String cedula, String celular, Long idPropio) {
        repo.findByCedula(cedula)
                .filter(d -> !d.getId().equals(idPropio))
                .ifPresent(d -> { throw new IllegalStateException(
                        "Ya existe un domiciliario con la cédula '" + cedula + "'."); });
        repo.findByCelular(celular)
                .filter(d -> !d.getId().equals(idPropio))
                .ifPresent(d -> { throw new IllegalStateException(
                        "Ya existe un domiciliario con el celular '" + celular + "'."); });
    }
}
