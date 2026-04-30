package com.example.demo.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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
import com.example.demo.repository.AdicionalRepository;
import com.example.demo.repository.CarritoRepository;
import com.example.demo.repository.ComidaRepository;
import com.example.demo.repository.LineaPedidoRepository;

@Service
public class CarritoServiceImpl implements CarritoService {

    @Autowired
    private CarritoRepository carritoRepository;

    @Autowired
    private LineaPedidoRepository lineaPedidoRepository;

    @Autowired
    private ComidaRepository comidaRepository;

    @Autowired
    private AdicionalRepository adicionalRepository;

    @Override
    public Optional<Carrito> findById(Long id) {
        return carritoRepository.findById(id);
    }

    @Override
    public Optional<Carrito> findByClienteId(Long clienteId) {
        return carritoRepository.findByClienteId(clienteId);
    }

    @Override
    public void save(Carrito carrito) {
        carritoRepository.save(carrito);
    }

    @Override
    public boolean existsById(Long id) {
        return carritoRepository.existsById(id);
    }

    // ── Implementación de lógica de negocio ──

    @Override
    @Transactional
    public Map<String, Object> addProducto(Long carritoId, Long comidaId, Integer cantidad, List<Long> adicionalesIds) {
        Optional<Carrito> carritoOpt = findById(carritoId);
        if (carritoOpt.isEmpty()) {
            return Map.of(
                "success", false,
                "error", "Carrito con id " + carritoId + " no encontrado."
            );
        }

        if (comidaId == null || cantidad == null || cantidad <= 0) {
            return Map.of(
                "success", false,
                "error", "Se requieren 'comidaId' y 'cantidad' (> 0)."
            );
        }

        Optional<Comida> comidaOpt = comidaRepository.findById(comidaId);
        if (comidaOpt.isEmpty()) {
            return Map.of(
                "success", false,
                "error", "Comida con id " + comidaId + " no encontrada."
            );
        }

        // Normalizar la lista de adicionales
        Set<Long> incomingIds = new TreeSet<>();
        if (adicionalesIds != null) {
            incomingIds.addAll(adicionalesIds);
        }

        // Validar que todos los adicionales existan
        List<Adicional> adicionales = new ArrayList<>();
        for (Long adicionalId : incomingIds) {
            Optional<Adicional> adicionalOpt = adicionalRepository.findById(adicionalId);
            if (adicionalOpt.isEmpty()) {
                return Map.of(
                    "success", false,
                    "error", "Adicional con id " + adicionalId + " no encontrado."
                );
            }
            adicionales.add(adicionalOpt.get());
        }

        Carrito carrito = carritoOpt.get();
        Comida comida = comidaOpt.get();

        // Buscar si ya existe una línea con la misma comida
        List<LineaPedido> lineasExistentes = lineaPedidoRepository.findByCarritoIdAndComidaId(carritoId, comidaId);

        for (LineaPedido linea : lineasExistentes) {
            Set<Long> existingIds = linea.getAdicionales().stream()
                    .map(lpa -> lpa.getAdicional().getId())
                    .collect(Collectors.toCollection(TreeSet::new));

            if (existingIds.equals(incomingIds)) {
                // Mismo conjunto de adicionales → solo suma la cantidad
                linea.setCantidad(linea.getCantidad() + cantidad);
                lineaPedidoRepository.save(linea);
                return Map.of(
                    "success", true,
                    "carrito", findById(carritoId).get()
                );
            }
        }

        // Adicionales distintos (o primera vez) → nueva línea
        LineaPedido nuevaLinea = new LineaPedido(cantidad, comida, carrito, null);
        for (Adicional adicional : adicionales) {
            LineaPedidoAdicional lpa = new LineaPedidoAdicional(nuevaLinea, adicional);
            nuevaLinea.getAdicionales().add(lpa);
        }
        lineaPedidoRepository.save(nuevaLinea);

        return Map.of(
            "success", true,
            "carrito", findById(carritoId).get()
        );
    }

    @Override
    @Transactional
    public Map<String, Object> removeProducto(Long carritoId, Long lineaPedidoId) {
        if (!existsById(carritoId)) {
            return Map.of(
                "success", false,
                "error", "Carrito no encontrado."
            );
        }

        Optional<LineaPedido> lineaOpt = lineaPedidoRepository.findById(lineaPedidoId);
        if (lineaOpt.isEmpty() || lineaOpt.get().getCarrito() == null
                || !lineaOpt.get().getCarrito().getId().equals(carritoId)) {
            return Map.of(
                "success", false,
                "error", "Línea de pedido no encontrada en el carrito."
            );
        }

        lineaPedidoRepository.deleteById(lineaPedidoId);

        return Map.of(
            "success", true,
            "carrito", findById(carritoId).get()
        );
    }

    @Override
    @Transactional
    public Map<String, Object> aumentarProducto(Long carritoId, Long lineaPedidoId) {
        return cambiarCantidad(carritoId, lineaPedidoId, 1);
    }

    @Override
    @Transactional
    public Map<String, Object> disminuirProducto(Long carritoId, Long lineaPedidoId) {
        return cambiarCantidad(carritoId, lineaPedidoId, -1);
    }

    @Override
    @Transactional
    public Map<String, Object> vaciarCarrito(Long carritoId) {
        Optional<Carrito> carritoOpt = findById(carritoId);
        if (carritoOpt.isEmpty()) {
            return Map.of(
                "success", false,
                "error", "Carrito no encontrado."
            );
        }

        Carrito carrito = carritoOpt.get();
        carrito.getLineasPedido().clear();
        save(carrito);

        return Map.of(
            "success", true,
            "carrito", findById(carritoId).get()
        );
    }

    // ── Helper ──

    private Map<String, Object> cambiarCantidad(Long carritoId, Long lineaPedidoId, int delta) {
        if (!existsById(carritoId)) {
            return Map.of(
                "success", false,
                "error", "Carrito no encontrado."
            );
        }

        Optional<LineaPedido> lineaOpt = lineaPedidoRepository.findById(lineaPedidoId);
        if (lineaOpt.isEmpty() || lineaOpt.get().getCarrito() == null
                || !lineaOpt.get().getCarrito().getId().equals(carritoId)) {
            return Map.of(
                "success", false,
                "error", "Línea de pedido no encontrada en el carrito."
            );
        }

        LineaPedido linea = lineaOpt.get();
        int nueva = linea.getCantidad() + delta;

        if (nueva <= 0) {
            lineaPedidoRepository.deleteById(lineaPedidoId);
            return Map.of(
                "success", true,
                "carrito", findById(carritoId).get()
            );
        }

        linea.setCantidad(nueva);
        lineaPedidoRepository.save(linea);

        return Map.of(
            "success", true,
            "carrito", findById(carritoId).get()
        );
    }
}
