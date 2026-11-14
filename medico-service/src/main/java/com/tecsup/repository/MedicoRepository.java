package com.tecsup.repository;

import com.tecsup.model.Medico;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MedicoRepository extends JpaRepository<Medico, Long> {

    boolean existsByCmp(String cmp);

    boolean existsByCmpAndIdMedicoNot(String cmp, Long idMedico);

    boolean existsByEspecialidadIdEspecialidad(Long idEspecialidad);

    List<Medico> findByEspecialidadIdEspecialidad(Long idEspecialidad);
}
