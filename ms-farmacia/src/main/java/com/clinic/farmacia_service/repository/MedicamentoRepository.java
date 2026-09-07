package com.clinic.farmacia_service.repository;

import com.clinic.farmacia_service.domain.Medicamento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MedicamentoRepository extends JpaRepository<Medicamento, Long> {
    Optional<Medicamento> findByCodigo(String codigo);
    List<Medicamento> findByActivoTrue();
    List<Medicamento> findByNombreContainingIgnoreCaseOrPrincipioActivoContainingIgnoreCase(String nombre, String principioActivo);
}
