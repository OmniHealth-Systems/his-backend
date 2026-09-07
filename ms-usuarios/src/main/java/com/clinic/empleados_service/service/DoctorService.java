package com.clinic.empleados_service.service;

import com.clinic.empleados_service.DTO.DoctorDTO;
import com.clinic.empleados_service.client.FranquiciasClient;
import com.clinic.empleados_service.domain.Doctor;
import com.clinic.empleados_service.domain.Especialidad;
import com.clinic.empleados_service.domain.Turno;
import com.clinic.empleados_service.exception.ResourceNotFoundException;
import com.clinic.empleados_service.repository.DoctorRepository;
import com.clinic.empleados_service.repository.EspecialidadRepository;
import com.clinic.empleados_service.repository.TurnoRepository;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DoctorService {

    private final DoctorRepository doctorRepository;
    private final EspecialidadRepository especialidadRepository;
    private final TurnoRepository turnoRepository;
    private final FranquiciasClient franquiciasClient;

    @Transactional
    public DoctorDTO createDoctor(DoctorDTO doctorDTO) {
        Especialidad especialidad = especialidadRepository.findById(doctorDTO.getEspecialidad().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Especialidad no encontrada"));

        Turno turno = turnoRepository.findById(doctorDTO.getTurno().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Turno no encontrado"));

        Doctor doctor = Doctor.builder()
                .nombreCompleto(doctorDTO.getNombreCompleto())
                .cmp(doctorDTO.getCmp())
                .especialidad(especialidad)
                .turno(turno)
                .build();

        Doctor savedDoctor = doctorRepository.save(doctor);
        return convertToDTO(savedDoctor);
    }

    @Transactional(readOnly = true)
    public List<DoctorDTO> findAll() {
        return doctorRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public DoctorDTO findById(Long id) {
        Doctor doctor = doctorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Doctor no encontrado con ID: " + id));
        return convertToDTO(doctor);
    }

    @Transactional(readOnly = true)
    public boolean existsById(Long id) {
        return doctorRepository.existsById(id);
    }

    @Transactional(readOnly = true)
    public boolean isDoctorDisponible(Long id) {
        Doctor doctor = doctorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Doctor no encontrado"));

        // Verificar si el turno del doctor está disponible
        return doctor.getTurno().getEstado() == Turno.EstadoTurno.DISPONIBLE;
    }

    @Transactional(readOnly = true)
    public Long getEspecialidadId(Long doctorId) {
        Doctor doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new ResourceNotFoundException("Doctor no encontrado"));
        return doctor.getEspecialidad().getId();
    }

    private DoctorDTO convertToDTO(Doctor doctor) {
        return DoctorDTO.builder()
                .id(doctor.getId())
                .nombreCompleto(doctor.getNombreCompleto())
                .cmp(doctor.getCmp())
                .especialidad(doctor.getEspecialidad())
                .turno(doctor.getTurno())
                .build();
    }
}