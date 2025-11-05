package com.ru.facil.ru_facil.menu.dto;

import com.ru.facil.ru_facil.menu.domain.SlotType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record DailyMenuCreateRequest(
        @NotNull SlotType slot,
        @NotBlank String title,
        String notes
) { }
