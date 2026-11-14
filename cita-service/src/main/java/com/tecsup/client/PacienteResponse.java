package com.tecsup.client;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/** Datos del paciente que devuelve paciente-service. */
@JsonIgnoreProperties(ignoreUnknown = true)
public record PacienteResponse(Long idPaciente, String dni, String nombres, String apellidos,
                               String telefono, String correo, String estado) {
}
