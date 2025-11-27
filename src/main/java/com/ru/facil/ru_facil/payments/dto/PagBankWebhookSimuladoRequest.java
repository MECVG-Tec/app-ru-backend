package com.ru.facil.ru_facil.payments.dto;

import jakarta.validation.constraints.NotBlank;

public record PagBankWebhookSimuladoRequest(

        @NotBlank
        String orderId,

        @NotBlank
        String status

) { }
