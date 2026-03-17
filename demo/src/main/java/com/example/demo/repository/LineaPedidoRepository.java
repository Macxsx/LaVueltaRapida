package com.example.demo.repository;

import java.util.List;
import com.example.demo.entitys.LineaPedido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface LineaPedidoRepository extends JpaRepository<LineaPedido, Long> {

    List<LineaPedido> findByComidaId(Long comidaId);

    @Modifying
    @Query("UPDATE LineaPedido l SET l.comida = null WHERE l.comida.id = :comidaId")
    void nullifyComidaById(@Param("comidaId") Long comidaId);

    List<LineaPedido> findByCarritoId(Long carritoId);
}