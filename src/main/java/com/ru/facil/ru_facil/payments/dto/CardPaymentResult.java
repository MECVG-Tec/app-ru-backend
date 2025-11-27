package com.ru.facil.ru_facil.payments.dto;

public record CardPaymentResult(
        String transactionId,
        boolean autorizado
) { }
