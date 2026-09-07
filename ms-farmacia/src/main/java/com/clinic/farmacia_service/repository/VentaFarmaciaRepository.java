package com.clinic.farmacia_service.repository;

import com.clinic.farmacia_service.domain.VentaFarmacia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VentaFarmaciaRepository extends JpaRepository<VentaFarmacia, String> {
    Optional<VentaFarmacia> findByNumeroVenta(String numeroVenta);
    List<VentaFarmacia> findByPacienteId(Long pacienteId);
    List<VentaFarmacia> findBySedeId(Long sedeId);
    List<VentaFarmacia> findByEstadoVenta(VentaFarmacia.EstadoVenta estado);
}
