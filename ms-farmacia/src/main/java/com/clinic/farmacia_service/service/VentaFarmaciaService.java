package com.clinic.farmacia_service.service;

import com.clinic.farmacia_service.domain.DetalleVentaFarmacia;
import com.clinic.farmacia_service.domain.Medicamento;
import com.clinic.farmacia_service.domain.MovimientoInventario;
import com.clinic.farmacia_service.domain.VentaFarmacia;
import com.clinic.farmacia_service.repository.MedicamentoRepository;
import com.clinic.farmacia_service.repository.MovimientoInventarioRepository;
import com.clinic.farmacia_service.repository.VentaFarmaciaRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class VentaFarmaciaService {

    private final VentaFarmaciaRepository ventaRepository;
    private final MedicamentoRepository medicamentoRepository;
    private final MovimientoInventarioRepository movimientoRepository;

    public List<VentaFarmacia> listarVentas() {
        return ventaRepository.findAll();
    }

    public VentaFarmacia obtenerPorId(String id) {
        return ventaRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Venta no encontrada con ID: " + id));
    }

    @Transactional
    public VentaFarmacia procesarVentaPOS(VentaFarmacia venta, List<DetalleVentaFarmacia> items) {
        String numVenta = "POS-" + System.currentTimeMillis() % 10000000;
        venta.setNumeroVenta(numVenta);
        if (venta.getEstadoVenta() == null) {
            venta.setEstadoVenta(VentaFarmacia.EstadoVenta.COMPLETADA);
        }

        BigDecimal subtotal = BigDecimal.ZERO;
        List<DetalleVentaFarmacia> detallesProcesados = new ArrayList<>();

        for (DetalleVentaFarmacia item : items) {
            Medicamento med = medicamentoRepository.findById(item.getMedicamento().getId())
                    .orElseThrow(() -> new EntityNotFoundException("Medicamento no encontrado: " + item.getMedicamento().getId()));

            if (med.getStockActual() < item.getCantidad()) {
                throw new IllegalStateException("Stock insuficiente para '" + med.getNombre() + "'. Solicitado: " + item.getCantidad() + ", Disponible: " + med.getStockActual());
            }

            int stockPrevio = med.getStockActual();
            int stockPosterior = stockPrevio - item.getCantidad();
            med.setStockActual(stockPosterior);
            medicamentoRepository.save(med);

            // Registrar movimiento de auditoría
            MovimientoInventario mov = MovimientoInventario.builder()
                    .medicamento(med)
                    .tipo(MovimientoInventario.TipoMovimiento.VENTA_POS)
                    .cantidad(item.getCantidad())
                    .stockPrevio(stockPrevio)
                    .stockPosterior(stockPosterior)
                    .motivo("Venta POS #" + numVenta)
                    .fechaMovimiento(LocalDateTime.now())
                    .build();
            movimientoRepository.save(mov);

            BigDecimal lineTotal = item.getPrecioUnitario().multiply(BigDecimal.valueOf(item.getCantidad()));
            item.setSubtotal(lineTotal);
            item.setVenta(venta);
            detallesProcesados.add(item);

            subtotal = subtotal.add(lineTotal);
        }

        venta.setSubtotal(subtotal);
        BigDecimal desc = venta.getDescuento() != null ? venta.getDescuento() : BigDecimal.ZERO;
        venta.setTotal(subtotal.subtract(desc));
        venta.setDetalles(detallesProcesados);

        log.info("[ms-farmacia][POS] Venta directa {} registrada por un total de S/ {}", numVenta, venta.getTotal());
        return ventaRepository.save(venta);
    }
}
