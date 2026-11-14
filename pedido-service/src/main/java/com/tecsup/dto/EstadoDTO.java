package com.tecsup.dto;

import com.tecsup.model.EstadoPedido;
import jakarta.validation.constraints.NotNull;

public class EstadoDTO {

    @NotNull(message = "El estado es obligatorio: PENDIENTE, ATENDIDO o ANULADO")
    private EstadoPedido estado;

    public EstadoPedido getEstado() {
        return estado;
    }

    public void setEstado(EstadoPedido estado) {
        this.estado = estado;
    }
}
