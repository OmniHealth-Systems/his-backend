package com.clinic.Informe_service.service;


import com.clinic.Informe_service.DTO.*;
import com.clinic.Informe_service.client.CitasClient;
import com.clinic.Informe_service.client.PacientesClient;
import com.clinic.Informe_service.client.PersonalClient;
import com.clinic.Informe_service.domain.Informe;
import com.clinic.Informe_service.repository.InformeRepository;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
@Slf4j
public class InformeServiceImpl implements InformeService {

    private final InformeRepository informeRepository;
    private final CitasClient citaClient;
    private final PacientesClient pacienteClient;
    private final PersonalClient personalClient;
    private final JavaMailSender mailSender;

    @Override
    @Transactional
    public InformeDTO crearInforme(InformeDTO informeDTO) {
        // Validar cita
        CitaDTO cita = citaClient.getCitaById(informeDTO.getIdCita());
        if (cita == null || cita.getIdCita() == null) {
            throw new RuntimeException("Cita no encontrada");
        }

        Informe informe = mapToEntity(informeDTO);
        informe.setFechaEmision(java.time.LocalDateTime.now());
        informe.setEstado("PENDIENTE");

        informe = informeRepository.save(informe);
        return mapToDTO(informe);
    }

    @Override
    @Transactional
    public InformeDTO actualizarInforme(Long id, InformeDTO informeDTO) {
        Informe informe = informeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Informe no encontrado"));

        informe.setDiagnostico(informeDTO.getDiagnostico());
        informe.setTratamiento(informeDTO.getTratamiento());
        informe.setObservaciones(informeDTO.getObservaciones());
        informe.setEstado(informeDTO.getEstado());

        informe = informeRepository.save(informe);
        return mapToDTO(informe);
    }

    @Override
    public InformeDTO obtenerInformePorId(Long id) {
        Informe informe = informeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Informe no encontrado"));
        return mapToDTO(informe);
    }

    @Override
    public InformeCompletoDTO obtenerInformeCompletoPorId(Long id) {
        Informe informe = informeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Informe no encontrado"));

        // Obtener datos de otros servicios
        CitaDTO cita = citaClient.getCitaById(informe.getIdCita());
        PacienteDTO paciente = pacienteClient.getPacienteById(informe.getIdPaciente());
        DoctorDTO doctor = personalClient.getDoctorById(informe.getIdDoctor());

        return InformeCompletoDTO.builder()
                .idInforme(informe.getIdInforme())
                .fechaEmision(informe.getFechaEmision())
                .tipoInforme(informe.getTipoInforme())
                .diagnostico(informe.getDiagnostico())
                .tratamiento(informe.getTratamiento())
                .observaciones(informe.getObservaciones())
                .estado(informe.getEstado())
                .cita(cita)
                .paciente(paciente)
                .doctor(doctor)
                .build();
    }

    @Override
    public ResponseEntity<Resource> generarPdf(Long id) {
        InformeCompletoDTO informe = obtenerInformeCompletoPorId(id);
        byte[] pdfBytes = generarPdfInforme(informe);

        ByteArrayResource resource = new ByteArrayResource(pdfBytes);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=informe_medico_" + id + ".pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .contentLength(pdfBytes.length)
                .body(resource);
    }

    @Override
    public void enviarPorEmail(Long id, String destinatario) {
        InformeCompletoDTO informe = obtenerInformeCompletoPorId(id);
        byte[] pdfBytes = generarPdfInforme(informe);

        String asunto = "Informe Médico - " + informe.getPaciente().getNombreCompleto();
        String cuerpo = "Adjunto encontrará el informe médico de la consulta realizada el "
                + informe.getFechaEmision().format(DateTimeFormatter.ISO_DATE);

        try {
            if (mailSender == null) {
                throw new IllegalStateException("JavaMailSender no configurado");
            }

            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setTo(destinatario);
            helper.setSubject(asunto);
            helper.setText(cuerpo);

            helper.addAttachment("informe_medico_" + id + ".pdf", new ByteArrayResource(pdfBytes));
            mailSender.send(message);

        } catch (MessagingException e) {
            log.error("Error al enviar correo", e);
            throw new RuntimeException("Error al enviar el correo electrónico");
        }
    }

    private byte[] generarPdfInforme(InformeCompletoDTO informe) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        Document document = new Document();

        try {
            PdfWriter.getInstance(document, outputStream);
            document.open();

            // Fuentes
            Font titleFont = new Font(Font.FontFamily.HELVETICA, 18, Font.BOLD);
            Font headerFont = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD);
            Font normalFont = new Font(Font.FontFamily.HELVETICA, 10);

            // Título
            Paragraph title = new Paragraph("INFORME MÉDICO", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            title.setSpacingAfter(20f);
            document.add(title);

            // Información básica
            PdfPTable headerTable = new PdfPTable(2);
            headerTable.setWidthPercentage(100);
            headerTable.setSpacingAfter(20f);

            addHeaderCell(headerTable, "Número de Informe", headerFont);
            addCell(headerTable, informe.getIdInforme().toString(), normalFont);
            addHeaderCell(headerTable, "Fecha de Emisión", headerFont);
            addCell(headerTable, informe.getFechaEmision().format(DateTimeFormatter.ISO_DATE), normalFont);
            addHeaderCell(headerTable, "Paciente", headerFont);
            addCell(headerTable, informe.getPaciente().getNombreCompleto(), normalFont);
            addHeaderCell(headerTable, "Médico", headerFont);
            addCell(headerTable, informe.getDoctor().getNombreCompleto(), normalFont);
            addHeaderCell(headerTable, "Especialidad", headerFont);
            addCell(headerTable, informe.getCita().getEspecialidad(), normalFont);

            document.add(headerTable);

            // Diagnóstico
            Paragraph diagnosticoHeader = new Paragraph("Diagnóstico:", headerFont);
            diagnosticoHeader.setSpacingBefore(10f);
            document.add(diagnosticoHeader);

            Paragraph diagnostico = new Paragraph(informe.getDiagnostico(), normalFont);
            diagnostico.setSpacingAfter(15f);
            document.add(diagnostico);

            // Tratamiento
            Paragraph tratamientoHeader = new Paragraph("Tratamiento Recomendado:", headerFont);
            document.add(tratamientoHeader);

            Paragraph tratamiento = new Paragraph(informe.getTratamiento(), normalFont);
            tratamiento.setSpacingAfter(15f);
            document.add(tratamiento);

            // Observaciones
            Paragraph observacionesHeader = new Paragraph("Observaciones:", headerFont);
            document.add(observacionesHeader);

            Paragraph observaciones = new Paragraph(informe.getObservaciones(), normalFont);
            document.add(observaciones);

            document.close();
            return outputStream.toByteArray();
        } catch (DocumentException e) {
            log.error("Error al generar PDF", e);
            throw new RuntimeException("Error al generar el PDF del informe");
        } finally {
            try {
                if (document.isOpen()) {
                    document.close();
                }
                outputStream.close();
            } catch (IOException e) {
                log.warn("Error al cerrar recursos", e);
            }
        }
    }

    private void addHeaderCell(PdfPTable table, String text, Font font) {
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setBackgroundColor(new BaseColor(220, 220, 220));
        table.addCell(cell);
    }

    private void addCell(PdfPTable table, String text, Font font) {
        table.addCell(new Phrase(text, font));
    }

    private Informe mapToEntity(InformeDTO dto) {
        return Informe.builder()
                .idCita(dto.getIdCita())
                .idPaciente(dto.getIdPaciente())
                .idDoctor(dto.getIdDoctor())
                .fechaEmision(dto.getFechaEmision())
                .tipoInforme(dto.getTipoInforme())
                .diagnostico(dto.getDiagnostico())
                .tratamiento(dto.getTratamiento())
                .observaciones(dto.getObservaciones())
                .estado(dto.getEstado())
                .build();
    }

    private InformeDTO mapToDTO(Informe entity) {
        return InformeDTO.builder()
                .idInforme(entity.getIdInforme())
                .idCita(entity.getIdCita())
                .idPaciente(entity.getIdPaciente())
                .idDoctor(entity.getIdDoctor())
                .fechaEmision(entity.getFechaEmision())
                .tipoInforme(entity.getTipoInforme())
                .diagnostico(entity.getDiagnostico())
                .tratamiento(entity.getTratamiento())
                .observaciones(entity.getObservaciones())
                .estado(entity.getEstado())
                .build();
    }
}