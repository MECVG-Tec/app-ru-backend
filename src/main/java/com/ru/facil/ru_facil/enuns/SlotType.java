package com.ru.facil.ru_facil.enuns;

/** Superset de slots; alguns só se aplicam a almoço (SALADA_COZIDA) ou jantar (SOPA). */
public enum SlotType {
    PRATO_PRINCIPAL_1,
    PRATO_PRINCIPAL_2,
    LEVE_SABOR,
    SELECT,
    VEGETARIANO,
    GUARINCAO,     // guarnição
    SALADA_CRUA,
    SALADA_COZIDA, // almoço
    SOPA,          // jantar
    SOBREMESA,
    SUCO
}
