package com.eventmanagement.dao;

import com.eventmanagement.model.Category;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class CategoryRepository {

    private final Map<Integer, Category> categoryStore = new LinkedHashMap<>();

    public Category save(Category category) {
        categoryStore.put(category.getCategoryId(), category);
        return category;
    }

    public Optional<Category> findById(int categoryId) {
        return Optional.ofNullable(categoryStore.get(categoryId));
    }

    public List<Category> findAll() {
        return new ArrayList<>(categoryStore.values());
    }

    public void deleteById(int categoryId) {
        categoryStore.remove(categoryId);
    }

    public boolean existsById(int categoryId) {
        return categoryStore.containsKey(categoryId);
    }
}
