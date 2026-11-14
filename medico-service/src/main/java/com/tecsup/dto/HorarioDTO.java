package com.tecsup.dto;

import jakarta.validation.constraints.*;

import java.time.LocalTime;

public class HorarioDTO {

    @NotBlank(message = "El día es obligatorio")
    @Pattern(regexp = "LUNES|MARTES|MIERCOLES|JUEVES|VIERNES|SABADO|DOMINGO",
            message = "Día inválido. Use: LUNES, MARTES, MIERCOLES, JUEVES, VIERNES, SABADO o DOMINGO")
    private String diaSemana;

    @NotNull(message = "La hora de inicio es obligatoria (formato HH:mm)")
    private LocalTime horaInicio;

    @NotNull(message = "La hora de fin es obligatoria (formato HH:mm)")
    private LocalTime horaFin;

    public String getDiaSemana() {
        return diaSemana;
    }

    public void setDiaSemana(String diaSemana) {
        this.diaSemana = diaSemana;
    }

    public LocalTime getHoraInicio() {
        return horaInicio;
    }

    public void setHoraInicio(LocalTime horaInicio) {
        this.horaInicio = horaInicio;
    }

    public LocalTime getHoraFin() {
        return horaFin;
    }

    public void setHoraFin(LocalTime horaFin) {
        this.horaFin = horaFin;
    }
}
