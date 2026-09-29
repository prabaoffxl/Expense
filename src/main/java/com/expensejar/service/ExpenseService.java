package com.expensejar.service;

import com.expensejar.dto.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface ExpenseService {
    /**
     * Add a new expense and return response with budget alert status.
     */
    ExpenseResponseDTO addExpense(ExpenseRequestDTO requestDTO);

    /**
     * Get expense by ID.
     */
    ExpenseResponseDTO getExpenseById(Long id);

    /**
     * Search/filter expenses with pagination.
     */
    Page<ExpenseResponseDTO> getExpenses(Long userId, Long categoryId, LocalDate startDate, LocalDate endDate,
                                         BigDecimal minAmount, BigDecimal maxAmount, Pageable pageable);

    /**
     * Update an existing expense and recalculate budget alert.
     */
    ExpenseResponseDTO updateExpense(Long id, ExpenseRequestDTO requestDTO);

    /**
     * Delete expense by ID.
     */
    void deleteExpense(Long id);

    /**
     * Get current month summary per category for a user.
     */
    MonthlySummaryDTO getCurrentMonthSummary(Long userId);

    /**
     * Get active budget alerts for categories crossing threshold (>= 90%) for the current month.
     */
    List<BudgetAlertDTO> getBudgetAlerts(Long userId);

    /**
     * Get month-over-month spending trend for the last N months.
     */
    OverallTrendDTO getMonthOverMonthTrend(Long userId, int months);
}
