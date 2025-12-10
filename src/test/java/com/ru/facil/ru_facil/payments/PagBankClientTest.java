package com.ru.facil.ru_facil.payments;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

class PagBankClientTest {

    @Test
    @DisplayName("Deve autorizar pagamento com valor <= 200 e token normal")
    void autorizarPagamentoCartao_sucesso() {
        PagBankClient client = new PagBankClient();

        var resultado = client.autorizarPagamentoCartao(
                "TOKEN-OK",
                BigDecimal.valueOf(150),
                "REF-123"
        );

        assertThat(resultado.autorizado()).isTrue();
        assertThat(resultado.transactionId()).isNotNull();
    }

    @Test
    @DisplayName("Deve recusar pagamento com valor maior que 200")
    void autorizarPagamentoCartao_recusaPorValorAlto() {
        PagBankClient client = new PagBankClient();

        var resultado = client.autorizarPagamentoCartao(
                "TOKEN-OK",
                BigDecimal.valueOf(250),
                "REF-ALTA"
        );

        assertThat(resultado.autorizado()).isFalse();
        assertThat(resultado.transactionId()).isNotNull();
    }

    @Test
    @DisplayName("Deve recusar pagamento quando token começa com FAIL")
    void autorizarPagamentoCartao_recusaPorTokenFail() {
        PagBankClient client = new PagBankClient();

        var resultado = client.autorizarPagamentoCartao(
                "FAIL-123",
                BigDecimal.valueOf(100),
                "REF-FAIL"
        );

        assertThat(resultado.autorizado()).isFalse();
        assertThat(resultado.transactionId()).isNotNull();
    }
}
