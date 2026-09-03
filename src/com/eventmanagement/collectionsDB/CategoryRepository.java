package com.eventmanagement.collectionsDB;

import com.eventmanagement.model.Category;

import java.util.*;

public class CategoryRepository {

    private final Map<Integer, Category> categoryStore = new HashMap<>();

    public Category save(Category category) {
        categoryStore.put(category.getCategoryId(), category);
        return category;
    }

    public Optional<Category> findById(int categoryId) {
        return Optional.ofNullable(categoryStore.get(categoryId));
    }

    public List<Category> findAll() {
        return categoryStore.values().stream().
                sorted(Comparator.comparingInt(Category::getCategoryId)).toList();
    }

    public void deleteById(int categoryId) {
        categoryStore.remove(categoryId);
    }

    public boolean existsById(int categoryId) {
        return categoryStore.containsKey(categoryId);
    }
}
