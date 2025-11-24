package com.ru.facil.ru_facil.pontuacao.dto;

import com.ru.facil.ru_facil.entities.HistoricoPontos;
import com.ru.facil.ru_facil.enuns.PontosEventoTipo;

import java.time.LocalDateTime;

public record HistoricoPontosResponse(
        Long id,
        PontosEventoTipo tipoEvento,
        String descricao,
        Integer pontos,
        LocalDateTime criadoEm
) {

    public static HistoricoPontosResponse of(HistoricoPontos h) {
        return new HistoricoPontosResponse(
                h.getId(),
                h.getTipoEvento(),
                h.getDescricao(),
                h.getPontos(),
                h.getCriadoEm()
        );
    }
}
