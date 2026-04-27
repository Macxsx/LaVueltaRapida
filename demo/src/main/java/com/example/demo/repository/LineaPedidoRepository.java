package com.example.demo.repository;

import java.util.List;
import com.example.demo.entitys.LineaPedido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LineaPedidoRepository extends JpaRepository<LineaPedido, Long> {

    List<LineaPedido> findByCarritoId(Long carritoId);

    List<LineaPedido> findByCarritoIdAndComidaId(Long carritoId, Long comidaId);

    boolean existsByComidaIdAndPedidoIsNotNull(Long comidaId);
}
