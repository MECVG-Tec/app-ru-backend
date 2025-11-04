package com.ru.facil.ru_facil.menu.dto;

import com.ru.facil.ru_facil.menu.domain.MealType;

import java.time.LocalDate;
import java.util.List;

public record DailyMenuResponse(LocalDate date, MealType meal, List<DailySlotDto> slots) { }
