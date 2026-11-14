package com.tecsup.client;

import com.tecsup.exception.ResourceNotFoundException;
import com.tecsup.exception.ServiceUnavailableException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;

/** Cliente REST para comunicarse con paciente-service. */
@Component
public class PacienteClient {

    private final RestClient restClient;
    private final String url;

    public PacienteClient(@Value("${servicios.paciente.url}") String url) {
        this.url = url;
        this.restClient = RestClient.create(url);
    }

    public PacienteResponse obtener(Long idPaciente) {
        try {
            return restClient.get()
                    .uri("/api/pacientes/{id}", idPaciente)
                    .retrieve()
                    .body(PacienteResponse.class);
        } catch (HttpClientErrorException.NotFound e) {
            throw new ResourceNotFoundException("Paciente no encontrado con id: " + idPaciente);
        } catch (ResourceAccessException e) {
            throw new ServiceUnavailableException("paciente-service no está disponible en " + url);
        }
    }
}
