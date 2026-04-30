package com.example.demo.service;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.entitys.Adicional;
import com.example.demo.entitys.Carrito;
import com.example.demo.entitys.Comida;
import com.example.demo.entitys.LineaPedido;
import com.example.demo.entitys.LineaPedidoAdicional;
import com.example.demo.repository.CarritoRepository;
import com.example.demo.repository.LineaPedidoRepository;

@Service
public class CarritoServiceImpl implements CarritoService {

    @Autowired
    private CarritoRepository carritoRepository;

    @Autowired
    private LineaPedidoRepository lineaPedidoRepository;

    @Autowired
    private ComidaService comidaService;

    @Autowired
    private AdicionalService adicionalService;

    @Override
    public Optional<Carrito> findById(Long id) {
        return carritoRepository.findById(id);
    }

    @Override
    public Optional<Carrito> findByClienteId(Long clienteId) {
        return carritoRepository.findByClienteId(clienteId);
    }

    @Override
    public Carrito save(Carrito carrito) {
        return carritoRepository.save(carrito);
    }

    @Override
    @Transactional
    public Carrito addProducto(Long carritoId, Long comidaId, Integer cantidad, List<Long> adicionalesIds) {
        if (comidaId == null) {
            throw new IllegalArgumentException("Se requiere 'comidaId'.");
        }
        if (cantidad == null || cantidad <= 0) {
            throw new IllegalArgumentException("Se requiere 'cantidad' mayor a 0.");
        }
        Carrito carrito = carritoRepository.findById(carritoId)
                .orElseThrow(() -> new NoSuchElementException("Carrito no encontrado."));

        Comida comida = comidaService.findById(comidaId);

        Set<Long> incomingIds = new TreeSet<>();
        if (adicionalesIds != null) incomingIds.addAll(adicionalesIds);

        List<Adicional> adicionales = new ArrayList<>();
        for (Long adicionalId : incomingIds) {
            adicionales.add(adicionalService.findById(adicionalId));
        }

        List<LineaPedido> lineasExistentes =
                lineaPedidoRepository.findByCarritoIdAndComidaId(carritoId, comidaId);
        for (LineaPedido linea : lineasExistentes) {
            Set<Long> existingIds = linea.getAdicionales().stream()
                    .map(lpa -> lpa.getAdicional().getId())
                    .collect(Collectors.toCollection(TreeSet::new));
            if (existingIds.equals(incomingIds)) {
                linea.setCantidad(linea.getCantidad() + cantidad);
                lineaPedidoRepository.save(linea);
                return carritoRepository.findById(carritoId).get();
            }
        }

        LineaPedido nuevaLinea = new LineaPedido(cantidad, comida, carrito, null);
        for (Adicional adicional : adicionales) {
            nuevaLinea.getAdicionales().add(new LineaPedidoAdicional(nuevaLinea, adicional));
        }
        lineaPedidoRepository.save(nuevaLinea);
        return carritoRepository.findById(carritoId).get();
    }

    @Override
    @Transactional
    public Carrito removeProducto(Long carritoId, Long lineaId) {
        if (!carritoRepository.existsById(carritoId)) {
            throw new NoSuchElementException("Carrito no encontrado.");
        }
        LineaPedido linea = lineaPedidoRepository.findById(lineaId).orElse(null);
        if (linea == null || linea.getCarrito() == null
                || !linea.getCarrito().getId().equals(carritoId)) {
            throw new NoSuchElementException("Línea de pedido no encontrada en este carrito.");
        }
        lineaPedidoRepository.deleteById(lineaId);
        return carritoRepository.findById(carritoId).get();
    }

    @Override
    @Transactional
    public Carrito aumentarCantidad(Long carritoId, Long lineaId) {
        return cambiarCantidad(carritoId, lineaId, +1);
    }

    @Override
    @Transactional
    public Carrito disminuirCantidad(Long carritoId, Long lineaId) {
        return cambiarCantidad(carritoId, lineaId, -1);
    }

    @Override
    @Transactional
    public Carrito vaciar(Long carritoId) {
        Carrito carrito = carritoRepository.findById(carritoId)
                .orElseThrow(() -> new NoSuchElementException("Carrito no encontrado."));
        carrito.getLineasPedido().clear();
        carritoRepository.save(carrito);
        return carritoRepository.findById(carritoId).get();
    }

    private Carrito cambiarCantidad(Long carritoId, Long lineaId, int delta) {
        if (!carritoRepository.existsById(carritoId)) {
            throw new NoSuchElementException("Carrito no encontrado.");
        }
        LineaPedido linea = lineaPedidoRepository.findById(lineaId).orElse(null);
        if (linea == null || linea.getCarrito() == null
                || !linea.getCarrito().getId().equals(carritoId)) {
            throw new NoSuchElementException("Línea de pedido no encontrada en este carrito.");
        }
        int nueva = linea.getCantidad() + delta;
        if (nueva <= 0) {
            lineaPedidoRepository.deleteById(lineaId);
        } else {
            linea.setCantidad(nueva);
            lineaPedidoRepository.save(linea);
        }
        return carritoRepository.findById(carritoId).get();
    }
}
