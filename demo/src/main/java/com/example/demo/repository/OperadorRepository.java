package com.example.demo.repository;

import java.util.Optional;
import com.example.demo.entitys.Operador;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OperadorRepository extends JpaRepository<Operador, Long> {

    Optional<Operador> findByUsuario(String usuario);
}
