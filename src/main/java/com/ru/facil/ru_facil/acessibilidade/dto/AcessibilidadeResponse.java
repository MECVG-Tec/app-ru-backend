package com.ru.facil.ru_facil.acessibilidade.dto;

import com.ru.facil.ru_facil.entities.Cliente;

public record AcessibilidadeResponse(
        Long clienteId,
        String nome,
        String email,
        Boolean prefereAltoContraste,
        Boolean prefereLinguagemSimples,
        Boolean prefereFonteGrande
) {

    public static AcessibilidadeResponse of(Cliente c) {
        return new AcessibilidadeResponse(
                c.getId(),
                c.getNome(),
                c.getEmail(),
                c.getPrefereAltoContraste(),
                c.getPrefereLinguagemSimples(),
                c.getPrefereFonteGrande()
        );
    }
}
