package com.example.demo.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import com.example.demo.entitys.Carrito;
import com.example.demo.entitys.Categoria;
import com.example.demo.entitys.Cliente;
import com.example.demo.entitys.Comida;
import com.example.demo.entitys.EstadoPedido;
import com.example.demo.entitys.LineaPedido;
import com.example.demo.entitys.Pedido;
import com.example.demo.entitys.Rol;
import com.example.demo.entitys.Usuario;

@DataJpaTest
public class LineaPedidoRepositoryTest {

    @Autowired private LineaPedidoRepository lineaPedidoRepo;
    @Autowired private CarritoRepository     carritoRepo;
    @Autowired private PedidoRepository      pedidoRepo;
    @Autowired private ComidaRepository      comidaRepo;
    @Autowired private CategoriaRepository   catRepo;
    @Autowired private ClienteRepository     clienteRepo;
    @Autowired private RolRepository         rolRepo;

    private Carrito carrito1;
    private Carrito carrito2;
    private Comida  comida1;
    private Comida  comida2;
    private Cliente cliente;

    @BeforeEach
    public void setUp() {
        Categoria categoria = catRepo.save(new Categoria("Clásicas"));
        comida1 = comidaRepo.save(new Comida("Margarita",  "Tomate y queso",  25000, null, true, categoria));
        comida2 = comidaRepo.save(new Comida("Pepperoni",  "Pepperoni extra", 28000, null, true, categoria));

        Rol rol = rolRepo.save(new Rol("CLIENTE"));
        cliente  = clienteRepo.save(new Cliente("Juan", "Pérez", "juan@mail.com", new Usuario("juanp", "pass1", rol), "Calle 1", "3001000000"));
        carrito1 = carritoRepo.save(new Carrito(cliente));
        carrito2 = carritoRepo.save(new Carrito(clienteRepo.save(
                new Cliente("Ana", "Gómez", "ana@mail.com", new Usuario("anag", "pass2", rol), "Calle 2", "3002000000"))));

        lineaPedidoRepo.save(new LineaPedido(1, comida1, carrito1, null));
        lineaPedidoRepo.save(new LineaPedido(2, comida2, carrito1, null));
        lineaPedidoRepo.save(new LineaPedido(1, comida1, carrito2, null));
    }

    @Test
    public void LineaPedidoRepository_findByCarritoId_ReturnsLinesForCarrito() {

        List<LineaPedido> result = lineaPedidoRepo.findByCarritoId(carrito1.getId());

        Assertions.assertThat(result).isNotEmpty();
        Assertions.assertThat(result).hasSize(2);
        Assertions.assertThat(result).allMatch(l -> l.getCarrito().getId().equals(carrito1.getId()));
    }

    @Test
    public void LineaPedidoRepository_findByCarritoId_ReturnsEmptyWhenNotExists() {

        List<LineaPedido> result = lineaPedidoRepo.findByCarritoId(999L);

        Assertions.assertThat(result).isEmpty();
    }

    @Test
    public void LineaPedidoRepository_findByCarritoIdAndComidaId_ReturnsMatchingLine() {

        List<LineaPedido> result = lineaPedidoRepo.findByCarritoIdAndComidaId(carrito1.getId(), comida1.getId());

        Assertions.assertThat(result).isNotEmpty();
        Assertions.assertThat(result).hasSize(1);
        Assertions.assertThat(result.get(0).getComida().getId()).isEqualTo(comida1.getId());
        Assertions.assertThat(result.get(0).getCarrito().getId()).isEqualTo(carrito1.getId());
    }

    @Test
    public void LineaPedidoRepository_findByCarritoIdAndComidaId_ReturnsEmptyWhenNoMatch() {

        List<LineaPedido> result = lineaPedidoRepo.findByCarritoIdAndComidaId(carrito2.getId(), comida2.getId());

        Assertions.assertThat(result).isEmpty();
    }

    @Test
    public void LineaPedidoRepository_existsByComidaIdAndPedidoIsNotNull_ReturnsTrueWhenLinkedToOrder() {

        Pedido pedido = pedidoRepo.save(new Pedido(LocalDateTime.now(), null, EstadoPedido.RECIBIDO, cliente));
        lineaPedidoRepo.save(new LineaPedido(1, comida1, null, pedido));

        boolean result = lineaPedidoRepo.existsByComidaIdAndPedidoIsNotNull(comida1.getId());

        Assertions.assertThat(result).isTrue();
    }

    @Test
    public void LineaPedidoRepository_existsByComidaIdAndPedidoIsNotNull_ReturnsFalseWhenOnlyInCart() {

        boolean result = lineaPedidoRepo.existsByComidaIdAndPedidoIsNotNull(comida1.getId());

        Assertions.assertThat(result).isFalse();
    }
}
