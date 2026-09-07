package com.clinic.Franquicia_service.repository;

import com.clinic.Franquicia_service.domain.CentroDiagnostico;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CentroDiagnosticoRepository extends JpaRepository<CentroDiagnostico, Long> {
    List<CentroDiagnostico> findBySedeId(Long sedeId);
    List<CentroDiagnostico> findByEstado(String estado);
}