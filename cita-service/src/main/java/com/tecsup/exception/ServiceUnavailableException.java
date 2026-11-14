package com.tecsup.exception;

/** Se lanza cuando otro microservicio no responde. */
public class ServiceUnavailableException extends RuntimeException {

    public ServiceUnavailableException(String mensaje) {
        super(mensaje);
    }
}
