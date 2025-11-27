package com.ru.facil.ru_facil.resources;

import com.ru.facil.ru_facil.entities.Cliente;
import com.ru.facil.ru_facil.entities.HistoricoPontos;
import com.ru.facil.ru_facil.entities.PontuacaoUsuario;
import com.ru.facil.ru_facil.pontuacao.dto.HistoricoPontosResponse;
import com.ru.facil.ru_facil.pontuacao.dto.PontuacaoResumoResponse;
import com.ru.facil.ru_facil.repositories.ClienteRepository;
import com.ru.facil.ru_facil.repositories.HistoricoPontosRepository;
import com.ru.facil.ru_facil.repositories.PontuacaoUsuarioRepository;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/v1/pontuacao")
public class PontuacaoResource {

    private final ClienteRepository clienteRepository;
    private final PontuacaoUsuarioRepository pontuacaoUsuarioRepository;
    private final HistoricoPontosRepository historicoPontosRepository;

    public PontuacaoResource(ClienteRepository clienteRepository,
                             PontuacaoUsuarioRepository pontuacaoUsuarioRepository,
                             HistoricoPontosRepository historicoPontosRepository) {
        this.clienteRepository = clienteRepository;
        this.pontuacaoUsuarioRepository = pontuacaoUsuarioRepository;
        this.historicoPontosRepository = historicoPontosRepository;
    }

    @Operation(summary = "Retorna o saldo atual de pontos do usuário, nível e progresso")
    @GetMapping
    public ResponseEntity<PontuacaoResumoResponse> getPontuacao(@RequestParam String email) {

        Cliente cliente = clienteRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Cliente não encontrado para o e-mail: " + email
                ));

        PontuacaoUsuario pontuacao = pontuacaoUsuarioRepository.findByClienteId(cliente.getId())
                .orElseGet(() -> {
                    PontuacaoUsuario nova = new PontuacaoUsuario();
                    nova.setCliente(cliente);
                    nova.setTotalPontos(0);
                    return pontuacaoUsuarioRepository.save(nova);
                });

        return ResponseEntity.ok(PontuacaoResumoResponse.of(cliente, pontuacao));
    }

    @Operation(summary = "Lista o histórico de pontos do usuário")
    @GetMapping("/historico")
    public List<HistoricoPontosResponse> getHistorico(@RequestParam String email) {

        Cliente cliente = clienteRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Cliente não encontrado para o e-mail: " + email
                ));

        PontuacaoUsuario pontuacao = pontuacaoUsuarioRepository.findByClienteId(cliente.getId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Pontuação não encontrada para o cliente: " + email
                ));

        List<HistoricoPontos> historico = historicoPontosRepository
                .findByPontuacaoUsuarioIdOrderByCriadoEmDesc(pontuacao.getId());

        return historico.stream()
                .map(HistoricoPontosResponse::of)
                .toList();
    }
}
