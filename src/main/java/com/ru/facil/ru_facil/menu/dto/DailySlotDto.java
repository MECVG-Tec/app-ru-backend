package com.ru.facil.ru_facil.menu.dto;

import com.ru.facil.ru_facil.entities.DailyMenuEntry;
import com.ru.facil.ru_facil.enuns.SlotType;

public record DailySlotDto(SlotType slot, String title, String notes) {
    public static DailySlotDto of(DailyMenuEntry e) {
        return new DailySlotDto(e.getSlotType(), e.getTitle(), e.getNotes());
    }
}
