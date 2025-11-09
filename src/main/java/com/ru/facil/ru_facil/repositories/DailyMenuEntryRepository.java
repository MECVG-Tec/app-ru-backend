package com.ru.facil.ru_facil.repositories;

import com.ru.facil.ru_facil.entities.DailyMenuEntry;
import com.ru.facil.ru_facil.enuns.MealType;
import com.ru.facil.ru_facil.enuns.SlotType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface DailyMenuEntryRepository extends JpaRepository<DailyMenuEntry, Long> {

    // já existente
    List<DailyMenuEntry> findByDateAndMealTypeOrderBySlotType(LocalDate date, MealType mealType);

    // usado pelo POST (upsert de um slot)
    Optional<DailyMenuEntry> findByDateAndMealTypeAndSlotType(LocalDate date, MealType mealType, SlotType slotType);

    // usado pelo PUT (apagar tudo do dia/refeição antes de regravar)
    void deleteByDateAndMealType(LocalDate date, MealType mealType);

    // usado pelo DELETE (apagar só um slot)
    void deleteByDateAndMealTypeAndSlotType(LocalDate date, MealType mealType, SlotType slotType);

    // usado pelo GET de intervalo (semana, etc.)
    List<DailyMenuEntry> findByDateBetweenAndMealTypeOrderByDateAscSlotTypeAsc(
            LocalDate start, LocalDate end, MealType mealType
    );
}
