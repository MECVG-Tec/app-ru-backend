package com.ru.facil.ru_facil.suporte.dto;

import com.ru.facil.ru_facil.entities.SuporteMensagem;
import com.ru.facil.ru_facil.enuns.SupportCategory;
import com.ru.facil.ru_facil.enuns.SupportStatus;

import java.time.LocalDateTime;

public record SuporteResponse(
        Long id,
        String nome,
        String email,
        SupportCategory categoria,
        String assunto,
        String mensagem,
        SupportStatus status,
        LocalDateTime criadoEm,
        LocalDateTime atualizadoEm
) {

    public static SuporteResponse of(SuporteMensagem m) {
        return new SuporteResponse(
                m.getId(),
                m.getNome(),
                m.getEmail(),
                m.getCategoria(),
                m.getAssunto(),
                m.getMensagem(),
                m.getStatus(),
                m.getCriadoEm(),
                m.getAtualizadoEm()
        );
    }
}
