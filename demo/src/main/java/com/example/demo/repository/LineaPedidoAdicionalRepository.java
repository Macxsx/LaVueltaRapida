package com.example.demo.repository;


import com.example.demo.entitys.LineaPedidoAdicional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LineaPedidoAdicionalRepository extends JpaRepository<LineaPedidoAdicional, Long> {

    boolean existsByAdicionalIdAndLineaPedidoPedidoIsNotNull(Long adicionalId);
}