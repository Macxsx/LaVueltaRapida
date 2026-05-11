package com.example.demo.repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import com.example.demo.entitys.EstadoPedido;
import com.example.demo.entitys.Pedido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface PedidoRepository extends JpaRepository<Pedido, Long> {

    @Query("SELECT DISTINCT p FROM Pedido p " +
           "LEFT JOIN FETCH p.lineasPedido lp " +
           "LEFT JOIN FETCH lp.comida c " +
           "LEFT JOIN FETCH c.category " +
           "WHERE p.id = :id")
    Optional<Pedido> findByIdConLineas(@Param("id") Long id);

    List<Pedido> findByClienteId(Long clienteId);

    List<Pedido> findByClienteIdOrderByFechaCreacionDesc(Long clienteId);

    List<Pedido> findByEstadoNotOrderByFechaCreacionAsc(EstadoPedido estado);

    List<Pedido> findByEstadoInOrderByFechaCreacionAsc(Collection<EstadoPedido> estados);

    boolean existsByClienteId(Long clienteId);

    boolean existsByClienteIdAndEstadoNot(Long clienteId, EstadoPedido estado);

    boolean existsByClienteIdAndEstadoNotIn(Long clienteId, Collection<EstadoPedido> estados);

    boolean existsByDomiciliarioIdAndEstadoNot(Long domiciliarioId, EstadoPedido estado);

    boolean existsByDomiciliarioIdAndEstadoNotIn(Long domiciliarioId, Collection<EstadoPedido> estados);
}
