package com.clinic.HistorialClinica_service.repository;

import com.clinic.HistorialClinica_service.domain.HistorialClinico;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface HistorialRepository extends JpaRepository<HistorialClinico, Long> {
    Optional<HistorialClinico> findByPacienteId(Long pacienteId);
    boolean existsByPacienteId(Long pacienteId);
}