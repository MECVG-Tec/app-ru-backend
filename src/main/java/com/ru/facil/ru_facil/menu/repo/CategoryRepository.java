package com.ru.facil.ru_facil.menu.repo;

import com.ru.facil.ru_facil.menu.domain.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Long> { }
