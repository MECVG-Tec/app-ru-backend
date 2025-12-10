package com.ru.facil.ru_facil.fichas.dto;

public record SaldoResponse(
    String email,
    Integer saldoAlmoco,
    Integer saldoJantar
) {}