package com.clinic.farmacia_service.repository;

import com.clinic.farmacia_service.domain.DetalleVentaFarmacia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DetalleVentaFarmaciaRepository extends JpaRepository<DetalleVentaFarmacia, Long> {
    List<DetalleVentaFarmacia> findByVentaId(String ventaId);
}
