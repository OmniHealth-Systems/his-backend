package com.clinic.pacientes_service.service;
import com.clinic.pacientes_service.DTO.PacienteRequestDTO;
import com.clinic.pacientes_service.DTO.PacienteResponseDTO;
import com.clinic.pacientes_service.client.CitasClient;
import com.clinic.pacientes_service.domain.Direccion;
import com.clinic.pacientes_service.domain.Paciente;
import com.clinic.pacientes_service.domain.SeguroMedico;
import com.clinic.pacientes_service.exception.PacienteConCitasException;
import com.clinic.pacientes_service.exception.ResourceNotFoundException;
import com.clinic.pacientes_service.exception.UniqueConstraintViolationException;
import com.clinic.pacientes_service.repository.DireccionRepository;
import com.clinic.pacientes_service.repository.PacienteRepository;
import com.clinic.pacientes_service.repository.AseguradoraRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PacienteService {
    private final PacienteRepository pacienteRepository;
    private final DireccionRepository direccionRepository;
    private final AseguradoraRepository seguroMedicoRepository;
    private final CitasClient citasClient;

    @Transactional(readOnly = true)
    public List<PacienteResponseDTO> findAll() {
        return pacienteRepository.findAll().stream()
                .map(PacienteResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public PacienteResponseDTO findById(Long id) {
        Paciente paciente = pacienteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Paciente no encontrado con ID: " + id));
        return PacienteResponseDTO.fromEntity(paciente);
    }

    @Transactional
    public PacienteResponseDTO create(PacienteRequestDTO pacienteDTO) {
        // Validar unicidad de DNI y email
        if (pacienteRepository.existsByDni(pacienteDTO.getDni())) {
            throw new UniqueConstraintViolationException("Ya existe un paciente con DNI: " + pacienteDTO.getDni());
        }

        if (pacienteRepository.existsByEmail(pacienteDTO.getEmail())) {
            throw new UniqueConstraintViolationException("Ya existe un paciente con email: " + pacienteDTO.getEmail());
        }

        // Obtener direccion y seguro médico
        Direccion direccion = direccionRepository.findById(pacienteDTO.getDireccionId())
                .orElseThrow(() -> new ResourceNotFoundException("Dirección no encontrada"));

        SeguroMedico seguroMedico = seguroMedicoRepository.findById(pacienteDTO.getSeguroMedicoId())
                .orElseThrow(() -> new ResourceNotFoundException("Seguro médico no encontrado"));

        // Crear entidad Paciente
        Paciente paciente = Paciente.builder()
                .nombre(pacienteDTO.getNombre())
                .apellidos(pacienteDTO.getApellidos())
                .edad(pacienteDTO.getEdad())
                .email(pacienteDTO.getEmail())
                .fechaNacimiento(pacienteDTO.getFechaNacimiento())
                .sexo(pacienteDTO.getSexo())
                .estadoCivil(pacienteDTO.getEstadoCivil())
                .telefono(pacienteDTO.getTelefono())
                .nacionalidad(pacienteDTO.getNacionalidad())
                .direccion(direccion)
                .tipoDocumento(pacienteDTO.getTipoDocumento())
                .dni(pacienteDTO.getDni())
                .contactoEmergencia(pacienteDTO.getContactoEmergencia())
                .seguroMedico(seguroMedico)
                .build();

        Paciente savedPaciente = pacienteRepository.save(paciente);
        return PacienteResponseDTO.fromEntity(savedPaciente);
    }

    @Transactional
    public PacienteResponseDTO update(Long id, PacienteRequestDTO pacienteDTO) {
        Paciente paciente = pacienteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Paciente no encontrado con ID: " + id));

        // Validar cambios en DNI y email
        if (!paciente.getDni().equals(pacienteDTO.getDni()) &&
                pacienteRepository.existsByDni(pacienteDTO.getDni())) {
            throw new UniqueConstraintViolationException("Ya existe un paciente con DNI: " + pacienteDTO.getDni());
        }

        if (!paciente.getEmail().equals(pacienteDTO.getEmail()) &&
                pacienteRepository.existsByEmail(pacienteDTO.getEmail())) {
            throw new UniqueConstraintViolationException("Ya existe un paciente con email: " + pacienteDTO.getEmail());
        }

        // Obtener direccion y seguro médico
        Direccion direccion = direccionRepository.findById(pacienteDTO.getDireccionId())
                .orElseThrow(() -> new ResourceNotFoundException("Dirección no encontrada"));

        SeguroMedico seguroMedico = seguroMedicoRepository.findById(pacienteDTO.getSeguroMedicoId())
                .orElseThrow(() -> new ResourceNotFoundException("Seguro médico no encontrado"));

        // Actualizar datos
        paciente.setNombre(pacienteDTO.getNombre());
        paciente.setApellidos(pacienteDTO.getApellidos());
        paciente.setEdad(pacienteDTO.getEdad());
        paciente.setEmail(pacienteDTO.getEmail());
        paciente.setFechaNacimiento(pacienteDTO.getFechaNacimiento());
        paciente.setSexo(pacienteDTO.getSexo());
        paciente.setEstadoCivil(pacienteDTO.getEstadoCivil());
        paciente.setTelefono(pacienteDTO.getTelefono());
        paciente.setNacionalidad(pacienteDTO.getNacionalidad());
        paciente.setDireccion(direccion);
        paciente.setTipoDocumento(pacienteDTO.getTipoDocumento());
        paciente.setDni(pacienteDTO.getDni());
        paciente.setContactoEmergencia(pacienteDTO.getContactoEmergencia());
        paciente.setSeguroMedico(seguroMedico);

        Paciente updatedPaciente = pacienteRepository.save(paciente);
        return PacienteResponseDTO.fromEntity(updatedPaciente);
    }

    @Transactional
    public void delete(Long id) {
        Paciente paciente = pacienteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Paciente no encontrado con ID: " + id));

        // Verificar si el paciente tiene citas programadas
        if (citasClient.checkPacienteTieneCitas(id)) {
            throw new PacienteConCitasException("No se puede eliminar el paciente porque tiene citas programadas");
        }

        pacienteRepository.delete(paciente);
    }

    @Transactional(readOnly = true)
    public boolean existsById(Long id) {
        return pacienteRepository.existsById(id);
    }

    @Transactional(readOnly = true)
    public Optional<Paciente> findByDni(String dni) {
        return pacienteRepository.findByDni(dni);
    }
}
