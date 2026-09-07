package com.clinic.logistica_service.repository;

import com.clinic.logistica_service.domain.InsumoHospitalario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InsumoRepository extends JpaRepository<InsumoHospitalario, Long> {
    List<InsumoHospitalario> findBySedeId(Long sedeId);
    List<InsumoHospitalario> findByCategoria(InsumoHospitalario.CategoriaInsumo categoria);
    Optional<InsumoHospitalario> findByCodigoBarras(String codigoBarras);
    List<InsumoHospitalario> findByStockActualLessThanEqual(Integer stockMinimo);
}
