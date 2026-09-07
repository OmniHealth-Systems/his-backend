package com.clinic.turnos_service.repository;

import com.clinic.turnos_service.domain.AusenciaMedico;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AusenciaMedicoRepository extends JpaRepository<AusenciaMedico, Long> {
    List<AusenciaMedico> findByDoctorIdAndEstado(Long doctorId, AusenciaMedico.EstadoAusencia estado);
    List<AusenciaMedico> findByDoctorIdAndFechaInicioLessThanEqualAndFechaFinGreaterThanEqual(
            Long doctorId, LocalDateTime fechaFin, LocalDateTime fechaInicio);
}
