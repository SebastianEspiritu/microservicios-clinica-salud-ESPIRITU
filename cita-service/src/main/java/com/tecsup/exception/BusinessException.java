package com.tecsup.exception;

/** Error de regla de negocio (ej. DNI duplicado, cambio de estado no permitido). */
public class BusinessException extends RuntimeException {

    public BusinessException(String mensaje) {
        super(mensaje);
    }
}
