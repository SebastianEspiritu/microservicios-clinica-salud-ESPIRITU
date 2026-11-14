package com.tecsup.controller;

import com.tecsup.dto.HorarioDTO;
import com.tecsup.model.HorarioMedico;
import com.tecsup.services.HorarioMedicoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class HorarioMedicoController {

    @Autowired
    private HorarioMedicoService service;

    // Horarios de un médico (lo consume cita-service)
    @GetMapping("/medicos/{idMedico}/horarios")
    public ResponseEntity<List<HorarioMedico>> listar(@PathVariable Long idMedico) {
        return ResponseEntity.ok(service.listarPorMedico(idMedico));
    }

    @PostMapping("/medicos/{idMedico}/horarios")
    public ResponseEntity<HorarioMedico> registrar(@PathVariable Long idMedico,
                                                   @Valid @RequestBody HorarioDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.registrar(idMedico, dto));
    }

    @DeleteMapping("/horarios/{idHorario}")
    public ResponseEntity<Map<String, String>> eliminar(@PathVariable Long idHorario) {
        service.eliminar(idHorario);
        return ResponseEntity.ok(Map.of("mensaje", "Horario eliminado correctamente"));
    }
}
