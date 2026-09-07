package com.clinic.turnos_service.repository;

import com.clinic.turnos_service.domain.Turno;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface TurnoRepository extends JpaRepository<Turno, Long> {
    List<Turno> findByDoctorIdAndEstado(Long doctorId, Turno.EstadoTurno estado);
    List<Turno> findByDoctorIdAndFechaHoraInicioBetween(Long doctorId, LocalDateTime inicio, LocalDateTime fin);
    List<Turno> findBySedeIdAndEstado(Long sedeId, Turno.EstadoTurno estado);
    Optional<Turno> findByCitaId(Long citaId);
}
