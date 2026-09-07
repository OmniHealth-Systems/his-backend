package com.clinic.cita_service.repository;

import com.clinic.cita_service.domain.TipoCita;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TipoCitaRepository extends JpaRepository<TipoCita, Long> {
    List<TipoCita> findByActivoTrue();
}