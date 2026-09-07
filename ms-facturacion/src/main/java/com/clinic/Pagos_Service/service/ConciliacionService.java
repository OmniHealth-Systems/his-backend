package com.clinic.Pagos_Service.service;

import com.clinic.Pagos_Service.domain.EstadoPago;
import com.clinic.Pagos_Service.domain.Pago;
import com.clinic.Pagos_Service.domain.Transaccion;
import com.clinic.Pagos_Service.repository.PagoRepository;
import com.clinic.Pagos_Service.repository.TransaccionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ConciliacionService {

    private final PagoRepository pagoRepository;
    private final TransaccionRepository transaccionRepository;

    @Scheduled(cron = "0 0 2 * * ?") // Ejecutar diario a las 2 AM
    public void conciliacionDiaria() {
        LocalDate fecha = LocalDate.now().minusDays(1);
        LocalDateTime inicio = fecha.atStartOfDay();
        LocalDateTime fin = fecha.plusDays(1).atStartOfDay();

        List<Pago> pagos = pagoRepository.findByFechaCreacionBetween(inicio, fin);

        BigDecimal totalSistema = pagos.stream()
                .filter(p -> p.getEstado() == EstadoPago.COMPLETADO)
                .map(Pago::getMonto)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // En una implementación real, aquí se obtendría el total de la pasarela
        BigDecimal totalPasarela = obtenerTotalPasarela(fecha);

        if (totalSistema.compareTo(totalPasarela) != 0) {
            generarReporteDiscrepancia(totalSistema, totalPasarela, fecha);
        }

        // Verificar pagos pendientes
        pagos.stream()
                .filter(p -> p.getEstado() == EstadoPago.PENDIENTE)
                .forEach(this::verificarEstadoPago);
    }

    public void registrarReembolso(UUID pagoId, BigDecimal monto) {
        // Registrar reembolso en el sistema de conciliación
        Transaccion transaccion = Transaccion.builder()
                .pagoId(pagoId)
                .accion("REEMBOLSO_REGISTRADO")
                .mensaje("Reembolso conciliado por valor de " + monto)
                .build();

        transaccionRepository.save(transaccion);
    }

    private BigDecimal obtenerTotalPasarela(LocalDate fecha) {
        // Simulación: en producción se integraría con API de la pasarela
        return BigDecimal.valueOf(10000); // Valor simulado
    }

    private void generarReporteDiscrepancia(BigDecimal totalSistema, BigDecimal totalPasarela, LocalDate fecha) {
        // Lógica para generar reporte y notificar
        System.err.println("ALERTA: Discrepancia en conciliación " + fecha);
        System.err.println("Sistema: " + totalSistema + " | Pasarela: " + totalPasarela);
    }

    private void verificarEstadoPago(Pago pago) {
        // En producción: verificar estado real en la pasarela
        if (pago.getFechaCreacion().isBefore(LocalDateTime.now().minusDays(1))) {
            pago.setEstado(EstadoPago.CANCELADO);
            pagoRepository.save(pago);
            registrarTransaccion(pago, "PAGO_CANCELADO", "Pago pendiente expirado");
        }
    }

    private void registrarTransaccion(Pago pago, String accion, String mensaje) {
        Transaccion transaccion = Transaccion.builder()
                .pagoId(pago.getId())
                .accion(accion)
                .mensaje(mensaje)
                .build();

        transaccionRepository.save(transaccion);
    }
}