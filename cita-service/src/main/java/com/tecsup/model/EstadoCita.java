package com.tecsup.model;

/**
 * Estados posibles de una cita y los cambios permitidos:
 * PROGRAMADA -> CONFIRMADA | CANCELADA
 * CONFIRMADA -> ATENDIDA   | CANCELADA
 * ATENDIDA y CANCELADA son estados finales.
 */
public enum EstadoCita {
    PROGRAMADA,
    CONFIRMADA,
    ATENDIDA,
    CANCELADA;

    public boolean puedeCambiarA(EstadoCita nuevo) {
        return switch (this) {
            case PROGRAMADA -> nuevo == CONFIRMADA || nuevo == CANCELADA;
            case CONFIRMADA -> nuevo == ATENDIDA || nuevo == CANCELADA;
            case ATENDIDA, CANCELADA -> false;
        };
    }
}
