package com.clinic.logistica_service.service;

import com.clinic.logistica_service.domain.InsumoHospitalario;
import com.clinic.logistica_service.domain.MovimientoStock;
import com.clinic.logistica_service.repository.InsumoRepository;
import com.clinic.logistica_service.repository.MovimientoStockRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class LogisticaService {

    private final InsumoRepository insumoRepository;
    private final MovimientoStockRepository movimientoRepository;

    public List<InsumoHospitalario> listarInsumos() {
        return insumoRepository.findAll();
    }

    public List<InsumoHospitalario> listarPorSede(Long sedeId) {
        return insumoRepository.findBySedeId(sedeId);
    }

    public List<InsumoHospitalario> listarAlertasStockMinimo() {
        return insumoRepository.findByStockActualLessThanEqual(10);
    }

    public InsumoHospitalario guardarInsumo(InsumoHospitalario insumo) {
        insumo.setFechaUltimaActualizacion(LocalDateTime.now());
        if (insumo.getEstado() == null) {
            insumo.setEstado(InsumoHospitalario.EstadoInsumo.ACTIVO);
        }
        return insumoRepository.save(insumo);
    }

    @Transactional
    public InsumoHospitalario registrarMovimiento(Long insumoId, int cantidad, MovimientoStock.TipoMovimiento tipo,
                                                  String motivo, Long sedeId, Long usuarioId) {
        InsumoHospitalario insumo = insumoRepository.findById(insumoId)
                .orElseThrow(() -> new EntityNotFoundException("Insumo no encontrado: " + insumoId));

        int stockPrevio = insumo.getStockActual();
        int stockPosterior = tipo == MovimientoStock.TipoMovimiento.ENTRADA || tipo == MovimientoStock.TipoMovimiento.COMPRA
                ? stockPrevio + cantidad
                : stockPrevio - cantidad;

        if (stockPosterior < 0) {
            throw new IllegalStateException("Stock insuficiente para realizar la salida del insumo.");
        }

        insumo.setStockActual(stockPosterior);
        insumo.setFechaUltimaActualizacion(LocalDateTime.now());
        if (stockPosterior == 0) {
            insumo.setEstado(InsumoHospitalario.EstadoInsumo.AGOTADO);
        }
        insumoRepository.save(insumo);

        MovimientoStock mov = MovimientoStock.builder()
                .insumo(insumo)
                .tipo(tipo)
                .cantidad(cantidad)
                .stockPrevio(stockPrevio)
                .stockPosterior(stockPosterior)
                .motivo(motivo)
                .sedeId(sedeId)
                .usuarioId(usuarioId)
                .fechaMovimiento(LocalDateTime.now())
                .build();
        movimientoRepository.save(mov);

        return insumo;
    }
}
