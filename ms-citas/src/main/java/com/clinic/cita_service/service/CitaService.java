package com.clinic.cita_service.service;

import com.clinic.cita_service.DTO.CitaRequestDTO;
import com.clinic.cita_service.DTO.CitaResponseDTO;
import com.clinic.cita_service.client.FranquiciasClient;
import com.clinic.cita_service.client.PacientesClient;
import com.clinic.cita_service.client.PersonalClient;
import com.clinic.cita_service.domain.Cita;
import com.clinic.cita_service.domain.ExcepcionHorario;
import com.clinic.cita_service.domain.TipoCita;
import com.clinic.cita_service.exception.CitaConflictException;
import com.clinic.cita_service.exception.ResourceNotFoundException;
import com.clinic.cita_service.exception.ServiceUnavailableException;
import com.clinic.cita_service.repository.CitaRepository;
import com.clinic.cita_service.repository.ExcepcionHorarioRepository;
import com.clinic.cita_service.repository.TipoCitaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
public class CitaService {

    // Constantes para horario laboral
    private static final LocalTime HORA_INICIO_JORNADA = LocalTime.of(8, 0);
    private static final LocalTime HORA_FIN_JORNADA = LocalTime.of(18, 0);
    private static final int MINUTOS_INTERVALO = 15; // Intervalo mínimo entre citas

    private final CitaRepository citaRepository;
    private final TipoCitaRepository tipoCitaRepository;
    private final ExcepcionHorarioRepository excepcionHorarioRepository;
    private final PacientesClient pacientesClient;
    private final PersonalClient personalClient;
    private final FranquiciasClient franquiciasClient;

    @Transactional
    public CitaResponseDTO crearCita(CitaRequestDTO citaRequest) {
        // Validar aspectos básicos de la solicitud
        validarSolicitudCita(citaRequest);

        // Validar existencia de paciente
        if (!pacientesClient.checkPacienteExiste(citaRequest.getPacienteId())) {
            throw new ResourceNotFoundException("Paciente con ID " + citaRequest.getPacienteId() + " no encontrado");
        }

        // Validar disponibilidad de doctor
        if (!personalClient.checkDoctorDisponible(citaRequest.getDoctorId())) {
            throw new ServiceUnavailableException("Doctor con ID " + citaRequest.getDoctorId() + " no disponible");
        }

        // Validar disponibilidad de sede
        if (!franquiciasClient.checkSedeDisponible(citaRequest.getSedeId())) {
            throw new ServiceUnavailableException("Sede con ID " + citaRequest.getSedeId() + " no disponible");
        }

        // Verificar excepciones de horario
        List<ExcepcionHorario> excepciones = excepcionHorarioRepository
                .findByDoctorIdAndFecha(citaRequest.getDoctorId(), citaRequest.getFechaCita());

        if (!excepciones.isEmpty()) {
            ExcepcionHorario excepcion = excepciones.get(0);
            String mensaje = "El doctor tiene una excepción de horario (" +
                    (excepcion.getTodoElDia() ? "todo el día" : "parcial") +
                    ") el " + citaRequest.getFechaCita() +
                    ": " + excepcion.getMotivo();
            throw new CitaConflictException(mensaje);
        }

        // Verificar conflictos de horario
        LocalTime horaFin = citaRequest.getHoraCita()
                .plusMinutes(citaRequest.getDuracionMinutos());

        List<Cita> citasConflictivas = citaRepository.findConflictingCitas(
                citaRequest.getDoctorId(),
                citaRequest.getFechaCita(),
                citaRequest.getHoraCita(),
                horaFin
        );

        if (!citasConflictivas.isEmpty()) {
            Cita conflicto = citasConflictivas.get(0);
            String mensaje = "Conflicto con cita existente #" + conflicto.getId() +
                    " del paciente " + conflicto.getPacienteId() +
                    " a las " + conflicto.getHoraCita();
            throw new CitaConflictException(mensaje);
        }

        // Obtener tipo de cita
        TipoCita tipoCita = tipoCitaRepository.findById(citaRequest.getTipoCitaId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Tipo de cita con ID " + citaRequest.getTipoCitaId() + " no encontrado"
                ));

        // Validar que el tipo de cita esté activo
        if (!tipoCita.getActivo()) {
            throw new CitaConflictException("El tipo de cita '" + tipoCita.getNombre() + "' no está disponible");
        }

        // Crear y guardar cita
        Cita cita = new Cita();
        cita.setPacienteId(citaRequest.getPacienteId());
        cita.setDoctorId(citaRequest.getDoctorId());
        cita.setSedeId(citaRequest.getSedeId());
        cita.setFechaCita(citaRequest.getFechaCita());
        cita.setHoraCita(citaRequest.getHoraCita());
        cita.setDuracionMinutos(citaRequest.getDuracionMinutos());
        cita.setTipoCita(tipoCita);
        cita.setEstado(Cita.EstadoCita.PROGRAMADA);
        cita.setMotivo(citaRequest.getMotivo());

        Cita savedCita = citaRepository.save(cita);
        return CitaResponseDTO.fromEntity(savedCita);
    }

    /**
     * Valida los aspectos básicos de una solicitud de cita
     * @param request Solicitud de cita
     */
    private void validarSolicitudCita(CitaRequestDTO request) {
        // Validar que la fecha no sea en el pasado
        if (request.getFechaCita().isBefore(LocalDate.now())) {
            throw new CitaConflictException("No se pueden agendar citas en fechas pasadas");
        }

        // Validar que la fecha no sea hoy con hora pasada
        if (request.getFechaCita().isEqual(LocalDate.now()) &&
                request.getHoraCita().isBefore(LocalTime.now())) {
            throw new CitaConflictException("No se pueden agendar citas en horas pasadas");
        }

        // Validar duración positiva
        if (request.getDuracionMinutos() <= 0) {
            throw new CitaConflictException("La duración de la cita debe ser positiva");
        }

        // Validar horario laboral
        if (request.getHoraCita().isBefore(HORA_INICIO_JORNADA)) {
            throw new CitaConflictException("El horario laboral comienza a las " + HORA_INICIO_JORNADA);
        }

        LocalTime horaFin = request.getHoraCita().plusMinutes(request.getDuracionMinutos());
        if (horaFin.isAfter(HORA_FIN_JORNADA)) {
            throw new CitaConflictException("El horario laboral termina a las " + HORA_FIN_JORNADA);
        }

        // Validar que la hora termine antes del cierre
        long minutosAntesCierre = HORA_FIN_JORNADA.toSecondOfDay() - horaFin.toSecondOfDay();
        if (minutosAntesCierre < 0) {
            throw new CitaConflictException("La cita excede el horario de cierre por " +
                    (-minutosAntesCierre) + " minutos");
        }

        // Validar que la duración sea múltiplo del intervalo mínimo
        if (request.getDuracionMinutos() % MINUTOS_INTERVALO != 0) {
            throw new CitaConflictException("La duración debe ser múltiplo de " + MINUTOS_INTERVALO + " minutos");
        }
    }

    public CitaResponseDTO obtenerCitaPorId(Long id) {
        Cita cita = citaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cita con ID " + id + " no encontrada"));
        return CitaResponseDTO.fromEntity(cita);
    }

    public List<CitaResponseDTO> obtenerCitasPorPaciente(Long pacienteId) {
        List<Cita> citas = citaRepository.findByPacienteId(pacienteId);
        return citas.stream()
                .map(CitaResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional
    public void cancelarCita(Long id) {
        Cita cita = citaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cita con ID " + id + " no encontrada"));

        // Validar que la cita se pueda cancelar
        if (cita.getEstado() == Cita.EstadoCita.CANCELADA) {
            throw new CitaConflictException("La cita ya está cancelada");
        }

        if (cita.getEstado() == Cita.EstadoCita.COMPLETADA) {
            throw new CitaConflictException("No se puede cancelar una cita completada");
        }

        // Validar tiempo mínimo para cancelación (24 horas antes)
        LocalDate fechaCita = cita.getFechaCita();
        LocalTime horaCita = cita.getHoraCita();

        long horasAnticipacion = ChronoUnit.HOURS.between(
                LocalDate.now().atTime(LocalTime.now()),
                fechaCita.atTime(horaCita)
        );

        if (horasAnticipacion < 24) {
            throw new CitaConflictException("Las cancelaciones deben hacerse con al menos 24 horas de anticipación. " +
                    "Faltan " + horasAnticipacion + " horas");
        }

        cita.setEstado(Cita.EstadoCita.CANCELADA);
        citaRepository.save(cita);
    }

    /**
     * Obtiene horarios disponibles para un doctor en una fecha específica
     * @param doctorId ID del doctor
     * @param fecha Fecha a consultar
     * @return Lista de horarios disponibles
     */
    public List<String> obtenerHorariosDisponibles(Long doctorId, LocalDate fecha) {
        // 1. Verificar excepciones de horario
        List<ExcepcionHorario> excepciones = excepcionHorarioRepository
                .findByDoctorIdAndFecha(doctorId, fecha);

        if (!excepciones.isEmpty() && excepciones.get(0).getTodoElDia()) {
            // Todo el día ocupado
            return List.of();
        }

        // 2. Obtener citas existentes para ese día
        List<Cita> citasDelDia = citaRepository.findByDoctorIdAndFechaCita(doctorId, fecha);

        // 3. Generar horarios disponibles
        return generarIntervalosDisponibles(citasDelDia);
    }

    /**
     * Genera intervalos de tiempo disponibles basado en las citas existentes
     * @param citas Lista de citas existentes para el día
     * @return Lista de horarios disponibles formateados
     */
    private List<String> generarIntervalosDisponibles(List<Cita> citas) {
        return IntStream.iterate(0, i -> i + MINUTOS_INTERVALO)
                .mapToObj(i -> HORA_INICIO_JORNADA.plusMinutes(i))
                .takeWhile(hora -> hora.isBefore(HORA_FIN_JORNADA))
                .filter(hora -> estaDisponible(hora, citas))
                .map(LocalTime::toString)
                .collect(Collectors.toList());
    }

    /**
     * Verifica si un horario específico está disponible
     * @param hora Hora a verificar
     * @param citas Lista de citas existentes
     * @return true si el horario está disponible, false de lo contrario
     */
    private boolean estaDisponible(LocalTime hora, List<Cita> citas) {
        for (Cita cita : citas) {
            LocalTime inicioCita = cita.getHoraCita();
            LocalTime finCita = inicioCita.plusMinutes(cita.getDuracionMinutos());

            // Verificar si la hora está dentro del intervalo de la cita
            if ((hora.equals(inicioCita) ||
                    (hora.isAfter(inicioCita) && hora.isBefore(finCita)))) {
                return false;
            }

            // Verificar si el intervalo se superpone con la cita
            if (hora.isBefore(inicioCita) && hora.plusMinutes(MINUTOS_INTERVALO).isAfter(inicioCita)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Actualiza el estado de una cita
     * @param id ID de la cita
     * @param nuevoEstado Nuevo estado (CONFIRMADA, COMPLETADA, etc.)
     */
    @Transactional
    public void actualizarEstadoCita(Long id, Cita.EstadoCita nuevoEstado) {
        Cita cita = citaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cita no encontrada"));

        // Validar transiciones de estado válidas
        if (cita.getEstado() == Cita.EstadoCita.CANCELADA) {
            throw new CitaConflictException("No se puede modificar una cita cancelada");
        }

        if (nuevoEstado == Cita.EstadoCita.PROGRAMADA &&
                cita.getEstado() != Cita.EstadoCita.PROGRAMADA) {
            throw new CitaConflictException("No se puede volver al estado PROGRAMADA");
        }

        cita.setEstado(nuevoEstado);
        citaRepository.save(cita);
    }

    /**
     * Obtiene todas las citas de un doctor en una fecha específica
     * @param doctorId ID del doctor
     * @param fecha Fecha a consultar
     * @return Lista de citas del doctor en esa fecha
     */
    public List<CitaResponseDTO> obtenerCitasPorDoctorYFecha(Long doctorId, LocalDate fecha) {
        List<Cita> citas = citaRepository.findByDoctorIdAndFechaCita(doctorId, fecha);
        return citas.stream()
                .map(CitaResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * Crea una excepción de horario para un doctor
     * @param doctorId ID del doctor
     * @param franquiciaId ID de la franquicia
     * @param fecha Fecha de la excepción
     * @param motivo Motivo de la excepción
     * @param todoElDia Indica si es para todo el día
     * @return Excepción creada
     */
    @Transactional
    public ExcepcionHorario crearExcepcionHorario(
            Long doctorId,
            Long franquiciaId,
            LocalDate fecha,
            String motivo,
            boolean todoElDia) {

        // Validar que no exista ya una excepción para ese día
        List<ExcepcionHorario> existentes = excepcionHorarioRepository
                .findByDoctorIdAndFecha(doctorId, fecha);

        if (!existentes.isEmpty()) {
            throw new CitaConflictException("Ya existe una excepción para este doctor en esta fecha");
        }

        ExcepcionHorario excepcion = new ExcepcionHorario();
        excepcion.setDoctorId(doctorId);
        excepcion.setFranquiciaId(franquiciaId);
        excepcion.setFecha(fecha);
        excepcion.setMotivo(motivo);
        excepcion.setTodoElDia(todoElDia);

        return excepcionHorarioRepository.save(excepcion);
    }
}