package com.clinic.HistorialClinica_service.repository;

import com.clinic.HistorialClinica_service.domain.RegistroMedico;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface RegistroMedicoRepository extends JpaRepository<RegistroMedico, Long> {

    @Query("SELECT COUNT(r) > 0 FROM RegistroMedico r " +
            "WHERE r.pacienteId = :pacienteId " +
            "AND r.doctorId = :doctorId " +
            "AND r.tipo = :tipo " +
            "AND r.fechaRegistro = :fechaRegistro")
    boolean existsByPacienteIdAndDoctorIdAndTipoAndFechaRegistro(
            @Param("pacienteId") Long pacienteId,
            @Param("doctorId") Long doctorId,
            @Param("tipo") RegistroMedico.TipoRegistro tipo,
            @Param("fechaRegistro") LocalDateTime fechaRegistro);
}