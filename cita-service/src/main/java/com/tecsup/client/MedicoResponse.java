package com.tecsup.client;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/** Datos del médico que devuelve medico-service. */
@JsonIgnoreProperties(ignoreUnknown = true)
public record MedicoResponse(Long idMedico, String cmp, String nombres, String apellidos,
                             String estado, EspecialidadResponse especialidad) {
}
