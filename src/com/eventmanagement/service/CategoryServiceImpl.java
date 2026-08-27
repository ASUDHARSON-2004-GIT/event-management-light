package com.eventmanagement.service;

import com.eventmanagement.dao.CategoryRepository;
import com.eventmanagement.exception.CategoryNotFoundException;
import com.eventmanagement.exception.ValidationException;
import com.eventmanagement.model.Category;
import com.eventmanagement.util.IdGenerator;
import com.eventmanagement.util.ValidationUtil;

import java.util.List;

public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final IdGenerator categoryIdGenerator;

    public CategoryServiceImpl(CategoryRepository categoryRepository, IdGenerator categoryIdGenerator) {
        this.categoryRepository = categoryRepository;
        this.categoryIdGenerator = categoryIdGenerator;
    }

    @Override
    public Category addCategory(String categoryName, String description) throws ValidationException {
        if (ValidationUtil.isEmpty(categoryName)) {
            throw new ValidationException("Category name cannot be empty.");
        }

        Category category = new Category(categoryIdGenerator.nextId(), categoryName, description);
        categoryRepository.save(category);
        return category;
    }

    @Override
    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    @Override
    public void updateCategory(int categoryId, String categoryName, String description)
            throws CategoryNotFoundException, ValidationException {

        Category category = getCategoryById(categoryId);

        if (ValidationUtil.isEmpty(categoryName)) {
            throw new ValidationException("Category name cannot be empty.");
        }

        category.setCategoryName(categoryName);
        category.setDescription(description);
        categoryRepository.save(category);
    }

    @Override
    public void deleteCategory(int categoryId) throws CategoryNotFoundException {
        if (!categoryRepository.existsById(categoryId)) {
            throw new CategoryNotFoundException("No category found with id " + categoryId);
        }
        categoryRepository.deleteById(categoryId);
    }

    @Override
    public Category getCategoryById(int categoryId) throws CategoryNotFoundException {
        return categoryRepository.findById(categoryId)
                .orElseThrow(() -> new CategoryNotFoundException("No category found with id " + categoryId));
    }
}
