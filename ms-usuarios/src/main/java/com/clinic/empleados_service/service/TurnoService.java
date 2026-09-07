package com.clinic.empleados_service.service;

import com.clinic.empleados_service.DTO.TurnoDTO;
import com.clinic.empleados_service.domain.Turno;
import com.clinic.empleados_service.exception.ResourceNotFoundException;
import com.clinic.empleados_service.repository.TurnoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TurnoService {

    private final TurnoRepository turnoRepository;

    @Transactional
    public TurnoDTO createTurno(TurnoDTO turnoDTO) {
        Turno turno = Turno.builder()
                .fecha(turnoDTO.getFecha())
                .lugar(turnoDTO.getLugar())
                .estado(turnoDTO.getEstado())
                .build();

        Turno savedTurno = turnoRepository.save(turno);
        return convertToDTO(savedTurno);
    }

    @Transactional
    public void actualizarEstadoTurno(Long id, Turno.EstadoTurno estado) {
        Turno turno = turnoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Turno no encontrado con ID: " + id));
        turno.setEstado(estado);
        turnoRepository.save(turno);
    }

    @Transactional(readOnly = true)
    public List<TurnoDTO> findAll() {
        return turnoRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    private TurnoDTO convertToDTO(Turno turno) {
        return TurnoDTO.builder()
                .id(turno.getId())
                .fecha(turno.getFecha())
                .lugar(turno.getLugar())
                .estado(turno.getEstado())
                .build();
    }
}