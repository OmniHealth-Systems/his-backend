package com.clinic.consultas_service.service;

import com.clinic.consultas_service.DTO.ConsultaRequestDTO;
import com.clinic.consultas_service.DTO.ConsultaResponseDTO;
import com.clinic.consultas_service.domain.Consulta;
import com.clinic.consultas_service.domain.Diagnostico;
import com.clinic.consultas_service.domain.Receta;
import com.clinic.consultas_service.domain.SignosVitales;
import com.clinic.consultas_service.repository.ConsultaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ConsultaService {

    private final ConsultaRepository consultaRepository;
    private final com.clinic.consultas_service.kafka.ExamenKafkaProducer examenKafkaProducer;

    @Transactional
    public ConsultaResponseDTO registrarConsulta(ConsultaRequestDTO dto) {
        SignosVitales signosVitales = null;
        if (dto.getSignosVitales() != null) {
            Double imc = null;
            if (dto.getSignosVitales().getPeso() != null && dto.getSignosVitales().getTalla() != null && dto.getSignosVitales().getTalla() > 0) {
                double tallaM = dto.getSignosVitales().getTalla() / 100.0;
                imc = Math.round((dto.getSignosVitales().getPeso() / (tallaM * tallaM)) * 100.0) / 100.0;
            }

            signosVitales = SignosVitales.builder()
                    .presionArterial(dto.getSignosVitales().getPresionArterial())
                    .frecuenciaCardiaca(dto.getSignosVitales().getFrecuenciaCardiaca())
                    .temperatura(dto.getSignosVitales().getTemperatura())
                    .peso(dto.getSignosVitales().getPeso())
                    .talla(dto.getSignosVitales().getTalla())
                    .imc(imc)
                    .saturacionOxigeno(dto.getSignosVitales().getSaturacionOxigeno())
                    .build();
        }

        List<Diagnostico> diagnosticos = new ArrayList<>();
        if (dto.getDiagnosticos() != null) {
            diagnosticos = dto.getDiagnosticos().stream().map(d -> Diagnostico.builder()
                    .codigoCie10(d.getCodigoCie10())
                    .descripcion(d.getDescripcion())
                    .tipo(d.getTipo())
                    .build()).collect(Collectors.toList());
        }

        List<Receta> recetas = new ArrayList<>();
        if (dto.getRecetas() != null) {
            recetas = dto.getRecetas().stream().map(r -> Receta.builder()
                    .medicamento(r.getMedicamento())
                    .dosis(r.getDosis())
                    .frecuencia(r.getFrecuencia())
                    .duracion(r.getDuracion())
                    .indicaciones(r.getIndicaciones())
                    .build()).collect(Collectors.toList());
        }

        Consulta consulta = Consulta.builder()
                .citaId(dto.getCitaId())
                .pacienteId(dto.getPacienteId())
                .doctorId(dto.getDoctorId())
                .fechaConsulta(LocalDateTime.now())
                .motivoConsulta(dto.getMotivoConsulta())
                .anamnesis(dto.getAnamnesis())
                .examenFisico(dto.getExamenFisico())
                .signosVitales(signosVitales)
                .diagnosticos(diagnosticos)
                .recetas(recetas)
                .planTratamiento(dto.getPlanTratamiento())
                .requiereExamenLaboratorio(dto.getRequiereExamenLaboratorio() != null ? dto.getRequiereExamenLaboratorio() : false)
                .examenesSolicitados(dto.getExamenesSolicitados())
                .estado(Consulta.EstadoConsulta.FINALIZADA)
                .build();

        Consulta saved = consultaRepository.save(consulta);

        if (Boolean.TRUE.equals(saved.getRequiereExamenLaboratorio()) && saved.getExamenesSolicitados() != null) {
            com.clinic.consultas_service.event.ExamenSolicitadoEvent event = com.clinic.consultas_service.event.ExamenSolicitadoEvent.builder()
                    .consultaId(saved.getId())
                    .pacienteId(saved.getPacienteId())
                    .doctorId(saved.getDoctorId())
                    .examenesNombres(saved.getExamenesSolicitados())
                    .indicacionesClinicas(saved.getPlanTratamiento())
                    .fechaSolicitud(saved.getFechaConsulta().toString())
                    .build();
            examenKafkaProducer.emitirExamenSolicitado(event);
        }

        if (saved.getRecetas() != null && !saved.getRecetas().isEmpty()) {
            for (Receta receta : saved.getRecetas()) {
                com.clinic.consultas_service.event.RecetaEmitidaEvent recetaEvent = com.clinic.consultas_service.event.RecetaEmitidaEvent.builder()
                        .consultaId(saved.getId())
                        .pacienteId(saved.getPacienteId())
                        .doctorId(saved.getDoctorId())
                        .medicamento(receta.getMedicamento())
                        .dosis(receta.getDosis())
                        .frecuencia(receta.getFrecuencia())
                        .duracion(receta.getDuracion())
                        .cantidadDeducir(1) // 1 unidad / envase estándar
                        .indicaciones(receta.getIndicaciones())
                        .fechaEmision(saved.getFechaConsulta().toString())
                        .build();
                examenKafkaProducer.emitirRecetaEmitida(recetaEvent);
            }
        }

        return ConsultaResponseDTO.fromEntity(saved);
    }

    public List<ConsultaResponseDTO> listarConsultas() {
        return consultaRepository.findAll().stream()
                .map(ConsultaResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }

    public ConsultaResponseDTO obtenerPorId(Long id) {
        Consulta consulta = consultaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Consulta médica no encontrada con ID: " + id));
        return ConsultaResponseDTO.fromEntity(consulta);
    }

    public List<ConsultaResponseDTO> obtenerPorPaciente(Long pacienteId) {
        return consultaRepository.findByPacienteIdOrderByFechaConsultaDesc(pacienteId).stream()
                .map(ConsultaResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }

    public List<ConsultaResponseDTO> obtenerPorDoctor(Long doctorId) {
        return consultaRepository.findByDoctorIdOrderByFechaConsultaDesc(doctorId).stream()
                .map(ConsultaResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }
}
