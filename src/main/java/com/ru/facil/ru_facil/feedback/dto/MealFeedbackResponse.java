package com.ru.facil.ru_facil.feedback.dto;

import com.ru.facil.ru_facil.entities.MealFeedback;
import com.ru.facil.ru_facil.enuns.MealType;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record MealFeedbackResponse(
        Long id,
        String nome,
        String email,
        LocalDate date,
        MealType mealType,
        Integer rating,
        String comentario,
        LocalDateTime criadoEm
) {

    public static MealFeedbackResponse of(MealFeedback f) {
        return new MealFeedbackResponse(
                f.getId(),
                f.getNome(),
                f.getEmail(),
                f.getDate(),
                f.getMealType(),
                f.getRating(),
                f.getComentario(),
                f.getCriadoEm()
        );
    }
}
