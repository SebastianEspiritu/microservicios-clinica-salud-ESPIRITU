package com.tecsup.repository;

import com.tecsup.model.Cita;
import com.tecsup.model.EstadoCita;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public interface CitaRepository extends JpaRepository<Cita, Long> {

    List<Cita> findByIdPaciente(Long idPaciente);

    List<Cita> findByIdMedico(Long idMedico);

    List<Cita> findByEstado(EstadoCita estado);

    // ¿El médico ya tiene una cita activa a esa fecha y hora?
    boolean existsByIdMedicoAndFechaAndHoraAndEstadoNot(Long idMedico, LocalDate fecha,
                                                        LocalTime hora, EstadoCita estado);

    // ¿El paciente ya tiene una cita activa a esa fecha y hora?
    boolean existsByIdPacienteAndFechaAndHoraAndEstadoNot(Long idPaciente, LocalDate fecha,
                                                          LocalTime hora, EstadoCita estado);
}
