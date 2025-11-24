package com.ru.facil.ru_facil.pontuacao;

import com.ru.facil.ru_facil.entities.Cliente;
import com.ru.facil.ru_facil.entities.HistoricoPontos;
import com.ru.facil.ru_facil.entities.PontuacaoUsuario;
import com.ru.facil.ru_facil.enuns.PontosEventoTipo;
import com.ru.facil.ru_facil.repositories.HistoricoPontosRepository;
import com.ru.facil.ru_facil.repositories.PontuacaoUsuarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PontuacaoService {

    private final PontuacaoUsuarioRepository pontuacaoUsuarioRepository;
    private final HistoricoPontosRepository historicoPontosRepository;

    private static final int PONTOS_POR_FICHA = 2;
    private static final int PONTOS_POR_AVALIACAO = 5;

    public PontuacaoService(PontuacaoUsuarioRepository pontuacaoUsuarioRepository,
                            HistoricoPontosRepository historicoPontosRepository) {
        this.pontuacaoUsuarioRepository = pontuacaoUsuarioRepository;
        this.historicoPontosRepository = historicoPontosRepository;
    }

    @Transactional
    public void registrarCompra(Cliente cliente, int quantidadeFichas, Long compraId) {
        int pontos = quantidadeFichas * PONTOS_POR_FICHA;
        if (pontos <= 0) {
            return;
        }

        PontuacaoUsuario pontuacao = obterOuCriarPontuacao(cliente);
        pontuacao.setTotalPontos(pontuacao.getTotalPontos() + pontos);
        pontuacao = pontuacaoUsuarioRepository.save(pontuacao);

        HistoricoPontos historico = new HistoricoPontos();
        historico.setPontuacaoUsuario(pontuacao);
        historico.setTipoEvento(PontosEventoTipo.COMPRA_FICHA);
        historico.setPontos(pontos);
        historico.setDescricao("Compra de " + quantidadeFichas + " ficha(s). ID da compra: " + compraId);

        historicoPontosRepository.save(historico);
    }

    @Transactional
    public void registrarAvaliacao(Cliente cliente, Long feedbackId) {
        int pontos = PONTOS_POR_AVALIACAO;

        PontuacaoUsuario pontuacao = obterOuCriarPontuacao(cliente);
        pontuacao.setTotalPontos(pontuacao.getTotalPontos() + pontos);
        pontuacao = pontuacaoUsuarioRepository.save(pontuacao);

        HistoricoPontos historico = new HistoricoPontos();
        historico.setPontuacaoUsuario(pontuacao);
        historico.setTipoEvento(PontosEventoTipo.AVALIACAO_REFEICAO);
        historico.setPontos(pontos);
        historico.setDescricao("Avaliação de refeição. ID do feedback: " + feedbackId);

        historicoPontosRepository.save(historico);
    }

    private PontuacaoUsuario obterOuCriarPontuacao(Cliente cliente) {
        return pontuacaoUsuarioRepository.findByClienteId(cliente.getId())
                .orElseGet(() -> {
                    PontuacaoUsuario nova = new PontuacaoUsuario();
                    nova.setCliente(cliente);
                    nova.setTotalPontos(0);
                    return pontuacaoUsuarioRepository.save(nova);
                });
    }
}
