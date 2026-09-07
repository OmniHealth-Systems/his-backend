package com.clinic.laboratorio_service.DTO;

import com.clinic.laboratorio_service.domain.OrdenLaboratorio;
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
public class OrdenLaboratorioResponseDTO {
    private Long id;
    private Long pacienteId;
    private Long doctorId;
    private Long consultaId;
    private LocalDateTime fechaOrden;
    private OrdenLaboratorio.EstadoOrden estado;
    private String indicacionesMuestra;
    private String observacionesGenerales;
    private Double costoTotal;
    private List<DetalleOrdenDTO> detalles;

    public static OrdenLaboratorioResponseDTO fromEntity(OrdenLaboratorio orden) {
        List<DetalleOrdenDTO> detallesDTO = orden.getDetalles() == null ? List.of() :
                orden.getDetalles().stream().map(d -> DetalleOrdenDTO.builder()
                        .id(d.getId())
                        .examenId(d.getExamen() != null ? d.getExamen().getId() : null)
                        .examenCodigo(d.getExamen() != null ? d.getExamen().getCodigo() : null)
                        .examenNombre(d.getExamen() != null ? d.getExamen().getNombre() : null)
                        .examenPrecio(d.getExamen() != null ? d.getExamen().getPrecio() : 0.0)
                        .resultado(d.getResultado())
                        .valoresReferencia(d.getValoresReferencia())
                        .observaciones(d.getObservaciones())
                        .fechaResultado(d.getFechaResultado())
                        .estado(d.getEstado())
                        .build()).collect(Collectors.toList());

        double total = detallesDTO.stream()
                .mapToDouble(d -> d.getExamenPrecio() != null ? d.getExamenPrecio() : 0.0)
                .sum();

        return OrdenLaboratorioResponseDTO.builder()
                .id(orden.getId())
                .pacienteId(orden.getPacienteId())
                .doctorId(orden.getDoctorId())
                .consultaId(orden.getConsultaId())
                .fechaOrden(orden.getFechaOrden())
                .estado(orden.getEstado())
                .indicacionesMuestra(orden.getIndicacionesMuestra())
                .observacionesGenerales(orden.getObservacionesGenerales())
                .costoTotal(Math.round(total * 100.0) / 100.0)
                .detalles(detallesDTO)
                .build();
    }
}
