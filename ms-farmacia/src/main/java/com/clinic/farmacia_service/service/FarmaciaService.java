package com.clinic.farmacia_service.service;

import com.clinic.farmacia_service.DTO.MedicamentoDTO;
import com.clinic.farmacia_service.DTO.MovimientoStockDTO;
import com.clinic.farmacia_service.domain.Medicamento;
import com.clinic.farmacia_service.domain.MovimientoInventario;
import com.clinic.farmacia_service.event.RecetaEmitidaEvent;
import com.clinic.farmacia_service.repository.MedicamentoRepository;
import com.clinic.farmacia_service.repository.MovimientoInventarioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class FarmaciaService {

    private final MedicamentoRepository medicamentoRepository;
    private final MovimientoInventarioRepository movimientoRepository;

    public List<MedicamentoDTO> listarMedicamentos() {
        return medicamentoRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public MedicamentoDTO obtenerPorId(Long id) {
        Medicamento m = medicamentoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Medicamento no encontrado con ID: " + id));
        return mapToDTO(m);
    }

    @Transactional
    public MedicamentoDTO crearMedicamento(MedicamentoDTO dto) {
        Medicamento m = Medicamento.builder()
                .codigo(dto.getCodigo())
                .nombre(dto.getNombre())
                .principioActivo(dto.getPrincipioActivo())
                .presentacion(dto.getPresentacion())
                .stockActual(dto.getStockActual())
                .stockMinimo(dto.getStockMinimo())
                .precioUnitario(dto.getPrecioUnitario())
                .lote(dto.getLote())
                .fechaVencimiento(dto.getFechaVencimiento())
                .activo(dto.getActivo() != null ? dto.getActivo() : true)
                .build();

        Medicamento saved = medicamentoRepository.save(m);

        // Registro de movimiento inicial si stock > 0
        if (saved.getStockActual() > 0) {
            MovimientoInventario mov = MovimientoInventario.builder()
                    .medicamento(saved)
                    .tipo(MovimientoInventario.TipoMovimiento.ENTRADA_COMPRA)
                    .cantidad(saved.getStockActual())
                    .stockPrevio(0)
                    .stockPosterior(saved.getStockActual())
                    .motivo("Stock inicial de inventario")
                    .fechaMovimiento(LocalDateTime.now())
                    .build();
            movimientoRepository.save(mov);
        }

        return mapToDTO(saved);
    }

    @Transactional
    public MedicamentoDTO registrarMovimiento(MovimientoStockDTO dto) {
        Medicamento m = medicamentoRepository.findById(dto.getMedicamentoId())
                .orElseThrow(() -> new RuntimeException("Medicamento no encontrado con ID: " + dto.getMedicamentoId()));

        int stockPrevio = m.getStockActual();
        int stockPosterior = stockPrevio;
        MovimientoInventario.TipoMovimiento tipoMov = MovimientoInventario.TipoMovimiento.valueOf(dto.getTipo());

        if (tipoMov == MovimientoInventario.TipoMovimiento.ENTRADA_COMPRA) {
            stockPosterior += dto.getCantidad();
        } else if (tipoMov == MovimientoInventario.TipoMovimiento.SALIDA_MANUAL || tipoMov == MovimientoInventario.TipoMovimiento.AJUSTE_INVENTARIO) {
            if (stockPrevio < dto.getCantidad()) {
                throw new RuntimeException("Stock insuficiente. Stock actual: " + stockPrevio);
            }
            stockPosterior -= dto.getCantidad();
        }

        m.setStockActual(stockPosterior);
        medicamentoRepository.save(m);

        MovimientoInventario mov = MovimientoInventario.builder()
                .medicamento(m)
                .tipo(tipoMov)
                .cantidad(dto.getCantidad())
                .stockPrevio(stockPrevio)
                .stockPosterior(stockPosterior)
                .motivo(dto.getMotivo())
                .fechaMovimiento(LocalDateTime.now())
                .build();
        movimientoRepository.save(mov);

        return mapToDTO(m);
    }

    /**
     * Deducción automática activada por evento Kafka (RecetaEmitidaEvent)
     */
    @Transactional
    public void deducirStockPorReceta(RecetaEmitidaEvent event) {
        log.info("Deduciendo stock para receta emitida: {} en consulta ID: {}", event.getMedicamento(), event.getConsultaId());

        // Buscar medicamento por coincidencia de nombre o código
        List<Medicamento> encontrados = medicamentoRepository
                .findByNombreContainingIgnoreCaseOrPrincipioActivoContainingIgnoreCase(event.getMedicamento(), event.getMedicamento());

        Medicamento target = null;
        if (!encontrados.isEmpty()) {
            target = encontrados.get(0);
        } else {
            // Si no existe, crear registro automático en catálogo de farmacia
            target = Medicamento.builder()
                    .codigo("MED-" + Math.abs(event.getMedicamento().hashCode() % 10000))
                    .nombre(event.getMedicamento())
                    .principioActivo(event.getMedicamento())
                    .presentacion(event.getDosis() != null ? event.getDosis() : "Estándar")
                    .stockActual(50) // Stock inicial de cortesía para simulación
                    .stockMinimo(10)
                    .precioUnitario(new java.math.BigDecimal("15.00"))
                    .activo(true)
                    .build();
            target = medicamentoRepository.save(target);
        }

        int cantidadADeducir = (event.getCantidadDeducir() != null && event.getCantidadDeducir() > 0) 
                ? event.getCantidadDeducir() : 1;

        int stockPrevio = target.getStockActual();
        int stockPosterior = Math.max(0, stockPrevio - cantidadADeducir);

        target.setStockActual(stockPosterior);
        medicamentoRepository.save(target);

        MovimientoInventario mov = MovimientoInventario.builder()
                .medicamento(target)
                .tipo(MovimientoInventario.TipoMovimiento.DEDUCCION_RECETA_KAFKA)
                .cantidad(cantidadADeducir)
                .stockPrevio(stockPrevio)
                .stockPosterior(stockPosterior)
                .motivo("Dispensación automática por Receta Médica - Consulta #" + event.getConsultaId() + " (" + event.getIndicaciones() + ")")
                .consultaId(event.getConsultaId())
                .fechaMovimiento(LocalDateTime.now())
                .build();

        movimientoRepository.save(mov);
        log.info("Stock deducido con éxito. Medicamento: {}, Stock Previo: {}, Stock Posterior: {}", 
                target.getNombre(), stockPrevio, stockPosterior);
    }

    private MedicamentoDTO mapToDTO(Medicamento m) {
        return MedicamentoDTO.builder()
                .id(m.getId())
                .codigo(m.getCodigo())
                .nombre(m.getNombre())
                .principioActivo(m.getPrincipioActivo())
                .presentacion(m.getPresentacion())
                .stockActual(m.getStockActual())
                .stockMinimo(m.getStockMinimo())
                .precioUnitario(m.getPrecioUnitario())
                .lote(m.getLote())
                .fechaVencimiento(m.getFechaVencimiento())
                .activo(m.getActivo())
                .build();
    }
}
