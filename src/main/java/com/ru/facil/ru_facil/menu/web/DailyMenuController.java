package com.ru.facil.ru_facil.menu.web;

import com.ru.facil.ru_facil.menu.domain.DailyMenuEntry;
import com.ru.facil.ru_facil.menu.domain.MealType;
import com.ru.facil.ru_facil.menu.dto.DailyMenuResponse;
import com.ru.facil.ru_facil.menu.dto.DailyMenuUpsertRequest;
import com.ru.facil.ru_facil.menu.dto.DailySlotDto;
import com.ru.facil.ru_facil.menu.repo.DailyMenuEntryRepository;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/v1/menu")
public class DailyMenuController {

    private final DailyMenuEntryRepository repo;

    public DailyMenuController(DailyMenuEntryRepository repo) {
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

    /** PUT /api/v1/menu/2025-11-04?meal=ALMOCO  (sobrescreve tudo daquela data/refeição) */
    @PutMapping("/{date}")
    @Transactional
    public DailyMenuResponse upsert(
            @PathVariable String date,
            @RequestParam MealType meal,
            @RequestBody @Valid DailyMenuUpsertRequest body
    ) {
        var d = LocalDate.parse(date);
        repo.deleteByDateAndMealType(d, meal); // limpa o do dia/refeição

        // grava o novo conjunto
        for (var it : body.items()) {
            var e = DailyMenuEntry.builder()
                    .date(d)
                    .mealType(meal)
                    .slotType(it.slot())
                    .title(it.title())
                    .notes(it.notes())
                    .build();
            repo.save(e);
        }

        var entries = repo.findByDateAndMealTypeOrderBySlotType(d, meal)
                          .stream().map(DailySlotDto::of).toList();
        return new DailyMenuResponse(d, meal, entries);
    }
}
