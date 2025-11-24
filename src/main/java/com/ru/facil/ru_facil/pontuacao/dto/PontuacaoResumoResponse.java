package com.ru.facil.ru_facil.pontuacao.dto;

import com.ru.facil.ru_facil.entities.Cliente;
import com.ru.facil.ru_facil.entities.PontuacaoUsuario;

public record PontuacaoResumoResponse(
        Long clienteId,
        String nome,
        String email,
        Integer totalPontos,
        Integer nivel,
        Integer pontosParaProximoNivel
) {

    public static PontuacaoResumoResponse of(Cliente cliente, PontuacaoUsuario pontuacao) {
        int total = pontuacao.getTotalPontos() == null ? 0 : pontuacao.getTotalPontos();
        int pontosPorNivel = 100; // cada 100 pontos = 1 nível
        int nivel = total / pontosPorNivel;
        int limiteProximoNivel = (nivel + 1) * pontosPorNivel;
        int falta = limiteProximoNivel - total;

        return new PontuacaoResumoResponse(
                cliente.getId(),
                cliente.getNome(),
                cliente.getEmail(),
                total,
                nivel,
                falta
        );
    }
}
