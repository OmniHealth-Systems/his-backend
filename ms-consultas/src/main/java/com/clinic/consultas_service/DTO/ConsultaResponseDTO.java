package com.clinic.consultas_service.DTO;

import com.clinic.consultas_service.domain.Consulta;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConsultaResponseDTO {
    private Long id;
    private Long citaId;
    private Long pacienteId;
    private Long doctorId;
    private LocalDateTime fechaConsulta;
    private String motivoConsulta;
    private String anamnesis;
    private String examenFisico;
    private SignosVitalesDTO signosVitales;
    private List<DiagnosticoDTO> diagnosticos;
    private List<RecetaDTO> recetas;
    private String planTratamiento;
    private Boolean requiereExamenLaboratorio;
    private String examenesSolicitados;
    private Consulta.EstadoConsulta estado;

    public static ConsultaResponseDTO fromEntity(Consulta consulta) {
        SignosVitalesDTO svDTO = null;
        if (consulta.getSignosVitales() != null) {
            svDTO = SignosVitalesDTO.builder()
                    .presionArterial(consulta.getSignosVitales().getPresionArterial())
                    .frecuenciaCardiaca(consulta.getSignosVitales().getFrecuenciaCardiaca())
                    .temperatura(consulta.getSignosVitales().getTemperatura())
                    .peso(consulta.getSignosVitales().getPeso())
                    .talla(consulta.getSignosVitales().getTalla())
                    .imc(consulta.getSignosVitales().getImc())
                    .saturacionOxigeno(consulta.getSignosVitales().getSaturacionOxigeno())
                    .build();
        }

        List<DiagnosticoDTO> diagDTOs = consulta.getDiagnosticos() == null ? List.of() :
                consulta.getDiagnosticos().stream().map(d -> DiagnosticoDTO.builder()
                        .id(d.getId())
                        .codigoCie10(d.getCodigoCie10())
                        .descripcion(d.getDescripcion())
                        .tipo(d.getTipo())
                        .build()).collect(Collectors.toList());

        List<RecetaDTO> recetaDTOs = consulta.getRecetas() == null ? List.of() :
                consulta.getRecetas().stream().map(r -> RecetaDTO.builder()
                        .id(r.getId())
                        .medicamento(r.getMedicamento())
                        .dosis(r.getDosis())
                        .frecuencia(r.getFrecuencia())
                        .duracion(r.getDuracion())
                        .indicaciones(r.getIndicaciones())
                        .build()).collect(Collectors.toList());

        return ConsultaResponseDTO.builder()
                .id(consulta.getId())
                .citaId(consulta.getCitaId())
                .pacienteId(consulta.getPacienteId())
                .doctorId(consulta.getDoctorId())
                .fechaConsulta(consulta.getFechaConsulta())
                .motivoConsulta(consulta.getMotivoConsulta())
                .anamnesis(consulta.getAnamnesis())
                .examenFisico(consulta.getExamenFisico())
                .signosVitales(svDTO)
                .diagnosticos(diagDTOs)
                .recetas(recetaDTOs)
                .planTratamiento(consulta.getPlanTratamiento())
                .requiereExamenLaboratorio(consulta.getRequiereExamenLaboratorio())
                .examenesSolicitados(consulta.getExamenesSolicitados())
                .estado(consulta.getEstado())
                .build();
    }
}
