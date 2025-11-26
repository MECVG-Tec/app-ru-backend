package com.ru.facil.ru_facil.resources;

import com.ru.facil.ru_facil.acessibilidade.dto.AcessibilidadeRequest;
import com.ru.facil.ru_facil.acessibilidade.dto.AcessibilidadeResponse;
import com.ru.facil.ru_facil.entities.Cliente;
import com.ru.facil.ru_facil.repositories.ClienteRepository;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/v1/acessibilidade")
public class AcessibilidadeResource {

    private final ClienteRepository clienteRepository;

    public AcessibilidadeResource(ClienteRepository clienteRepository) {
        this.clienteRepository = clienteRepository;
    }

    @Operation(summary = "Consulta preferências de acessibilidade de um usuário pelo e-mail")
    @GetMapping
    public AcessibilidadeResponse getByEmail(@RequestParam String email) {

        Cliente cliente = clienteRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Cliente não encontrado para o e-mail: " + email
                ));

        return AcessibilidadeResponse.of(cliente);
    }

    @Operation(summary = "Atualiza preferências de acessibilidade de um usuário")
    @PutMapping
    public ResponseEntity<AcessibilidadeResponse> atualizar(
            @Valid @RequestBody AcessibilidadeRequest request) {

        Cliente cliente = clienteRepository.findByEmail(request.email())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Cliente não encontrado para o e-mail: " + request.email()
                ));

        cliente.setPrefereAltoContraste(request.prefereAltoContraste());
        cliente.setPrefereLinguagemSimples(request.prefereLinguagemSimples());
        cliente.setPrefereFonteGrande(request.prefereFonteGrande());

        cliente = clienteRepository.save(cliente);

        return ResponseEntity.ok(AcessibilidadeResponse.of(cliente));
    }
}
