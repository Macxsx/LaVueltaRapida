package com.example.demo.repository;


import java.util.List;
import com.example.demo.entitys.Adicional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface AdicionalRepository extends JpaRepository<Adicional, Long> {
    List<Adicional> findByCategorias_IdAndAvailableTrue(Long categoriaId);
    
    @Modifying
    @Transactional
    @Query(value = "DELETE FROM categoria_adicional WHERE adicional_id = :adicionalId", nativeQuery = true)
    void deleteFromCategorias(@Param("adicionalId") Long adicionalId);
}
