package com.tecsup.client;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record EspecialidadResponse(Long idEspecialidad, String nombre) {
}
