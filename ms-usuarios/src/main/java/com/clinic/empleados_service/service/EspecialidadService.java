package com.clinic.empleados_service.service;

import com.clinic.empleados_service.DTO.EspecialidadDTO;
import com.clinic.empleados_service.domain.Especialidad;
import com.clinic.empleados_service.exception.ResourceNotFoundException;
import com.clinic.empleados_service.repository.EspecialidadRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EspecialidadService {

    private final EspecialidadRepository especialidadRepository;

    @Transactional
    public EspecialidadDTO createEspecialidad(EspecialidadDTO especialidadDTO) {
        Especialidad especialidad = Especialidad.builder()
                .nombre(especialidadDTO.getNombre())
                .descripcion(especialidadDTO.getDescripcion())
                .codigo(especialidadDTO.getCodigo())
                .build();

        Especialidad savedEspecialidad = especialidadRepository.save(especialidad);
        return convertToDTO(savedEspecialidad);
    }

    @Transactional(readOnly = true)
    public List<EspecialidadDTO> findAll() {
        return especialidadRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public EspecialidadDTO findById(Long id) {
        Especialidad especialidad = especialidadRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Especialidad no encontrada con ID: " + id));
        return convertToDTO(especialidad);
    }

    private EspecialidadDTO convertToDTO(Especialidad especialidad) {
        return EspecialidadDTO.builder()
                .id(especialidad.getId())
                .nombre(especialidad.getNombre())
                .descripcion(especialidad.getDescripcion())
                .codigo(especialidad.getCodigo())
                .build();
    }
}