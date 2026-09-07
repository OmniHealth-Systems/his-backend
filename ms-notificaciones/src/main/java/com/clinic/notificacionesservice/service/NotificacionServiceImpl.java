package com.clinic.notificacionesservice.service;

import com.clinic.notificacionesservice.DTO.NotificacionDTO;
import com.clinic.notificacionesservice.domain.Canal;
import com.clinic.notificacionesservice.domain.Notificacion;
import com.clinic.notificacionesservice.domain.NotificacionLog;
import com.clinic.notificacionesservice.domain.enums.EstadoNotificacion;
import com.clinic.notificacionesservice.domain.enums.ResultadoEnvio;
import com.clinic.notificacionesservice.domain.enums.TipoCanal;
import com.clinic.notificacionesservice.exception.CanalDeshabilitadoException;
import com.clinic.notificacionesservice.exception.CanalNoSoportadoException;
import com.clinic.notificacionesservice.repository.CanalRepository;
import com.clinic.notificacionesservice.repository.LogNotificacionRepository;
import com.clinic.notificacionesservice.repository.NotificacionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class NotificacionServiceImpl implements NotificacionService {

    private final NotificacionRepository notificacionRepository;
    private final CanalRepository canalRepository;
    private final EmailService emailService;
    private final LogNotificacionRepository logRepository;

    @Override
    public Notificacion crearNotificacion(NotificacionDTO dto) {
        Canal canal = canalRepository.findByTipo(TipoCanal.valueOf(dto.getCanal()))
                .orElseThrow(() -> new CanalNoSoportadoException("Canal no soportado"));

        if(!canal.isHabilitado()) {
            throw new CanalDeshabilitadoException("Canal deshabilitado");
        }

        Notificacion notificacion = new Notificacion();
        // Mapear dto a entidad
        return notificacionRepository.save(notificacion);
    }

    @Override
    public void enviarNotificacion(Notificacion notificacion) {
        try {
            if("email".equalsIgnoreCase(notificacion.getCanal())) {
                emailService.enviarEmail(notificacion);
            }

            notificacion.setEstado(EstadoNotificacion.ENVIADA);
            registrarLog(notificacion, ResultadoEnvio.EXITO, null);
        } catch (Exception e) {
            notificacion.setEstado(EstadoNotificacion.FALLIDA);
            registrarLog(notificacion, ResultadoEnvio.ERROR, e.getMessage());
        } finally {
            notificacionRepository.save(notificacion);
        }
    }

    private void registrarLog(Notificacion notificacion, ResultadoEnvio resultado, String error) {
        NotificacionLog log = new NotificacionLog();
        log.setNotificacion(notificacion);
        log.setResultado(resultado);
        log.setMensajeError(error);
        log.setFecha(LocalDateTime.now());
        logRepository.save(log);
    }
}