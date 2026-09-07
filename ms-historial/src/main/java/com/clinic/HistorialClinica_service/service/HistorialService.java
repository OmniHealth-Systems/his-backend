package com.clinic.HistorialClinica_service.service;

import com.clinic.HistorialClinica_service.DTO.HistorialDTO;
import com.clinic.HistorialClinica_service.DTO.RegistroMedicoDTO;
import com.clinic.HistorialClinica_service.domain.HistorialClinico;
import com.clinic.HistorialClinica_service.exception.HistorialNotFoundException;
import com.clinic.HistorialClinica_service.repository.HistorialRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class HistorialService {
    private final HistorialRepository historialRepository;
    private final RegistroService registroService;

    @Transactional
    public HistorialDTO obtenerPorPaciente(Long pacienteId) {
        HistorialClinico historial = historialRepository.findByPacienteId(pacienteId)
                .orElseThrow(() -> new HistorialNotFoundException(
                        "Historial no encontrado para paciente: " + pacienteId));

        return convertToDTO(historial);
    }

    @Transactional
    public void crearHistorialParaPaciente(Long pacienteId) {
        if (!historialRepository.existsByPacienteId(pacienteId)) {
            HistorialClinico historial = HistorialClinico.builder()
                    .pacienteId(pacienteId)
                    .build();
            historialRepository.save(historial);
            log.info("Historial creado para paciente: {}", pacienteId);
        }
    }

    private HistorialDTO convertToDTO(HistorialClinico historial) {
        List<RegistroMedicoDTO> registros = historial.getRegistros().stream()
                .map(registroService::convertToDTO)
                .collect(Collectors.toList());

        return HistorialDTO.builder()
                .id(historial.getId())
                .pacienteId(historial.getPacienteId())
                .fechaCreacion(historial.getFechaCreacion())
                .fechaUltimaActualizacion(historial.getFechaUltimaActualizacion())
                .registros(registros)
                .build();
    }
}