package com.ru.facil.ru_facil.menu.web;

import com.ru.facil.ru_facil.menu.domain.DailyMenuEntry;
import com.ru.facil.ru_facil.menu.domain.MealType;
import com.ru.facil.ru_facil.menu.dto.DailyMenuCreateRequest;
import com.ru.facil.ru_facil.menu.dto.DailyMenuResponse;
import com.ru.facil.ru_facil.menu.dto.DailySlotDto;
import com.ru.facil.ru_facil.menu.repo.DailyMenuEntryRepository;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Cardápio diário", description = "Consulta e manutenção do cardápio por dia/refeição")
@RestController
@RequestMapping("/api/v1/menu")
public class DailyMenuController {

    private final DailyMenuEntryRepository repo;

    public DailyMenuController(DailyMenuEntryRepository repo) {
        this.repo = repo;
    }

    /** GET /api/v1/menu/2025-11-04?meal=ALMOCO */
    @Operation(summary = "Consulta o cardápio do dia por refeição (ALMOCO|JANTAR)")
    @GetMapping("/{date}")
    public DailyMenuResponse getByDate(@PathVariable String date, @RequestParam MealType meal) {
        var d = LocalDate.parse(date);
        var entries = repo.findByDateAndMealTypeOrderBySlotType(d, meal)
                          .stream().map(DailySlotDto::of).toList();
        return new DailyMenuResponse(d, meal, entries);
    }

    /** PUT /api/v1/menu/{date}?meal=... (sobrescreve todos os slots do dia/refeição) */
    @Operation(summary = "Define/atualiza todos os slots do dia/refeição (sobrescreve)")
    @PutMapping("/{date}")
    @Transactional
    public DailyMenuResponse upsert(@PathVariable String date,
                                    @RequestParam MealType meal,
                                    @RequestBody @Valid com.ru.facil.ru_facil.menu.dto.DailyMenuUpsertRequest body) {
        var d = LocalDate.parse(date);
        repo.deleteByDateAndMealType(d, meal);
        for (var it : body.items()) {
            var e = DailyMenuEntry.builder()
                    .date(d).mealType(meal).slotType(it.slot())
                    .title(it.title()).notes(it.notes())
                    .build();
            repo.save(e);
        }
        var entries = repo.findByDateAndMealTypeOrderBySlotType(d, meal)
                          .stream().map(DailySlotDto::of).toList();
        return new DailyMenuResponse(d, meal, entries);
    }

    /** POST /api/v1/menu/{date}?meal=...  (cadastra/atualiza UM slot) */
    @Operation(summary = "Cadastra/atualiza um slot do dia/refeição")
    @PostMapping("/{date}")
    @Transactional
    public DailyMenuResponse createOne(@PathVariable String date,
                                       @RequestParam MealType meal,
            @RequestBody @Valid DailyMenuCreateRequest body) {
        var d = LocalDate.parse(date);

        // se já existir aquele (data, refeição, slot), atualiza; senão cria
        var existing = repo.findByDateAndMealTypeAndSlotType(d, meal, body.slot());
        if (existing.isPresent()) {
            var e = existing.get();
            e.setTitle(body.title());
            e.setNotes(body.notes());
            repo.save(e);
        } else {
            var e = DailyMenuEntry.builder()
                    .date(d).mealType(meal).slotType(body.slot())
                    .title(body.title()).notes(body.notes())
                    .build();
            repo.save(e);
        }

        // retorna o cardápio completo do dia/refeição
        var entries = repo.findByDateAndMealTypeOrderBySlotType(d, meal)
                .stream().map(DailySlotDto::of).toList();
        return new DailyMenuResponse(d, meal, entries);
    }
    
    @DeleteMapping("/{date}")
    @Transactional
    public DailyMenuResponse deleteOne(@PathVariable String date,
                                   @RequestParam MealType meal,
                                   @RequestParam("slot") com.ru.facil.ru_facil.menu.domain.SlotType slot) {
    var d = java.time.LocalDate.parse(date);
    repo.deleteByDateAndMealTypeAndSlotType(d, meal, slot);

    var entries = repo.findByDateAndMealTypeOrderBySlotType(d, meal)
                      .stream().map(DailySlotDto::of).toList();
    return new DailyMenuResponse(d, meal, entries);
}

}
