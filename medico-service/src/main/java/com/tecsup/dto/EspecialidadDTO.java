package com.tecsup.dto;

import jakarta.validation.constraints.*;

public class EspecialidadDTO {

    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 100, message = "Máximo 100 caracteres")
    private String nombre;

    @Size(max = 200, message = "Máximo 200 caracteres")
    private String descripcion;

    @Pattern(regexp = "[AI]", message = "El estado debe ser A (activo) o I (inactivo)")
    private String estado;

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }
}
