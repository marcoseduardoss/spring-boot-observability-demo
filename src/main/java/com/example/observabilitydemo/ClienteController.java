package com.example.observabilitydemo;

import io.micrometer.core.annotation.Timed;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/clientes")
@RequiredArgsConstructor
public class ClienteController {

    private static final Logger log = LoggerFactory.getLogger(ClienteController.class);

    @GetMapping("/{id}")
    @Timed(value = "cliente.buscar.tempo", description = "Tempo da busca de cliente")
    public ClienteDto buscarCliente(@PathVariable("id") Long id) {
        log.info("Buscando cliente com id={}", id);
        ClienteDto dto = new ClienteDto(id, "Cliente " + id, "cliente" + id + "@exemplo.com");
        log.info("Cliente encontrado id={}", id);
        return dto;
    }

    @PostMapping
    @Timed(value = "cliente.criar.tempo", description = "Tempo da criação de cliente")
    public ClienteDto criarCliente(@RequestBody ClienteDto dto) {
        log.info("Criando cliente nome={}", dto.nome());
        return dto;
    }
}
