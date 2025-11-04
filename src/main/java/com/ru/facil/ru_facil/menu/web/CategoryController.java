package com.ru.facil.ru_facil.menu.web;

import com.ru.facil.ru_facil.menu.dto.CategoryDto;
import com.ru.facil.ru_facil.menu.repo.CategoryRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/categories")
public class CategoryController {
    private final CategoryRepository repo;

    public CategoryController(CategoryRepository repo) {
        this.repo = repo;
    }

    @GetMapping
    public List<CategoryDto> list() {
        return repo.findAll().stream().map(CategoryDto::of).toList();
    }
}
