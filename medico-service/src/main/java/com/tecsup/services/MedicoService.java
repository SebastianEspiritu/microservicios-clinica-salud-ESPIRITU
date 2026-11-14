package com.tecsup.services;

import com.tecsup.dto.MedicoDTO;
import com.tecsup.exception.BusinessException;
import com.tecsup.exception.ResourceNotFoundException;
import com.tecsup.model.Medico;
import com.tecsup.repository.MedicoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MedicoService {

    @Autowired
    private MedicoRepository repo;

    @Autowired
    private EspecialidadService especialidadService;

    public List<Medico> listar() {
        return repo.findAll();
    }

    public List<Medico> listarPorEspecialidad(Long idEspecialidad) {
        especialidadService.obtener(idEspecialidad); // valida que exista
        return repo.findByEspecialidadIdEspecialidad(idEspecialidad);
    }

    public Medico obtener(Long id) {
        return repo.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Médico no encontrado con id: " + id));
    }

    public Medico registrar(MedicoDTO dto) {
        if (repo.existsByCmp(dto.getCmp())) {
            throw new BusinessException("Ya existe un médico con CMP " + dto.getCmp());
        }
        Medico m = new Medico();
        copiar(dto, m);
        return repo.save(m);
    }

    public Medico actualizar(Long id, MedicoDTO dto) {
        Medico m = obtener(id);
        if (repo.existsByCmpAndIdMedicoNot(dto.getCmp(), id)) {
            throw new BusinessException("Ya existe otro médico con CMP " + dto.getCmp());
        }
        copiar(dto, m);
        return repo.save(m);
    }

    public void eliminar(Long id) {
        Medico m = obtener(id);
        repo.delete(m); // sus horarios se eliminan en cascada
    }

    private void copiar(MedicoDTO dto, Medico m) {
        m.setCmp(dto.getCmp());
        m.setNombres(dto.getNombres());
        m.setApellidos(dto.getApellidos());
        m.setTelefono(dto.getTelefono());
        m.setCorreo(dto.getCorreo());
        m.setEspecialidad(especialidadService.obtener(dto.getIdEspecialidad()));
        if (dto.getEstado() != null) {
            m.setEstado(dto.getEstado());
        }
    }
}
