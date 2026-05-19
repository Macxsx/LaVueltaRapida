package com.example.demo.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.demo.entitys.Cliente;
import com.example.demo.entitys.Usuario;

@Repository
public interface ClienteRepository extends JpaRepository<Cliente, Long> {

    @Query("SELECT c FROM Cliente c WHERE c.usuario.username = :username")
    Cliente findByUsername(@Param("username") String username);

    Cliente findByEmail(String email);

    Optional<Cliente> findByUsuario(Usuario usuario);
}
