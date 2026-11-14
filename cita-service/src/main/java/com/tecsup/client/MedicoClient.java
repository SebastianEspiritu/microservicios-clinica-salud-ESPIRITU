package com.tecsup.client;

import com.tecsup.exception.ResourceNotFoundException;
import com.tecsup.exception.ServiceUnavailableException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;

import java.util.List;

/** Cliente REST para comunicarse con medico-service. */
@Component
public class MedicoClient {

    private final RestClient restClient;
    private final String url;

    public MedicoClient(@Value("${servicios.medico.url}") String url) {
        this.url = url;
        this.restClient = RestClient.create(url);
    }

    public MedicoResponse obtener(Long idMedico) {
        try {
            return restClient.get()
                    .uri("/api/medicos/{id}", idMedico)
                    .retrieve()
                    .body(MedicoResponse.class);
        } catch (HttpClientErrorException.NotFound e) {
            throw new ResourceNotFoundException("Médico no encontrado con id: " + idMedico);
        } catch (ResourceAccessException e) {
            throw new ServiceUnavailableException("medico-service no está disponible en " + url);
        }
    }

    public List<HorarioResponse> horarios(Long idMedico) {
        try {
            return restClient.get()
                    .uri("/api/medicos/{id}/horarios", idMedico)
                    .retrieve()
                    .body(new ParameterizedTypeReference<List<HorarioResponse>>() {
                    });
        } catch (HttpClientErrorException.NotFound e) {
            throw new ResourceNotFoundException("Médico no encontrado con id: " + idMedico);
        } catch (ResourceAccessException e) {
            throw new ServiceUnavailableException("medico-service no está disponible en " + url);
        }
    }
}
