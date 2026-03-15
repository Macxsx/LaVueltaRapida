package com.example.demo.repository;

import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.entitys.Cliente;

@Repository
public interface ClienteRepository extends JpaRepository<Cliente, Long> {
    Cliente findByUsername(String username);
    
}
