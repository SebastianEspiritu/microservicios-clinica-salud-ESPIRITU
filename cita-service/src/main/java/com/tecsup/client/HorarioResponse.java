package com.tecsup.client;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/** Horario de atención que devuelve medico-service (horas como texto "HH:mm:ss"). */
@JsonIgnoreProperties(ignoreUnknown = true)
public record HorarioResponse(Long idHorario, String diaSemana, String horaInicio, String horaFin) {
}
