package com.ru.facil.ru_facil.repositories;

import com.ru.facil.ru_facil.entities.MealFeedback;
import com.ru.facil.ru_facil.enuns.MealType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface MealFeedbackRepository extends JpaRepository<MealFeedback, Long> {

    List<MealFeedback> findByEmailOrderByDateDesc(String email);

    List<MealFeedback> findByDateAndMealTypeOrderByCriadoEmDesc(LocalDate date, MealType mealType);
}
