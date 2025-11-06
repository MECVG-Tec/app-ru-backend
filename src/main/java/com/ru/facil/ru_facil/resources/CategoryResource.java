package com.ru.facil.ru_facil.resources;

import com.ru.facil.ru_facil.menu.dto.CategoryDto;
import com.ru.facil.ru_facil.repositories.CategoryRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/categories")
public class CategoryResource {
    private final CategoryRepository repo;

    public CategoryResource(CategoryRepository repo) {
        this.repo = repo;
    }

    @GetMapping
    public List<CategoryDto> list() {
        return repo.findAll().stream().map(CategoryDto::of).toList();
    }
}
