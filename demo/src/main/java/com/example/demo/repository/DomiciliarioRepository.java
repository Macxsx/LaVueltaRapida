package com.example.demo.repository;

import java.util.List;
import java.util.Optional;
import com.example.demo.entitys.Domiciliario;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.stereotype.Repository;

@Repository
public interface DomiciliarioRepository extends JpaRepository<Domiciliario, Long> {

    Optional<Domiciliario> findByCedula(String cedula);

    Optional<Domiciliario> findByCelular(String celular);

    List<Domiciliario> findByDisponibleTrue();

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<Domiciliario> findFirstByDisponibleTrueOrderByIdAsc();
}
