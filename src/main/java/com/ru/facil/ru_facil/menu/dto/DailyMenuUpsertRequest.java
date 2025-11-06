package com.ru.facil.ru_facil.menu.dto;

import com.ru.facil.ru_facil.enuns.SlotType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record DailyMenuUpsertRequest(
        @NotNull List<Item> items
) {
    public record Item(
            @NotNull SlotType slot,
            @NotBlank String title,
            String notes
    ) {}
}
