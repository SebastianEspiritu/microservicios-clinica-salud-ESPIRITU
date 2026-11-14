package com.tecsup.client;

import com.tecsup.dto.ClienteDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

// Se comunica con el servicio registrado en Eureka como "cliente-service"
@FeignClient(name = "cliente-service")
public interface ClienteClient {

    @GetMapping("/api/clientes/{id}")
    ClienteDTO obtenerCliente(@PathVariable("id") Long id);
}
