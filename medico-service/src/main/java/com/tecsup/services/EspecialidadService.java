package com.tecsup.services;

import com.tecsup.dto.EspecialidadDTO;
import com.tecsup.exception.BusinessException;
import com.tecsup.exception.ResourceNotFoundException;
import com.tecsup.model.Especialidad;
import com.tecsup.repository.EspecialidadRepository;
import com.tecsup.repository.MedicoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EspecialidadService {

    @Autowired
    private EspecialidadRepository repo;

    @Autowired
    private MedicoRepository medicoRepo;

    public List<Especialidad> listar() {
        return repo.findAll();
    }

    public Especialidad obtener(Long id) {
        return repo.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Especialidad no encontrada con id: " + id));
    }

    public Especialidad registrar(EspecialidadDTO dto) {
        Especialidad e = new Especialidad();
        copiar(dto, e);
        return repo.save(e);
    }

    public Especialidad actualizar(Long id, EspecialidadDTO dto) {
        Especialidad e = obtener(id);
        copiar(dto, e);
        return repo.save(e);
    }

    public void eliminar(Long id) {
        Especialidad e = obtener(id);
        if (medicoRepo.existsByEspecialidadIdEspecialidad(id)) {
            throw new BusinessException("No se puede eliminar: hay médicos registrados con esta especialidad");
        }
        repo.delete(e);
    }

    private void copiar(EspecialidadDTO dto, Especialidad e) {
        e.setNombre(dto.getNombre());
        e.setDescripcion(dto.getDescripcion());
        if (dto.getEstado() != null) {
            e.setEstado(dto.getEstado());
        }
    }
}
