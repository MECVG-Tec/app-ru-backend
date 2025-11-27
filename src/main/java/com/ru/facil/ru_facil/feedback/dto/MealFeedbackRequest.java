package com.ru.facil.ru_facil.feedback.dto;

import com.ru.facil.ru_facil.enuns.MealType;
import jakarta.validation.constraints.*;

import java.time.LocalDate;

public record MealFeedbackRequest(

        @Size(max = 100)
        String nome,

        @NotBlank
        @Email
        @Size(max = 150)
        String email,

        // Data da refeição avaliada (ex.: 2025-11-24)
        @NotNull
        LocalDate date,

        @NotNull
        MealType mealType,

        @NotNull
        @Min(1)
        @Max(5)
        Integer rating,

        @Size(max = 2000)
        String comentario
) { }
