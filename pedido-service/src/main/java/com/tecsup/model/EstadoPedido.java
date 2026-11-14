package com.tecsup.model;

/**
 * Estados del pedido (no se elimina, se anula):
 * PENDIENTE -> ATENDIDO | ANULADO
 * ATENDIDO y ANULADO son estados finales.
 */
public enum EstadoPedido {
    PENDIENTE,
    ATENDIDO,
    ANULADO;

    public boolean puedeCambiarA(EstadoPedido nuevo) {
        return this == PENDIENTE && (nuevo == ATENDIDO || nuevo == ANULADO);
    }
}
