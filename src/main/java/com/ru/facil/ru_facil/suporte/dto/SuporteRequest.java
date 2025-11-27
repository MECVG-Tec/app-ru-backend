package com.ru.facil.ru_facil.suporte.dto;

import com.ru.facil.ru_facil.enuns.SupportCategory;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record SuporteRequest(

        @Size(max = 100)
        String nome,

        @NotBlank
        @Email
        @Size(max = 150)
        String email,

        @NotNull
        SupportCategory categoria,

        @NotBlank
        @Size(max = 150)
        String assunto,

        @NotBlank
        @Size(max = 5000)
        String mensagem
) { }
