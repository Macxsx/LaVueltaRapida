package com.example.demo.repository;

import java.util.List;
import com.example.demo.entitys.Domiciliario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DomiciliarioRepository extends JpaRepository<Domiciliario, Long> {
}
