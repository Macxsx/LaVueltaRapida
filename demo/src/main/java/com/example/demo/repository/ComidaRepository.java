package com.example.demo.repository;

import java.util.Collection;
import java.util.List;
import org.springframework.stereotype.Repository;
import com.example.demo.entitys.Comida;
import org.springframework.data.jpa.repository.JpaRepository;

@Repository
public interface ComidaRepository extends JpaRepository<Comida, Long> {
    List<Comida> findByCategoryIdAndIdNot(Long categoryId, Long id);
    List<Comida> findTop5ByAvailableTrue();
}