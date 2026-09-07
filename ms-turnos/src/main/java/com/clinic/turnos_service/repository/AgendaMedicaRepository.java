package com.clinic.turnos_service.repository;

import com.clinic.turnos_service.domain.AgendaMedica;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AgendaMedicaRepository extends JpaRepository<AgendaMedica, Long> {
    List<AgendaMedica> findByDoctorIdAndActivoTrue(Long doctorId);
    List<AgendaMedica> findBySedeIdAndActivoTrue(Long sedeId);
}
