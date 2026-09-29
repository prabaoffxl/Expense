package com.expensejar.service;

import com.expensejar.dto.CategoryRequestDTO;
import com.expensejar.dto.CategoryResponseDTO;
import com.expensejar.entity.Category;
import com.expensejar.exception.DuplicateResourceException;
import com.expensejar.exception.ResourceNotFoundException;
import com.expensejar.repository.CategoryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;

    public CategoryServiceImpl(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    /**
     * Create a new category ensuring name uniqueness.
     */
    @Override
    public CategoryResponseDTO createCategory(CategoryRequestDTO requestDTO) {
        String name = requestDTO.getName().trim();
        if (categoryRepository.existsByName(name)) {
            throw new DuplicateResourceException("Category with name '" + name + "' already exists");
        }

        Category category = Category.builder()
                .name(name)
                .build();

        Category saved = categoryRepository.save(category);
        return mapToDTO(saved);
    }

    /**
     * Get category by ID.
     */
    @Override
    @Transactional(readOnly = true)
    public CategoryResponseDTO getCategoryById(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + id));
        return mapToDTO(category);
    }

    /**
     * Get all categories.
     */
    @Override
    @Transactional(readOnly = true)
    public List<CategoryResponseDTO> getAllCategories() {
        return categoryRepository.findAll().stream()
                .map(this::mapToDTO)
                .toList();
    }

    /**
     * Update existing category name.
     */
    @Override
    public CategoryResponseDTO updateCategory(Long id, CategoryRequestDTO requestDTO) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + id));

        String newName = requestDTO.getName().trim();
        if (categoryRepository.existsByNameAndIdNot(newName, id)) {
            throw new DuplicateResourceException("Category with name '" + newName + "' already exists");
        }

        category.setName(newName);
        Category updated = categoryRepository.save(category);
        return mapToDTO(updated);
    }

    /**
     * Delete category by ID.
     */
    @Override
    public void deleteCategory(Long id) {
        if (!categoryRepository.existsById(id)) {
            throw new ResourceNotFoundException("Category not found with id: " + id);
        }
        categoryRepository.deleteById(id);
    }

    private CategoryResponseDTO mapToDTO(Category category) {
        return CategoryResponseDTO.builder()
                .id(category.getId())
                .name(category.getName())
                .build();
    }
}
