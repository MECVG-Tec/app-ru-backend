package com.ru.facil.ru_facil.payments.dto;

public record PagBankPixResponse(
        String orderId,
        String qrCodeId,
        String qrCodeText,
        String qrCodeImageUrl
) { }
