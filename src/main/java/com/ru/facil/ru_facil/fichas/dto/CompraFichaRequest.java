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
) {}