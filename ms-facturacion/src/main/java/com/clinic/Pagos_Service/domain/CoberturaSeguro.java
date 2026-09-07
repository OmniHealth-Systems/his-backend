package com.clinic.Pagos_Service.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Cobertura de seguro médico aplicada a un comprobante.
 * Registra qué parte paga el seguro y qué paga el paciente (copago).
 */
@Entity
@Table(name = "cobertura_seguro")
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class CoberturaSeguro {
    public enum EstadoCobro { PENDIENTE_VERIFICACION, APROBADO, RECHAZADO, EN_PROCESO }

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "comprobante_id", nullable = false)
    private ComprobantePago comprobante;

    @Column(name = "aseguradora_id")
    private Long aseguradoraId;

    @Column(name = "nombre_aseguradora", nullable = false)
    private String nombreAseguradora;

    @Column(name = "numero_poliza", nullable = false)
    private String numeroPoliza;

    @Column(name = "monto_cubierto", nullable = false, precision = 10, scale = 2)
    private BigDecimal montoCubierto;

    @Column(name = "copago_paciente", nullable = false, precision = 10, scale = 2)
    private BigDecimal copagoPaciente;

    @Column(name = "porcentaje_cobertura", precision = 5, scale = 2)
    private BigDecimal porcentajeCobertura;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado_cobro", nullable = false)
    private EstadoCobro estadoCobro;

    @Column(name = "fecha_vencimiento_poliza")
    private LocalDate fechaVencimientoPoliza;

    @CreationTimestamp
    private LocalDateTime fechaRegistro;
}
