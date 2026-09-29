package com.expensejar.service;

import com.expensejar.dto.BudgetRequestDTO;
import com.expensejar.dto.BudgetResponseDTO;
import com.expensejar.entity.Budget;
import com.expensejar.entity.Category;
import com.expensejar.entity.User;
import com.expensejar.exception.InvalidExpenseException;
import com.expensejar.exception.ResourceNotFoundException;
import com.expensejar.repository.BudgetRepository;
import com.expensejar.repository.CategoryRepository;
import com.expensejar.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class BudgetServiceImpl implements BudgetService {

    private final BudgetRepository budgetRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;

    public BudgetServiceImpl(BudgetRepository budgetRepository,
                             UserRepository userRepository,
                             CategoryRepository categoryRepository) {
        this.budgetRepository = budgetRepository;
        this.userRepository = userRepository;
        this.categoryRepository = categoryRepository;
    }

    /**
     * Create or update budget for user and category with positive limit validation.
     */
    @Override
    public BudgetResponseDTO setBudget(BudgetRequestDTO requestDTO) {
        validateBudgetLimit(requestDTO.getMonthlyLimit());

        User user = userRepository.findById(requestDTO.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + requestDTO.getUserId()));

        Category category = categoryRepository.findById(requestDTO.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + requestDTO.getCategoryId()));

        Optional<Budget> existingBudgetOpt = budgetRepository.findByUserIdAndCategoryId(user.getId(), category.getId());

        Budget budget;
        if (existingBudgetOpt.isPresent()) {
            budget = existingBudgetOpt.get();
            budget.setMonthlyLimit(requestDTO.getMonthlyLimit());
        } else {
            budget = Budget.builder()
                    .user(user)
                    .category(category)
                    .monthlyLimit(requestDTO.getMonthlyLimit())
                    .build();
        }

        Budget saved = budgetRepository.save(budget);
        return mapToDTO(saved);
    }

    /**
     * Get budget by ID.
     */
    @Override
    @Transactional(readOnly = true)
    public BudgetResponseDTO getBudgetById(Long id) {
        Budget budget = budgetRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Budget not found with id: " + id));
        return mapToDTO(budget);
    }

    /**
     * Get all budgets for a given user.
     */
    @Override
    @Transactional(readOnly = true)
    public List<BudgetResponseDTO> getBudgetsByUserId(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException("User not found with id: " + userId);
        }
        return budgetRepository.findByUserId(userId).stream()
                .map(this::mapToDTO)
                .toList();
    }

    /**
     * Get all budgets across all users.
     */
    @Override
    @Transactional(readOnly = true)
    public List<BudgetResponseDTO> getAllBudgets() {
        return budgetRepository.findAll().stream()
                .map(this::mapToDTO)
                .toList();
    }

    /**
     * Delete budget by ID.
     */
    @Override
    public void deleteBudget(Long id) {
        if (!budgetRepository.existsById(id)) {
            throw new ResourceNotFoundException("Budget not found with id: " + id);
        }
        budgetRepository.deleteById(id);
    }

    private void validateBudgetLimit(BigDecimal limit) {
        if (limit == null || limit.compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidExpenseException("Monthly budget limit must be positive (> 0)");
        }
    }

    private BudgetResponseDTO mapToDTO(Budget budget) {
        return BudgetResponseDTO.builder()
                .id(budget.getId())
                .userId(budget.getUser().getId())
                .userName(budget.getUser().getName())
                .categoryId(budget.getCategory().getId())
                .categoryName(budget.getCategory().getName())
                .monthlyLimit(budget.getMonthlyLimit())
                .build();
    }
}
