package com.expensejar.service;

import com.expensejar.dto.CategoryRequestDTO;
import com.expensejar.dto.CategoryResponseDTO;

import java.util.List;

public interface CategoryService {
    /**
     * Create a new category.
     */
    CategoryResponseDTO createCategory(CategoryRequestDTO requestDTO);

    /**
     * Get category by ID.
     */
    CategoryResponseDTO getCategoryById(Long id);

    /**
     * Get all categories.
     */
    List<CategoryResponseDTO> getAllCategories();

    /**
     * Update an existing category.
     */
    CategoryResponseDTO updateCategory(Long id, CategoryRequestDTO requestDTO);

    /**
     * Delete category by ID.
     */
    void deleteCategory(Long id);
}
