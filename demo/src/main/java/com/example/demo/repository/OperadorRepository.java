package com.example.demo.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.demo.entitys.Operador;

@Repository
public interface OperadorRepository extends JpaRepository<Operador, Long> {

    @Query("SELECT o FROM Operador o WHERE o.usuario.username = :username")
    Optional<Operador> findByUsuario(@Param("username") String username);
}
