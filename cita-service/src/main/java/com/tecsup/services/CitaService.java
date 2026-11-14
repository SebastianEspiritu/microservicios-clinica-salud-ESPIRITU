package com.tecsup.services;

import com.tecsup.client.HorarioResponse;
import com.tecsup.client.MedicoClient;
import com.tecsup.client.MedicoResponse;
import com.tecsup.client.PacienteClient;
import com.tecsup.client.PacienteResponse;
import com.tecsup.dto.AtencionDTO;
import com.tecsup.dto.CitaDTO;
import com.tecsup.dto.CitaDetalleDTO;
import com.tecsup.exception.BusinessException;
import com.tecsup.exception.ResourceNotFoundException;
import com.tecsup.model.Atencion;
import com.tecsup.model.Cita;
import com.tecsup.model.EstadoCita;
import com.tecsup.repository.AtencionRepository;
import com.tecsup.repository.CitaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CitaService {

    @Autowired
    private CitaRepository repo;

    @Autowired
    private AtencionRepository atencionRepo;

    @Autowired
    private PacienteClient pacienteClient;

    @Autowired
    private MedicoClient medicoClient;

    // ----------------------------------------------------------- consultas

    public List<Cita> listar(Long idPaciente, Long idMedico, EstadoCita estado) {
        if (idPaciente != null) {
            return repo.findByIdPaciente(idPaciente);
        }
        if (idMedico != null) {
            return repo.findByIdMedico(idMedico);
        }
        if (estado != null) {
            return repo.findByEstado(estado);
        }
        return repo.findAll();
    }

    public Cita obtener(Long id) {
        return repo.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Cita no encontrada con id: " + id));
    }

    /** Une la cita con los datos del paciente y del médico (comunicación entre microservicios). */
    public CitaDetalleDTO detalle(Long id) {
        Cita cita = obtener(id);
        PacienteResponse paciente = pacienteClient.obtener(cita.getIdPaciente());
        MedicoResponse medico = medicoClient.obtener(cita.getIdMedico());
        return new CitaDetalleDTO(cita, paciente, medico);
    }

    // ----------------------------------------------------------- registrar

    public Cita registrar(CitaDTO dto) {
        // 1. El paciente debe existir y estar activo (consulta a paciente-service)
        PacienteResponse paciente = pacienteClient.obtener(dto.getIdPaciente());
        if (!"A".equals(paciente.estado())) {
            throw new BusinessException("El paciente " + paciente.nombres() + " está inactivo");
        }

        // 2. El médico debe existir y estar activo (consulta a medico-service)
        MedicoResponse medico = medicoClient.obtener(dto.getIdMedico());
        if (!"A".equals(medico.estado())) {
            throw new BusinessException("El médico " + medico.nombres() + " está inactivo");
        }

        // 3. La hora debe estar dentro del horario del médico
        validarHorario(dto.getIdMedico(), dto.getFecha(), dto.getHora());

        // 4. No debe haber cruce de citas
        if (repo.existsByIdMedicoAndFechaAndHoraAndEstadoNot(
                dto.getIdMedico(), dto.getFecha(), dto.getHora(), EstadoCita.CANCELADA)) {
            throw new BusinessException("El médico ya tiene una cita el " + dto.getFecha() + " a las " + dto.getHora());
        }
        if (repo.existsByIdPacienteAndFechaAndHoraAndEstadoNot(
                dto.getIdPaciente(), dto.getFecha(), dto.getHora(), EstadoCita.CANCELADA)) {
            throw new BusinessException("El paciente ya tiene una cita el " + dto.getFecha() + " a las " + dto.getHora());
        }

        Cita cita = new Cita();
        cita.setFecha(dto.getFecha());
        cita.setHora(dto.getHora());
        cita.setIdPaciente(dto.getIdPaciente());
        cita.setIdMedico(dto.getIdMedico());
        cita.setMotivo(dto.getMotivo());
        cita.setEstado(EstadoCita.PROGRAMADA);
        return repo.save(cita);
    }

    private void validarHorario(Long idMedico, LocalDate fecha, LocalTime hora) {
        List<HorarioResponse> horarios = medicoClient.horarios(idMedico);
        if (horarios == null || horarios.isEmpty()) {
            throw new BusinessException("El médico no tiene horarios de atención registrados");
        }
        String dia = diaEnEspanol(fecha.getDayOfWeek());

        boolean dentro = horarios.stream().anyMatch(h ->
                h.diaSemana().equalsIgnoreCase(dia)
                        && !hora.isBefore(LocalTime.parse(h.horaInicio()))
                        && hora.isBefore(LocalTime.parse(h.horaFin())));

        if (!dentro) {
            String disponibles = horarios.stream()
                    .map(h -> h.diaSemana() + " " + h.horaInicio() + "-" + h.horaFin())
                    .collect(Collectors.joining(", "));
            throw new BusinessException("El médico no atiende el " + dia + " a las " + hora
                    + ". Horarios disponibles: " + disponibles);
        }
    }

    private String diaEnEspanol(DayOfWeek d) {
        return switch (d) {
            case MONDAY -> "LUNES";
            case TUESDAY -> "MARTES";
            case WEDNESDAY -> "MIERCOLES";
            case THURSDAY -> "JUEVES";
            case FRIDAY -> "VIERNES";
            case SATURDAY -> "SABADO";
            case SUNDAY -> "DOMINGO";
        };
    }

    // ----------------------------------------------------------- estados

    public Cita actualizarEstado(Long id, EstadoCita nuevo) {
        Cita cita = obtener(id);
        if (!cita.getEstado().puedeCambiarA(nuevo)) {
            throw new BusinessException("No se puede cambiar una cita de " + cita.getEstado() + " a " + nuevo);
        }
        cita.setEstado(nuevo);
        return repo.save(cita);
    }

    public Cita cancelar(Long id) {
        return actualizarEstado(id, EstadoCita.CANCELADA);
    }

    // ----------------------------------------------------------- atención

    @Transactional
    public Atencion registrarAtencion(Long idCita, AtencionDTO dto) {
        Cita cita = obtener(idCita);
        if (cita.getEstado() != EstadoCita.CONFIRMADA) {
            throw new BusinessException("Solo se puede registrar la atención de una cita CONFIRMADA (estado actual: "
                    + cita.getEstado() + ")");
        }
        if (atencionRepo.existsByCitaIdCita(idCita)) {
            throw new BusinessException("La cita ya tiene una atención registrada");
        }

        Atencion atencion = new Atencion();
        atencion.setCita(cita);
        atencion.setDiagnostico(dto.getDiagnostico());
        atencion.setTratamiento(dto.getTratamiento());
        atencion.setObservaciones(dto.getObservaciones());

        cita.setEstado(EstadoCita.ATENDIDA); // al registrar la atención la cita pasa a ATENDIDA
        repo.save(cita);
        return atencionRepo.save(atencion);
    }

    public Atencion obtenerAtencion(Long idCita) {
        obtener(idCita);
        return atencionRepo.findByCitaIdCita(idCita).orElseThrow(
                () -> new ResourceNotFoundException("La cita " + idCita + " no tiene atención registrada"));
    }
}
