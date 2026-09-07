package com.clinic.laboratorio_service.service;

import com.clinic.laboratorio_service.DTO.*;
import com.clinic.laboratorio_service.domain.DetalleOrden;
import com.clinic.laboratorio_service.domain.ExamenCatalogo;
import com.clinic.laboratorio_service.domain.OrdenLaboratorio;
import com.clinic.laboratorio_service.repository.ExamenCatalogoRepository;
import com.clinic.laboratorio_service.repository.OrdenLaboratorioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LaboratorioService {

    private final ExamenCatalogoRepository examenRepository;
    private final OrdenLaboratorioRepository ordenRepository;

    // --- Catálogo de Exámenes ---
    @Transactional
    public ExamenCatalogoDTO crearExamenCatalogo(ExamenCatalogoDTO dto) {
        ExamenCatalogo examen = ExamenCatalogo.builder()
                .codigo(dto.getCodigo())
                .nombre(dto.getNombre())
                .categoria(dto.getCategoria())
                .precio(dto.getPrecio())
                .descripcion(dto.getDescripcion())
                .tiempoEntregaHoras(dto.getTiempoEntregaHoras())
                .valoresReferenciaDefault(dto.getValoresReferenciaDefault())
                .activo(dto.getActivo() != null ? dto.getActivo() : true)
                .build();

        ExamenCatalogo saved = examenRepository.save(examen);
        dto.setId(saved.getId());
        return dto;
    }

    public List<ExamenCatalogoDTO> listarCatalogo() {
        return examenRepository.findByActivoTrue().stream().map(e -> ExamenCatalogoDTO.builder()
                .id(e.getId())
                .codigo(e.getCodigo())
                .nombre(e.getNombre())
                .categoria(e.getCategoria())
                .precio(e.getPrecio())
                .descripcion(e.getDescripcion())
                .tiempoEntregaHoras(e.getTiempoEntregaHoras())
                .valoresReferenciaDefault(e.getValoresReferenciaDefault())
                .activo(e.getActivo())
                .build()).collect(Collectors.toList());
    }

    // --- Órdenes de Laboratorio ---
    @Transactional
    public OrdenLaboratorioResponseDTO crearOrden(OrdenLaboratorioRequestDTO request) {
        List<DetalleOrden> detalles = new ArrayList<>();
        for (Long examenId : request.getExamenesIds()) {
            ExamenCatalogo examen = examenRepository.findById(examenId)
                    .orElseThrow(() -> new RuntimeException("Examen de laboratorio no encontrado con ID: " + examenId));

            DetalleOrden detalle = DetalleOrden.builder()
                    .examen(examen)
                    .valoresReferencia(examen.getValoresReferenciaDefault())
                    .estado(DetalleOrden.EstadoDetalle.PENDIENTE)
                    .build();
            detalles.add(detalle);
        }

        OrdenLaboratorio orden = OrdenLaboratorio.builder()
                .pacienteId(request.getPacienteId())
                .doctorId(request.getDoctorId())
                .consultaId(request.getConsultaId())
                .fechaOrden(LocalDateTime.now())
                .estado(OrdenLaboratorio.EstadoOrden.PENDIENTE)
                .indicacionesMuestra(request.getIndicacionesMuestra())
                .observacionesGenerales(request.getObservacionesGenerales())
                .detalles(detalles)
                .build();

        OrdenLaboratorio saved = ordenRepository.save(orden);
        return OrdenLaboratorioResponseDTO.fromEntity(saved);
    }

    public List<OrdenLaboratorioResponseDTO> listarOrdenes() {
        return ordenRepository.findAll().stream()
                .map(OrdenLaboratorioResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }

    public List<OrdenLaboratorioResponseDTO> listarOrdenesPendientes() {
        return ordenRepository.findByEstadoOrderByFechaOrdenDesc(OrdenLaboratorio.EstadoOrden.PENDIENTE).stream()
                .map(OrdenLaboratorioResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }

    public OrdenLaboratorioResponseDTO obtenerPorId(Long id) {
        OrdenLaboratorio orden = ordenRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Orden de laboratorio no encontrada con ID: " + id));
        return OrdenLaboratorioResponseDTO.fromEntity(orden);
    }

    public List<OrdenLaboratorioResponseDTO> obtenerPorPaciente(Long pacienteId) {
        return ordenRepository.findByPacienteIdOrderByFechaOrdenDesc(pacienteId).stream()
                .map(OrdenLaboratorioResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }

    // --- Registro de Resultados ---
    @Transactional
    public OrdenLaboratorioResponseDTO registrarResultadoDetalle(Long ordenId, Long detalleId, ResultadoLaboratorioDTO resultadoDTO) {
        OrdenLaboratorio orden = ordenRepository.findById(ordenId)
                .orElseThrow(() -> new RuntimeException("Orden de laboratorio no encontrada con ID: " + ordenId));

        DetalleOrden detalle = orden.getDetalles().stream()
                .filter(d -> d.getId().equals(detalleId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Detalle de orden no encontrado con ID: " + detalleId));

        detalle.setResultado(resultadoDTO.getResultado());
        if (resultadoDTO.getValoresReferencia() != null) {
            detalle.setValoresReferencia(resultadoDTO.getValoresReferencia());
        }
        detalle.setObservaciones(resultadoDTO.getObservaciones());
        detalle.setFechaResultado(LocalDateTime.now());
        detalle.setEstado(DetalleOrden.EstadoDetalle.COMPLETADO);

        // Si todos los detalles están completados, marcar orden como RESULTADOS_LISTOS
        boolean todosCompletados = orden.getDetalles().stream()
                .allMatch(d -> d.getEstado() == DetalleOrden.EstadoDetalle.COMPLETADO);

        if (todosCompletados) {
            orden.setEstado(OrdenLaboratorio.EstadoOrden.RESULTADOS_LISTOS);
        } else {
            orden.setEstado(OrdenLaboratorio.EstadoOrden.EN_PROCESAMIENTO);
        }

        OrdenLaboratorio saved = ordenRepository.save(orden);
        return OrdenLaboratorioResponseDTO.fromEntity(saved);
    }

    @Transactional
    public void cambiarEstadoOrden(Long id, OrdenLaboratorio.EstadoOrden nuevoEstado) {
        OrdenLaboratorio orden = ordenRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Orden no encontrada"));
        orden.setEstado(nuevoEstado);
        ordenRepository.save(orden);
    }
}
