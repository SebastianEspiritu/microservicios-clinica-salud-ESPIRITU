package com.tecsup.services;

import com.tecsup.dto.HorarioDTO;
import com.tecsup.exception.BusinessException;
import com.tecsup.exception.ResourceNotFoundException;
import com.tecsup.model.HorarioMedico;
import com.tecsup.model.Medico;
import com.tecsup.repository.HorarioMedicoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class HorarioMedicoService {

    @Autowired
    private HorarioMedicoRepository repo;

    @Autowired
    private MedicoService medicoService;

    public List<HorarioMedico> listarPorMedico(Long idMedico) {
        medicoService.obtener(idMedico); // valida que el médico exista
        return repo.findByMedicoIdMedicoOrderByIdHorario(idMedico);
    }

    public HorarioMedico registrar(Long idMedico, HorarioDTO dto) {
        Medico medico = medicoService.obtener(idMedico);

        if (!dto.getHoraInicio().isBefore(dto.getHoraFin())) {
            throw new BusinessException("La hora de inicio debe ser menor que la hora de fin");
        }

        // No permitir horarios que se crucen el mismo día
        for (HorarioMedico h : repo.findByMedicoIdMedicoOrderByIdHorario(idMedico)) {
            boolean mismoDia = h.getDiaSemana().equalsIgnoreCase(dto.getDiaSemana());
            boolean seCruzan = dto.getHoraInicio().isBefore(h.getHoraFin())
                    && h.getHoraInicio().isBefore(dto.getHoraFin());
            if (mismoDia && seCruzan) {
                throw new BusinessException("El horario se cruza con otro horario del " + h.getDiaSemana()
                        + " (" + h.getHoraInicio() + " - " + h.getHoraFin() + ")");
            }
        }

        HorarioMedico horario = new HorarioMedico();
        horario.setMedico(medico);
        horario.setDiaSemana(dto.getDiaSemana());
        horario.setHoraInicio(dto.getHoraInicio());
        horario.setHoraFin(dto.getHoraFin());
        return repo.save(horario);
    }

    public void eliminar(Long idHorario) {
        HorarioMedico h = repo.findById(idHorario).orElseThrow(
                () -> new ResourceNotFoundException("Horario no encontrado con id: " + idHorario));
        repo.delete(h);
    }
}
