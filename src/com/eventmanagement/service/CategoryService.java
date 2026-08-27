package com.eventmanagement.service;

import com.eventmanagement.exception.CategoryNotFoundException;
import com.eventmanagement.exception.ValidationException;
import com.eventmanagement.model.Category;

import java.util.List;

public interface CategoryService {

    Category addCategory(String categoryName, String description) throws ValidationException;

    List<Category> getAllCategories();

    void updateCategory(int categoryId, String categoryName, String description)
            throws CategoryNotFoundException, ValidationException;

    void deleteCategory(int categoryId) throws CategoryNotFoundException;

    Category getCategoryById(int categoryId) throws CategoryNotFoundException;
}
