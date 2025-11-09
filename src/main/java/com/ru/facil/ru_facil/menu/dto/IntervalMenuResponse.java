package com.ru.facil.ru_facil.menu.dto;

import com.ru.facil.ru_facil.enuns.MealType; // <<-- trocar domain por enuns
import java.util.List;

public record IntervalMenuResponse(
        MealType meal,
        List<DailyMenuResponse> days
) {}
