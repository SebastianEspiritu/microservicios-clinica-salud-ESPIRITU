package com.tecsup.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/** Datos de la categoría que se obtienen de categoria-service mediante Feign. */
@JsonIgnoreProperties(ignoreUnknown = true)
public class CategoriaDTO {

    private Long id;

    private String nombre;

    private String estado;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }
}
