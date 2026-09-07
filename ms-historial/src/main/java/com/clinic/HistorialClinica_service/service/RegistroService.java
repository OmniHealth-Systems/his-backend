package com.clinic.HistorialClinica_service.service;

import com.clinic.HistorialClinica_service.DTO.NuevoRegistroDTO;
import com.clinic.HistorialClinica_service.DTO.RegistroMedicoDTO;
import com.clinic.HistorialClinica_service.domain.HistorialClinico;
import com.clinic.HistorialClinica_service.domain.RegistroMedico;
import com.clinic.HistorialClinica_service.exception.DuplicateRecordException;
import com.clinic.HistorialClinica_service.repository.HistorialRepository;
import com.clinic.HistorialClinica_service.repository.RegistroMedicoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;



@Service
@RequiredArgsConstructor
@Slf4j
public class RegistroService {
    private final RegistroMedicoRepository registroMedicoRepository;
    private final HistorialRepository historialRepository;

    @Transactional
    public RegistroMedicoDTO crearRegistro(NuevoRegistroDTO dto) {
        // Validar duplicidad usando campos esenciales
        if (registroMedicoRepository.existsByPacienteIdAndDoctorIdAndTipoAndFechaRegistro(
                dto.getPacienteId(),
                dto.getDoctorId(),
                dto.getTipo(),
                dto.getFechaRegistro())) {
            throw new DuplicateRecordException("Registro médico duplicado");
        }

        HistorialClinico historial = historialRepository.findByPacienteId(dto.getPacienteId())
                .orElseGet(() -> crearNuevoHistorial(dto.getPacienteId()));

        RegistroMedico registro = RegistroMedico.builder()
                .tipo(dto.getTipo())
                .fechaRegistro(dto.getFechaRegistro())
                .doctorId(dto.getDoctorId())
                .citaId(dto.getCitaId())
                .pacienteId(dto.getPacienteId())
                .observaciones(dto.getObservaciones())
                .historial(historial)
                .build();

        try {
            registro = registroMedicoRepository.save(registro);
            log.info("Registro médico creado: {}", registro.getId());
        } catch (DataIntegrityViolationException e) {
            throw new DuplicateRecordException("Violación de restricción única: " + e.getMessage());
        }

        return convertToDTO(registro);
    }

    private HistorialClinico crearNuevoHistorial(Long pacienteId) {
        HistorialClinico nuevoHistorial = HistorialClinico.builder()
                .pacienteId(pacienteId)
                .build();
        return historialRepository.save(nuevoHistorial);
    }

    public RegistroMedicoDTO convertToDTO(RegistroMedico registro) {
        return RegistroMedicoDTO.builder()
                .id(registro.getId())
                .tipo(registro.getTipo())
                .fechaRegistro(registro.getFechaRegistro())
                .doctorId(registro.getDoctorId())
                .citaId(registro.getCitaId())
                .pacienteId(registro.getPacienteId())
                .observaciones(registro.getObservaciones())
                .build();
    }
}