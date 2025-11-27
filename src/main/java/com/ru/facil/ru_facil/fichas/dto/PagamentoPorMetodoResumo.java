package com.ru.facil.ru_facil.fichas.dto;

import com.ru.facil.ru_facil.enuns.PaymentMethod;

import java.math.BigDecimal;

public record PagamentoPorMetodoResumo(
        PaymentMethod formaPagamento,
        long quantidadeCompras,
        BigDecimal totalGasto
) { }
