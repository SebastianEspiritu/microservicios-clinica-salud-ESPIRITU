package com.tecsup.dto;

import com.tecsup.model.EstadoPedido;

import java.time.LocalDate;

/** Respuesta completa del pedido: datos propios + cliente + producto (vía Feign). */
public record PedidoResponseDTO(
        Long id,
        LocalDate fecha,
        Integer cantidad,
        EstadoPedido estado,
        Double total,
        ClienteDTO cliente,
        ProductoDTO producto) {
}
