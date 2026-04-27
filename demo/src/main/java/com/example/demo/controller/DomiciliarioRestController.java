package com.example.demo.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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

import com.example.demo.entitys.Domiciliario;
import com.example.demo.entitys.EstadoPedido;
import com.example.demo.repository.DomiciliarioRepository;
import com.example.demo.repository.PedidoRepository;

@CrossOrigin(origins = {"http://localhost:5000", "http://127.0.0.1:5000"})
@RestController
@RequestMapping("/domiciliarios")
public class DomiciliarioRestController {

    @Autowired
    private DomiciliarioRepository domiciliarioRepository;

    @Autowired
    private PedidoRepository pedidoRepository;

    // ── GET /domiciliarios ───────────────────────────────────────────────────
    @GetMapping
    public List<Domiciliario> findAll() {
        return domiciliarioRepository.findAll();
    }

    // ── GET /domiciliarios/{id} ──────────────────────────────────────────────
    @GetMapping("/{id}")
    public ResponseEntity<Domiciliario> findById(@PathVariable Long id) {
        return domiciliarioRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // ── POST /domiciliarios ──────────────────────────────────────────────────
    @PostMapping
    public ResponseEntity<?> add(@RequestBody Domiciliario domiciliario) {
        domiciliario.setId(null);
        ResponseEntity<?> conflicto = validarUnicidad(domiciliario.getCedula(), domiciliario.getCelular(), null);
        if (conflicto != null) return conflicto;
        return ResponseEntity.ok(domiciliarioRepository.save(domiciliario));
    }

    // ── PUT /domiciliarios/{id} ──────────────────────────────────────────────
    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id,
                                    @RequestBody Domiciliario domiciliario) {
        Domiciliario stored = domiciliarioRepository.findById(id).orElse(null);
        if (stored == null) {
            return ResponseEntity.notFound().build();
        }
        ResponseEntity<?> conflicto = validarUnicidad(domiciliario.getCedula(), domiciliario.getCelular(), id);
        if (conflicto != null) return conflicto;
        stored.setNombre(domiciliario.getNombre());
        stored.setCedula(domiciliario.getCedula());
        stored.setCelular(domiciliario.getCelular());
        stored.setDisponible(domiciliario.isDisponible());
        return ResponseEntity.ok(domiciliarioRepository.save(stored));
    }

    /** Retorna un 409 si la cédula o el celular ya pertenecen a otro domiciliario; null si todo está bien. */
    private ResponseEntity<?> validarUnicidad(String cedula, String celular, Long idPropio) {
        boolean cedulaOcupada = domiciliarioRepository.findByCedula(cedula)
                .filter(d -> !d.getId().equals(idPropio))
                .isPresent();
        if (cedulaOcupada) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("error", "Ya existe un domiciliario con la cédula '" + cedula + "'."));
        }
        boolean celularOcupado = domiciliarioRepository.findByCelular(celular)
                .filter(d -> !d.getId().equals(idPropio))
                .isPresent();
        if (celularOcupado) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("error", "Ya existe un domiciliario con el celular '" + celular + "'."));
        }
        return null;
    }

    // ── DELETE /domiciliarios/{id} ───────────────────────────────────────────
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        if (!domiciliarioRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        if (pedidoRepository.existsByDomiciliarioIdAndEstadoNot(id, EstadoPedido.ENTREGADO)) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("error", "No se puede eliminar el domiciliario porque tiene un pedido en curso."));
        }
        domiciliarioRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
