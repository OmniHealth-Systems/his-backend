package com.clinic.documentos_service.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

/**
 * Metadatos de cada archivo almacenado en AWS S3.
 * Centraliza la gestión documental para todo el ecosistema:
 *  - Historiales clínicos (extraído de ms-historial)
 *  - PDFs de facturas (ms-facturacion)
 *  - Resultados de laboratorio (ms-laboratorio)
 *  - Imágenes de consulta (ms-consultas)
 */
@Entity
@Table(name = "documentos")
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class Documento {
    public enum TipoDocumento {
        ARCHIVO_HISTORIAL, PDF_FACTURA, RESULTADO_LABORATORIO,
        IMAGEN_CONSULTA, CONSENTIMIENTO_INFORMADO, RECETA_DIGITAL,
        IMAGEN_RADIOLOGICA, DOCUMENTO_IDENTIDAD, OTRO
    }
    public enum EstadoDocumento { ACTIVO, ELIMINADO, ARCHIVADO }

    @Id @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_documento", nullable = false)
    private TipoDocumento tipoDocumento;

    @Column(name = "nombre_original", nullable = false)
    private String nombreOriginal;

    @Column(name = "s3_key", nullable = false, unique = true)
    private String s3Key;

    @Column(name = "s3_url", nullable = false, columnDefinition = "TEXT")
    private String s3Url;

    @Column(name = "content_type", nullable = false)
    private String contentType;

    @Column(name = "tamano_bytes")
    private Long tamanoBytes;

    /** Referencia polimorfica cross-service — no FK física */
    @Column(name = "entidad_origen")
    private String entidadOrigen; // ej: "ms-historial", "ms-facturacion"

    @Column(name = "entidad_id")
    private String entidadId; // ID del recurso relacionado en el MS origen

    @Column(name = "paciente_id")
    private Long pacienteId;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false)
    private EstadoDocumento estado;

    @CreationTimestamp
    private LocalDateTime fechaSubida;
}
