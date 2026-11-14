package com.tecsup.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/** Datos del producto obtenidos de producto-service mediante Feign. */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ProductoDTO {

    private Long id;
    private String nombre;
    private Double precio;
    private Integer stock;

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

    public Double getPrecio() {
        return precio;
    }

    public void setPrecio(Double precio) {
        this.precio = precio;
    }

    public Integer getStock() {
        return stock;
    }

    public void setStock(Integer stock) {
        this.stock = stock;
    }
}
