package com.ru.facil.ru_facil.menu.dto;

import com.ru.facil.ru_facil.entities.Category;

public record CategoryDto(Long id, String name) {
    public static CategoryDto of(Category c) {
        return new CategoryDto(c.getId(), c.getName());
    }
}
