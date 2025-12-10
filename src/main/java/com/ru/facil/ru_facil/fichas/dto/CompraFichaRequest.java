package com.ru.facil.ru_facil.fichas.dto;

import com.ru.facil.ru_facil.enuns.PaymentMethod;

public record CompraFichaRequest(
    String email,
    Integer quantidadeAlmoco, 
    Integer quantidadeJantar,
    PaymentMethod formaPagamento,
    String paymentToken,
    String cardBrand,
    String cardLast4
) {
    public CompraFichaRequest(
        String email,
        Integer quantidade,
        PaymentMethod formaPagamento,
        String paymentToken,
        String cardBrand,
        String cardLast4
    ) {
        this(email, quantidade, 0, formaPagamento, paymentToken, cardBrand, cardLast4);
    }
}