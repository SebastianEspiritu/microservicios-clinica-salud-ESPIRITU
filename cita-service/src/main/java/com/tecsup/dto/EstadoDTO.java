package com.tecsup.dto;

import com.tecsup.model.EstadoCita;
import jakarta.validation.constraints.NotNull;

public class EstadoDTO {

    @NotNull(message = "El estado es obligatorio: PROGRAMADA, CONFIRMADA, ATENDIDA o CANCELADA")
    private EstadoCita estado;

    public EstadoCita getEstado() {
        return estado;
    }

    public void setEstado(EstadoCita estado) {
        this.estado = estado;
    }
}
