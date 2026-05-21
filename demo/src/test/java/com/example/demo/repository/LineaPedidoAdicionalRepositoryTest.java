package com.example.demo.repository;

import java.time.LocalDateTime;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import com.example.demo.entitys.Adicional;
import com.example.demo.entitys.Categoria;
import com.example.demo.entitys.Cliente;
import com.example.demo.entitys.Comida;
import com.example.demo.entitys.EstadoPedido;
import com.example.demo.entitys.LineaPedido;
import com.example.demo.entitys.LineaPedidoAdicional;
import com.example.demo.entitys.Pedido;
import com.example.demo.entitys.Rol;
import com.example.demo.entitys.Usuario;

@DataJpaTest
public class LineaPedidoAdicionalRepositoryTest {

    @Autowired private LineaPedidoAdicionalRepository lineaPedidoAdicionalRepo;
    @Autowired private LineaPedidoRepository          lineaPedidoRepo;
    @Autowired private PedidoRepository               pedidoRepo;
    @Autowired private AdicionalRepository            adicionalRepo;
    @Autowired private ComidaRepository               comidaRepo;
    @Autowired private CategoriaRepository            catRepo;
    @Autowired private ClienteRepository              clienteRepo;
    @Autowired private RolRepository                  rolRepo;

    private Adicional adicional;
    private Comida comida;
    private Cliente cliente;

    @BeforeEach
    public void setUp() {
        Categoria categoria = catRepo.save(new Categoria("Clásicas"));
        comida    = comidaRepo.save(new Comida("Margarita", "Tomate y queso", 25000, null, true, categoria));
        adicional = adicionalRepo.save(new Adicional("Extra queso", 2000, true));
        Rol rol = rolRepo.save(new Rol("CLIENTE"));
        cliente   = clienteRepo.save(new Cliente("Juan", "Pérez", "juan@mail.com", new Usuario("juanp", "pass1", rol), "Calle 1", "3001000000"));
    }

    @Test
    public void LineaPedidoAdicionalRepository_existsByAdicionalIdAndLineaPedidoPedidoIsNotNull_ReturnsTrueWhenLinkedToOrder() {

        Pedido pedido = pedidoRepo.save(new Pedido(LocalDateTime.now(), null, EstadoPedido.RECIBIDO, cliente));
        LineaPedido lineaPedido = lineaPedidoRepo.save(new LineaPedido(1, comida, null, pedido));
        lineaPedidoAdicionalRepo.save(new LineaPedidoAdicional(lineaPedido, adicional));

        boolean result = lineaPedidoAdicionalRepo.existsByAdicionalIdAndLineaPedidoPedidoIsNotNull(adicional.getId());

        Assertions.assertThat(result).isTrue();
    }

    @Test
    public void LineaPedidoAdicionalRepository_existsByAdicionalIdAndLineaPedidoPedidoIsNotNull_ReturnsFalseWhenOnlyInCart() {

        LineaPedido lineaPedidoEnCarrito = lineaPedidoRepo.save(new LineaPedido(1, comida, null, null));
        lineaPedidoAdicionalRepo.save(new LineaPedidoAdicional(lineaPedidoEnCarrito, adicional));

        boolean result = lineaPedidoAdicionalRepo.existsByAdicionalIdAndLineaPedidoPedidoIsNotNull(adicional.getId());

        Assertions.assertThat(result).isFalse();
    }

    @Test
    public void LineaPedidoAdicionalRepository_existsByAdicionalIdAndLineaPedidoPedidoIsNotNull_ReturnsFalseWhenAdicionalNotUsed() {

        boolean result = lineaPedidoAdicionalRepo.existsByAdicionalIdAndLineaPedidoPedidoIsNotNull(adicional.getId());

        Assertions.assertThat(result).isFalse();
    }
}
