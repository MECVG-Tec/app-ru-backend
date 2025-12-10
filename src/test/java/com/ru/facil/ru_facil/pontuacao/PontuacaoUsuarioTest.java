package com.ru.facil.ru_facil.pontuacao;

import com.ru.facil.ru_facil.entities.PontuacaoUsuario;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class PontuacaoUsuarioTest {

    @Test
    void prePersistInicializaCamposCorretamente() {
        PontuacaoUsuario pontuacao = new PontuacaoUsuario();

        pontuacao.prePersist();

        assertEquals(0, pontuacao.getTotalPontos(), "totalPontos deve ser 0 se nulo");
        assertNotNull(pontuacao.getCriadoEm(), "criadoEm deve ser inicializado");
        assertNotNull(pontuacao.getAtualizadoEm(), "atualizadoEm deve ser inicializado");
        assertEquals(
                pontuacao.getCriadoEm(),
                pontuacao.getAtualizadoEm(),
                "Datas devem ser iguais na criação"
        );
    }

    @Test
    void preUpdateAtualizaDataCorretamente() throws InterruptedException {
        PontuacaoUsuario pontuacao = new PontuacaoUsuario();

        pontuacao.prePersist();
        LocalDateTime antes = pontuacao.getAtualizadoEm();

        Thread.sleep(1); // garante diferença mínima de tempo
        pontuacao.preUpdate();

        assertNotEquals(
                antes,
                pontuacao.getAtualizadoEm(),
                "atualizadoEm deve ser alterado no preUpdate"
        );
    }
}
