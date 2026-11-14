package com.tecsup.controller;

import com.tecsup.dto.EstadoDTO;
import com.tecsup.dto.PedidoDTO;
import com.tecsup.dto.PedidoResponseDTO;
import com.tecsup.services.PedidoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/pedidos")
public class PedidoController {

    @Autowired
    private PedidoService service;

    @GetMapping
    public ResponseEntity<List<PedidoResponseDTO>> listar() {
        return ResponseEntity.ok(service.listar());
    }

    // Pedido + cliente + producto (comunicación con Feign)
    @GetMapping("/{id}")
    public ResponseEntity<PedidoResponseDTO> obtener(@PathVariable Long id) {
        return ResponseEntity.ok(service.obtener(id));
    }

    @PostMapping
    public ResponseEntity<PedidoResponseDTO> registrar(@Valid @RequestBody PedidoDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.registrar(dto));
    }

    // Cambiar estado: PENDIENTE -> ATENDIDO | ANULADO
    @PutMapping("/{id}/estado")
    public ResponseEntity<PedidoResponseDTO> cambiarEstado(@PathVariable Long id,
                                                           @Valid @RequestBody EstadoDTO dto) {
        return ResponseEntity.ok(service.cambiarEstado(id, dto.getEstado()));
    }

    // El pedido no se elimina: se anula
    @PutMapping("/{id}/anular")
    public ResponseEntity<PedidoResponseDTO> anular(@PathVariable Long id) {
        return ResponseEntity.ok(service.anular(id));
    }
}
