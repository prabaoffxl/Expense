package com.expensejar.service;

import com.expensejar.dto.BudgetRequestDTO;
import com.expensejar.dto.BudgetResponseDTO;

import java.util.List;

public interface BudgetService {
    /**
     * Create or update monthly budget for a user and category.
     */
    BudgetResponseDTO setBudget(BudgetRequestDTO requestDTO);

    /**
     * Get budget by ID.
     */
    BudgetResponseDTO getBudgetById(Long id);

    /**
     * Get budgets for a specific user.
     */
    List<BudgetResponseDTO> getBudgetsByUserId(Long userId);

    /**
     * Get all budgets.
     */
    List<BudgetResponseDTO> getAllBudgets();

    /**
     * Delete budget by ID.
     */
    void deleteBudget(Long id);
}
