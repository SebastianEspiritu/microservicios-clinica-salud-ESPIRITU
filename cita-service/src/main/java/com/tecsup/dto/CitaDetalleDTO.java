package com.tecsup.dto;

import com.tecsup.client.MedicoResponse;
import com.tecsup.client.PacienteResponse;
import com.tecsup.model.Cita;

/** Cita + datos del paciente y del médico obtenidos de los otros microservicios. */
public record CitaDetalleDTO(Cita cita, PacienteResponse paciente, MedicoResponse medico) {
}
