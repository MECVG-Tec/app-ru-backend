package com.ru.facil.ru_facil.acessibilidade.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record AcessibilidadeRequest(

        @NotBlank
        @Email
        String email,

        @NotNull
        Boolean prefereAltoContraste,

        @NotNull
        Boolean prefereLinguagemSimples,

        @NotNull
        Boolean prefereFonteGrande
) { }
