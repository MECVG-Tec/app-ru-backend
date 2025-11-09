package com.ru.facil.ru_facil.resources;

import com.ru.facil.ru_facil.entities.DailyMenuEntry;
import com.ru.facil.ru_facil.enuns.MealType;
import com.ru.facil.ru_facil.enuns.SlotType; // <— enum da ENTIDADE
import com.ru.facil.ru_facil.menu.dto.DailyMenuResponse;
import com.ru.facil.ru_facil.menu.dto.DailyMenuUpsertRequest;
import com.ru.facil.ru_facil.menu.dto.DailySlotDto;
import com.ru.facil.ru_facil.repositories.DailyMenuEntryRepository;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/v1/menu")
public class DailyMenuResource {

    private final DailyMenuEntryRepository repo;

    public DailyMenuResource(DailyMenuEntryRepository repo) {
        this.repo = repo;
    }

    /** GET /api/v1/menu/2025-11-04?meal=ALMOCO */
    @GetMapping("/{date}")
    public DailyMenuResponse getByDate(@PathVariable String date, @RequestParam MealType meal) {
        var d = LocalDate.parse(date);
        var entries = repo.findByDateAndMealTypeOrderBySlotType(d, meal)
                .stream().map(DailySlotDto::of).toList();
        return new DailyMenuResponse(d, meal, entries);
    }

    @PutMapping("/{date}")
    @Transactional
    public DailyMenuResponse upsert(
            @PathVariable String date,
            @RequestParam com.ru.facil.ru_facil.enuns.MealType meal,
            @RequestBody @Valid com.ru.facil.ru_facil.menu.dto.DailyMenuUpsertRequest body) {
        var d = java.time.LocalDate.parse(date);

        repo.deleteByDateAndMealType(d, meal);
        repo.flush(); // garante DELETE antes dos INSERTs

        for (var it : body.items()) {
            // it.slot() é com.ru.facil.ru_facil.menu.domain.SlotType
            var dtoSlot = it.slot();
            // converte para com.ru.facil.ru_facil.enuns.SlotType usando o mesmo nome do enum
            var entitySlot = com.ru.facil.ru_facil.enuns.SlotType.valueOf(dtoSlot.name());

            var e = com.ru.facil.ru_facil.entities.DailyMenuEntry.builder()
                    .date(d)
                    .mealType(meal)
                    .slotType(entitySlot)
                    .title(it.title())
                    .notes(it.notes())
                    .build();
            repo.save(e);
        }

        var entries = repo.findByDateAndMealTypeOrderBySlotType(d, meal)
                .stream().map(com.ru.facil.ru_facil.menu.dto.DailySlotDto::of).toList();

        return new com.ru.facil.ru_facil.menu.dto.DailyMenuResponse(d, meal, entries);

    }
}
