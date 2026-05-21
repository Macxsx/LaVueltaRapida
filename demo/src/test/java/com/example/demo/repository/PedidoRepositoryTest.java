package com.example.demo.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import com.example.demo.entitys.Cliente;
import com.example.demo.entitys.Domiciliario;
import com.example.demo.entitys.EstadoPedido;
import com.example.demo.entitys.Pedido;
import com.example.demo.entitys.Rol;
import com.example.demo.entitys.Usuario;

@DataJpaTest
public class PedidoRepositoryTest {

    @Autowired private PedidoRepository       pedidoRepo;
    @Autowired private ClienteRepository      clienteRepo;
    @Autowired private DomiciliarioRepository domiciliarioRepo;
    @Autowired private RolRepository          rolRepo;

    private Cliente     cliente1;
    private Cliente     cliente2;
    private Domiciliario domiciliario;
    private Rol         rolCliente;

    @BeforeEach
    public void setUp() {
        rolCliente   = rolRepo.save(new Rol("CLIENTE"));
        cliente1     = clienteRepo.save(new Cliente("Juan", "Pérez", "juan@mail.com", new Usuario("juanp", "pass1", rolCliente), "Calle 1", "3001000001"));
        cliente2     = clienteRepo.save(new Cliente("Ana",  "Gómez", "ana@mail.com",  new Usuario("anag",  "pass2", rolCliente), "Calle 2", "3001000002"));
        domiciliario = domiciliarioRepo.save(new Domiciliario("Carlos Ruiz", "123456", "3101000001", true));

        pedidoRepo.save(new Pedido(LocalDateTime.now().minusHours(3), null, EstadoPedido.ENTREGADO, cliente1));
        pedidoRepo.save(new Pedido(LocalDateTime.now().minusHours(2), null, EstadoPedido.COCINANDO,  cliente1));
        pedidoRepo.save(new Pedido(LocalDateTime.now().minusHours(1), null, EstadoPedido.RECIBIDO,   cliente2));
    }

    @Test
    public void PedidoRepository_findByClienteId_ReturnsAllOrdersForCliente() {

        List<Pedido> result = pedidoRepo.findByClienteId(cliente1.getId());

        Assertions.assertThat(result).hasSize(2);
        Assertions.assertThat(result).allMatch(p -> p.getCliente().getId().equals(cliente1.getId()));
    }

    @Test
    public void PedidoRepository_findByClienteId_ReturnsEmptyWhenNoOrders() {

        List<Pedido> result = pedidoRepo.findByClienteId(999L);

        Assertions.assertThat(result).isEmpty();
    }

    @Test
    public void PedidoRepository_findByClienteIdOrderByFechaCreacionDesc_ReturnsOrderedDesc() {

        List<Pedido> result = pedidoRepo.findByClienteIdOrderByFechaCreacionDesc(cliente1.getId());

        Assertions.assertThat(result).hasSize(2);
        Assertions.assertThat(result).isSortedAccordingTo(
                (p1, p2) -> p2.getFechaCreacion().compareTo(p1.getFechaCreacion()));
    }

    @Test
    public void PedidoRepository_findByEstadoNotOrderByFechaCreacionAsc_ExcludesEstadoAndOrdersAsc() {

        List<Pedido> result = pedidoRepo.findByEstadoNotOrderByFechaCreacionAsc(EstadoPedido.ENTREGADO);

        Assertions.assertThat(result).hasSize(2);
        Assertions.assertThat(result).noneMatch(p -> p.getEstado() == EstadoPedido.ENTREGADO);
        Assertions.assertThat(result).isSortedAccordingTo(
                (p1, p2) -> p1.getFechaCreacion().compareTo(p2.getFechaCreacion()));
    }

    @Test
    public void PedidoRepository_existsByClienteId_ReturnsTrueWhenExists() {

        boolean result = pedidoRepo.existsByClienteId(cliente1.getId());

        Assertions.assertThat(result).isTrue();
    }

    @Test
    public void PedidoRepository_existsByClienteId_ReturnsFalseWhenNotExists() {

        boolean result = pedidoRepo.existsByClienteId(999L);

        Assertions.assertThat(result).isFalse();
    }

    @Test
    public void PedidoRepository_existsByClienteIdAndEstadoNot_ReturnsTrueWhenMatchingOrderExists() {

        boolean result = pedidoRepo.existsByClienteIdAndEstadoNot(cliente1.getId(), EstadoPedido.ENTREGADO);

        Assertions.assertThat(result).isTrue();
    }

    @Test
    public void PedidoRepository_existsByClienteIdAndEstadoNot_ReturnsFalseWhenAllOrdersAreExcludedEstado() {

        Cliente clienteSoloEntregados = clienteRepo.save(new Cliente("Luis", "Torres", "luis@mail.com", new Usuario("luist", "pass4", rolCliente), "Calle 4", "3001000004"));
        pedidoRepo.save(new Pedido(LocalDateTime.now(), null, EstadoPedido.ENTREGADO, clienteSoloEntregados));

        boolean result = pedidoRepo.existsByClienteIdAndEstadoNot(clienteSoloEntregados.getId(), EstadoPedido.ENTREGADO);

        Assertions.assertThat(result).isFalse();
    }

    @Test
    public void PedidoRepository_existsByDomiciliarioIdAndEstadoNot_ReturnsTrueWhenActiveOrderExists() {

        Pedido pedido = pedidoRepo.findByClienteId(cliente1.getId()).stream()
                .filter(p -> p.getEstado() == EstadoPedido.COCINANDO)
                .findFirst().get();
        pedido.setDomiciliario(domiciliario);
        pedidoRepo.save(pedido);

        boolean result = pedidoRepo.existsByDomiciliarioIdAndEstadoNot(domiciliario.getId(), EstadoPedido.ENTREGADO);

        Assertions.assertThat(result).isTrue();
    }

    @Test
    public void PedidoRepository_existsByDomiciliarioIdAndEstadoNot_ReturnsFalseWhenNoDomiciliarioOrders() {

        boolean result = pedidoRepo.existsByDomiciliarioIdAndEstadoNot(domiciliario.getId(), EstadoPedido.ENTREGADO);

        Assertions.assertThat(result).isFalse();
    }
}
