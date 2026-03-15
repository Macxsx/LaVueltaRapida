package com.example.demo.repository;


import java.util.List;
import com.example.demo.entitys.Adicional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AdicionalRepository  extends JpaRepository<Adicional, Long> {
    
}
