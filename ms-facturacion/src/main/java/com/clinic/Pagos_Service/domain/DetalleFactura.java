package com.clinic.Pagos_Service.domain;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

/**
 * Línea de detalle de factura — desglose de servicios cobrados.
 * Permite calcular subtotales, IGV (18%) y descuentos por ítem.
 * NUEVA ENTIDAD que resuelve el atajo de "montoTotal en una sola columna".
 */
@Entity
@Table(name = "detalle_factura")
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class DetalleFactura {
    public enum TipoItem { CONSULTA_MEDICA, EXAMEN_LABORATORIO, MEDICAMENTO, PROCEDIMIENTO_QUIRURGICO, IMAGENOLOGIA, HOSPITALIZACION, OTRO }

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "comprobante_id", nullable = false)
    private ComprobantePago comprobante;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_item", nullable = false)
    private TipoItem tipoItem;

    @Column(name = "descripcion", nullable = false)
    private String descripcion;

    @Column(name = "codigo_item")
    private String codigoItem; // CIE-10, código de medicamento, etc.

    @Column(name = "cantidad", nullable = false)
    private Integer cantidad;

    @Column(name = "precio_unitario", nullable = false, precision = 10, scale = 2)
    private BigDecimal precioUnitario;

    @Column(name = "descuento_porcentaje", precision = 5, scale = 2)
    private BigDecimal descuentoPorcentaje;

    @Column(name = "subtotal", nullable = false, precision = 10, scale = 2)
    private BigDecimal subtotal;

    /** IGV 18% solo si es factura a empresa */
    @Column(name = "igv", precision = 10, scale = 2)
    private BigDecimal igv;

    @Column(name = "total_linea", nullable = false, precision = 10, scale = 2)
    private BigDecimal totalLinea;
}
