package com.example.demo.repository;

import java.util.List;
import com.example.demo.entitys.EstadoPedido;
import com.example.demo.entitys.Pedido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PedidoRepository extends JpaRepository<Pedido, Long> {

    List<Pedido> findByClienteId(Long clienteId);

    List<Pedido> findByClienteIdOrderByFechaCreacionDesc(Long clienteId);

    List<Pedido> findByEstadoNotOrderByFechaCreacionAsc(EstadoPedido estado);
}
