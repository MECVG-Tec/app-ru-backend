package com.ru.facil.ru_facil.resources;

import com.ru.facil.ru_facil.entities.SuporteMensagem;
import com.ru.facil.ru_facil.enuns.SupportStatus;
import com.ru.facil.ru_facil.repositories.SuporteMensagemRepository;
import com.ru.facil.ru_facil.suporte.dto.AtualizarStatusSuporteRequest;
import com.ru.facil.ru_facil.suporte.dto.SuporteRequest;
import com.ru.facil.ru_facil.suporte.dto.SuporteResponse;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/v1/suporte")
public class SuporteResource {

    private final SuporteMensagemRepository suporteMensagemRepository;

    public SuporteResource(SuporteMensagemRepository suporteMensagemRepository) {
        this.suporteMensagemRepository = suporteMensagemRepository;
    }

    @Operation(summary = "Envia uma mensagem de suporte (dúvida, sugestão ou suporte técnico)")
    @PostMapping("/mensagens")
    public ResponseEntity<SuporteResponse> criarMensagem(@Valid @RequestBody SuporteRequest request) {

        SuporteMensagem entidade = new SuporteMensagem();
        entidade.setNome(request.nome());
        entidade.setEmail(request.email());
        entidade.setCategoria(request.categoria());
        entidade.setAssunto(request.assunto());
        entidade.setMensagem(request.mensagem());
        entidade.setStatus(SupportStatus.ABERTA);

        entidade = suporteMensagemRepository.save(entidade);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(SuporteResponse.of(entidade));
    }

    @Operation(summary = "Lista mensagens de suporte filtrando por e-mail do usuário (opcional)")
    @GetMapping("/mensagens")
    public List<SuporteResponse> listarMensagens(@RequestParam(required = false) String email) {

        List<SuporteMensagem> mensagens;

        if (email != null && !email.isBlank()) {
            mensagens = suporteMensagemRepository.findByEmailOrderByCriadoEmDesc(email);
        } else {
            mensagens = suporteMensagemRepository.findAllByOrderByCriadoEmDesc();
        }

        return mensagens.stream()
                .map(SuporteResponse::of)
                .toList();
    }

    @Operation(summary = "Atualiza o status de uma mensagem de suporte (uso interno/admin)")
    @PatchMapping("/mensagens/{id}/status")
    public SuporteResponse atualizarStatus(@PathVariable Long id,
                                           @Valid @RequestBody AtualizarStatusSuporteRequest request) {

        SuporteMensagem mensagem = suporteMensagemRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Mensagem de suporte não encontrada"
                ));

        mensagem.setStatus(request.status());
        mensagem = suporteMensagemRepository.save(mensagem);

        return SuporteResponse.of(mensagem);
    }
}
