package com.tecsup.controller;

import com.tecsup.dto.AtencionDTO;
import com.tecsup.dto.CitaDTO;
import com.tecsup.dto.CitaDetalleDTO;
import com.tecsup.dto.EstadoDTO;
import com.tecsup.model.Atencion;
import com.tecsup.model.Cita;
import com.tecsup.model.EstadoCita;
import com.tecsup.services.CitaService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/citas")
public class CitaController {

    @Autowired
    private CitaService service;

    // Listar citas (filtros opcionales: ?idPaciente=1  ?idMedico=1  ?estado=PROGRAMADA)
    @GetMapping
    public ResponseEntity<List<Cita>> listar(@RequestParam(required = false) Long idPaciente,
                                             @RequestParam(required = false) Long idMedico,
                                             @RequestParam(required = false) EstadoCita estado) {
        return ResponseEntity.ok(service.listar(idPaciente, idMedico, estado));
    }

    // Buscar cita por ID
    @GetMapping("/{id}")
    public ResponseEntity<Cita> obtener(@PathVariable Long id) {
        return ResponseEntity.ok(service.obtener(id));
    }

    // Cita con datos de paciente y médico (consulta a los otros microservicios)
    @GetMapping("/{id}/detalle")
    public ResponseEntity<CitaDetalleDTO> detalle(@PathVariable Long id) {
        return ResponseEntity.ok(service.detalle(id));
    }

    // Registrar cita
    @PostMapping
    public ResponseEntity<Cita> registrar(@Valid @RequestBody CitaDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.registrar(dto));
    }

    // Actualizar estado de cita
    @PutMapping("/{id}/estado")
    public ResponseEntity<Cita> actualizarEstado(@PathVariable Long id, @Valid @RequestBody EstadoDTO dto) {
        return ResponseEntity.ok(service.actualizarEstado(id, dto.getEstado()));
    }

    // Cancelar cita
    @PutMapping("/{id}/cancelar")
    public ResponseEntity<Cita> cancelar(@PathVariable Long id) {
        return ResponseEntity.ok(service.cancelar(id));
    }

    // Registrar la atención (resultado) de la cita -> pasa a ATENDIDA
    @PostMapping("/{id}/atencion")
    public ResponseEntity<Atencion> registrarAtencion(@PathVariable Long id, @Valid @RequestBody AtencionDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.registrarAtencion(id, dto));
    }

    @GetMapping("/{id}/atencion")
    public ResponseEntity<Atencion> obtenerAtencion(@PathVariable Long id) {
        return ResponseEntity.ok(service.obtenerAtencion(id));
    }
}
