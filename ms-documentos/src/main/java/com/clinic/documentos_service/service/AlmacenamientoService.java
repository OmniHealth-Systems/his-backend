package com.clinic.documentos_service.service;

import com.clinic.documentos_service.domain.Documento;
import com.clinic.documentos_service.repository.DocumentoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;

import java.io.IOException;
import java.time.Duration;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class AlmacenamientoService {

    private final S3Client s3Client;
    private final S3Presigner s3Presigner;
    private final DocumentoRepository documentoRepository;

    @Value("${storage.s3.bucket}")
    private String bucketName;

    @Value("${storage.s3.endpoint}")
    private String endpoint;

    /**
     * Sube un archivo a S3 y persiste sus metadatos en documentos_db.
     * Centraliza el acceso S3 para todo el ecosistema OmniHIS Cloud.
     */
    public Documento subirDocumento(MultipartFile file, Documento.TipoDocumento tipo,
                                     String entidadOrigen, String entidadId, Long pacienteId) throws IOException {
        String s3Key = "omnihis/" + entidadOrigen + "/" + entidadId + "/" + UUID.randomUUID() + "_" + file.getOriginalFilename();

        PutObjectRequest putRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(s3Key)
                .contentType(file.getContentType())
                .build();

        s3Client.putObject(putRequest, RequestBody.fromInputStream(file.getInputStream(), file.getSize()));

        String s3Url = endpoint + "/" + bucketName + "/" + s3Key;

        Documento documento = Documento.builder()
                .tipoDocumento(tipo)
                .nombreOriginal(file.getOriginalFilename())
                .s3Key(s3Key)
                .s3Url(s3Url)
                .contentType(file.getContentType())
                .tamanoBytes(file.getSize())
                .entidadOrigen(entidadOrigen)
                .entidadId(entidadId)
                .pacienteId(pacienteId)
                .estado(Documento.EstadoDocumento.ACTIVO)
                .build();

        log.info("[ms-documentos] Archivo '{}' subido a S3 con clave: {}", file.getOriginalFilename(), s3Key);
        return documentoRepository.save(documento);
    }

    /** Genera una URL pre-firmada de acceso temporal (15 minutos) */
    public String generarPresignedUrl(String documentoId) {
        Documento doc = documentoRepository.findById(documentoId)
                .orElseThrow(() -> new jakarta.persistence.EntityNotFoundException("Documento no encontrado: " + documentoId));

        GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
                .signatureDuration(Duration.ofMinutes(15))
                .getObjectRequest(b -> b.bucket(bucketName).key(doc.getS3Key()))
                .build();

        return s3Presigner.presignGetObject(presignRequest).url().toString();
    }
}
