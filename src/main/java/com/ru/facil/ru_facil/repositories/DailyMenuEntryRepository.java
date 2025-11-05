package com.ru.facil.ru_facil.repositories;

import com.ru.facil.ru_facil.entities.DailyMenuEntry;
import com.ru.facil.ru_facil.enuns.MealType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface DailyMenuEntryRepository extends JpaRepository<DailyMenuEntry, Long> {
    List<DailyMenuEntry> findByDateAndMealTypeOrderBySlotType(LocalDate date, MealType mealType);
    void deleteByDateAndMealType(LocalDate date, MealType mealType); // <-- add isto
}
