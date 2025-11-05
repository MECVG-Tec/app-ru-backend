package com.ru.facil.ru_facil.entities;

import com.ru.facil.ru_facil.enuns.MealType;
import com.ru.facil.ru_facil.enuns.SlotType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(
    name = "daily_menu_entries",
    uniqueConstraints = @UniqueConstraint(
        name = "uk_menu_date_meal_slot",
        columnNames = {"date", "meal_type", "slot_type"}
    )
)
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class DailyMenuEntry {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDate date;                  // ex.: 2025-11-04

    @Enumerated(EnumType.STRING)
    @Column(name = "meal_type", nullable = false, length = 16)
    private MealType mealType;               // ALMOCO ou JANTAR

    @Enumerated(EnumType.STRING)
    @Column(name = "slot_type", nullable = false, length = 32)
    private SlotType slotType;               // PRATO_PRINCIPAL_1, ...

    @Column(nullable = false, length = 200)
    private String title;                    // texto da célula (ex.: "Frango ao molho...")

    @Column(length = 200)
    private String notes;                    // ex.: "contém leite" (opcional)
}
