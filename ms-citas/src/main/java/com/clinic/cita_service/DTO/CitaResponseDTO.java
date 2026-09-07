package com.clinic.cita_service.DTO;

import com.clinic.cita_service.domain.Cita;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
public class CitaResponseDTO {
    private Long id;
    private Long pacienteId;
    private Long doctorId;
    private Long sedeId;
    private LocalDate fechaCita;
    private LocalTime horaCita;
    private Integer duracionMinutos;
    private String tipoCita;
    private Cita.EstadoCita estado;
    private String motivo;
    private String observaciones;

    public static CitaResponseDTO fromEntity(Cita cita) {
        CitaResponseDTO dto = new CitaResponseDTO();
        dto.setId(cita.getId());
        dto.setPacienteId(cita.getPacienteId());
        dto.setDoctorId(cita.getDoctorId());
        dto.setSedeId(cita.getSedeId());
        dto.setFechaCita(cita.getFechaCita());
        dto.setHoraCita(cita.getHoraCita());
        dto.setDuracionMinutos(cita.getDuracionMinutos());
        dto.setTipoCita(cita.getTipoCita().getNombre());
        dto.setEstado(cita.getEstado());
        dto.setMotivo(cita.getMotivo());
        dto.setObservaciones(cita.getObservaciones());
        return dto;
    }
}