package com.clinic.laboratorio_service.repository;

import com.clinic.laboratorio_service.domain.ExamenCatalogo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ExamenCatalogoRepository extends JpaRepository<ExamenCatalogo, Long> {
    Optional<ExamenCatalogo> findByCodigo(String codigo);
    List<ExamenCatalogo> findByActivoTrue();
    List<ExamenCatalogo> findByCategoriaAndActivoTrue(String categoria);
}
