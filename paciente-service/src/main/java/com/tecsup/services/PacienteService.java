package com.tecsup.services;

import com.tecsup.dto.PacienteDTO;
import com.tecsup.exception.BusinessException;
import com.tecsup.exception.ResourceNotFoundException;
import com.tecsup.model.Paciente;
import com.tecsup.repository.PacienteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PacienteService {

    @Autowired
    private PacienteRepository repo;

    public List<Paciente> listar() {
        return repo.findAll();
    }

    public Paciente obtener(Long id) {
        return repo.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Paciente no encontrado con id: " + id));
    }

    public Paciente registrar(PacienteDTO dto) {
        if (repo.existsByDni(dto.getDni())) {
            throw new BusinessException("Ya existe un paciente con DNI " + dto.getDni());
        }
        Paciente p = new Paciente();
        copiar(dto, p);
        return repo.save(p);
    }

    public Paciente actualizar(Long id, PacienteDTO dto) {
        Paciente p = obtener(id);
        if (repo.existsByDniAndIdPacienteNot(dto.getDni(), id)) {
            throw new BusinessException("Ya existe otro paciente con DNI " + dto.getDni());
        }
        copiar(dto, p);
        return repo.save(p);
    }

    public void eliminar(Long id) {
        Paciente p = obtener(id);
        repo.delete(p);
    }

    private void copiar(PacienteDTO dto, Paciente p) {
        p.setDni(dto.getDni());
        p.setNombres(dto.getNombres());
        p.setApellidos(dto.getApellidos());
        p.setFechaNacimiento(dto.getFechaNacimiento());
        p.setSexo(dto.getSexo());
        p.setTelefono(dto.getTelefono());
        p.setDireccion(dto.getDireccion());
        p.setCorreo(dto.getCorreo());
        if (dto.getEstado() != null) {
            p.setEstado(dto.getEstado());
        }
    }
}
