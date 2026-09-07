package com.clinic.turnos_service.service;

import com.clinic.turnos_service.domain.Turno;
import com.clinic.turnos_service.repository.TurnoRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class TurnoService {

    private final TurnoRepository turnoRepository;

    public List<Turno> obtenerTurnosDisponibles(Long doctorId) {
        return turnoRepository.findByDoctorIdAndEstado(doctorId, Turno.EstadoTurno.DISPONIBLE);
    }

    public List<Turno> obtenerTurnosPorRango(Long doctorId, LocalDateTime inicio, LocalDateTime fin) {
        return turnoRepository.findByDoctorIdAndFechaHoraInicioBetween(doctorId, inicio, fin);
    }

    @Transactional
    public Turno reservarTurno(Long turnoId, Long citaId) {
        Turno turno = turnoRepository.findById(turnoId)
                .orElseThrow(() -> new EntityNotFoundException("Turno no encontrado con ID: " + turnoId));
        if (turno.getEstado() != Turno.EstadoTurno.DISPONIBLE) {
            throw new IllegalStateException("El turno no está disponible para reserva.");
        }
        turno.setEstado(Turno.EstadoTurno.RESERVADO);
        turno.setCitaId(citaId);
        return turnoRepository.save(turno);
    }

    @Transactional
    public Turno liberarTurno(Long citaId) {
        Turno turno = turnoRepository.findByCitaId(citaId)
                .orElseThrow(() -> new EntityNotFoundException("No se encontró turno asignado a la cita: " + citaId));
        turno.setEstado(Turno.EstadoTurno.DISPONIBLE);
        turno.setCitaId(null);
        return turnoRepository.save(turno);
    }

    public boolean verificarDisponibilidadDoctor(Long doctorId) {
        List<Turno> disponibles = turnoRepository.findByDoctorIdAndEstado(doctorId, Turno.EstadoTurno.DISPONIBLE);
        return !disponibles.isEmpty();
    }
}
