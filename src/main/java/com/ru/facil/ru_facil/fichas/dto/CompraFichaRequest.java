package com.ru.facil.ru_facil.fichas.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CompraFichaRequest(

        @NotBlank
        String email,

        @NotNull
        @Min(1)
        Integer quantidade

) { }
