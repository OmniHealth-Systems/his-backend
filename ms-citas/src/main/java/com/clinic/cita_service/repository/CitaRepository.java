package com.clinic.cita_service.repository;

import com.clinic.cita_service.domain.Cita;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Repository
public interface CitaRepository extends JpaRepository<Cita, Long> {
    List<Cita> findByDoctorIdAndFechaCita(Long doctorId, LocalDate fechaCita);

    @Query(value = "SELECT * FROM cita c " +
            "WHERE c.doctor_id = :doctorId " +
            "AND c.fecha_cita = :fecha " +
            "AND ((c.hora_cita <= :horaFin " +
            "AND ADDTIME(c.hora_cita, SEC_TO_TIME(c.duracion_minutos * 60)) >= :horaInicio))",
            nativeQuery = true)
    List<Cita> findConflictingCitas(Long doctorId, LocalDate fecha, LocalTime horaInicio, LocalTime horaFin);
    List<Cita> findByPacienteId(Long pacienteId);
}