package com.ru.facil.ru_facil.fichas.dto;

import com.ru.facil.ru_facil.enuns.PaymentMethod;
import jakarta.validation.constraints.*;

public record CompraFichaRequest(

        @NotBlank
        @Email
        String email,

        @NotNull
        @Min(1)
        Integer quantidade,

        @NotNull
        PaymentMethod formaPagamento,

        /**
         * Token de pagamento fornecido pelo provider (ex.: PagBank, Pagar.me).
         * No mundo real, esse token é gerado no frontend a partir dos dados do cartão/carteira
         * e NUNCA expomos o número do cartão diretamente para o backend.
         *
         * Obrigatório quando formaPagamento for CARTAO_CREDITO, CARTAO_DEBITO ou CARTEIRA_DIGITAL.
         */
        String paymentToken,

        /**
         * Bandeira do cartão (apenas para exibição e registro não sensível).
         * Ex.: VISA, MASTERCARD, ELO...
         */
        String cardBrand,

        /**
         * Últimos 4 dígitos do cartão (registro não sensível).
         * Ex.: "1234"
         */
        String cardLast4

) { }
