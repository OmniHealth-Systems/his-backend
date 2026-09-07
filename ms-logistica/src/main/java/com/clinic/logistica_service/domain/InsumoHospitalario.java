package com.clinic.logistica_service.domain;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/** Inventario hospitalario general (excluyendo medicamentos, que son de ms-farmacia) */
@Entity
@Table(name = "insumos_hospitalarios")
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class InsumoHospitalario {
    public enum CategoriaInsumo {
        MATERIAL_QUIRURGICO, EQUIPAMIENTO_BIOMEDICO, MOBILIARIO_CLINICO,
        REACTIVO_LABORATORIO, INSUMO_IMAGEN, EPP, LIMPIEZA, OTRO
    }
    public enum EstadoInsumo { ACTIVO, AGOTADO, DESCONTINUADO, EN_REVISION }

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nombre;

    @Column(columnDefinition = "TEXT")
    private String descripcion;

    @Enumerated(EnumType.STRING)
    @Column(name = "categoria", nullable = false)
    private CategoriaInsumo categoria;

    @Column(name = "codigo_barras", unique = true)
    private String codigoBarras;

    @Column(name = "stock_actual", nullable = false)
    private Integer stockActual;

    @Column(name = "stock_minimo", nullable = false)
    private Integer stockMinimo;

    @Column(name = "unidad_medida", nullable = false)
    private String unidadMedida;

    @Column(name = "precio_unitario", precision = 10, scale = 2)
    private BigDecimal precioUnitario;

    @Column(name = "sede_id")
    private Long sedeId;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false)
    private EstadoInsumo estado;

    @Column(name = "fecha_ultima_actualizacion")
    private LocalDateTime fechaUltimaActualizacion;
}
