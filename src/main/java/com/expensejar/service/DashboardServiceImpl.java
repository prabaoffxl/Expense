package com.expensejar.service;

import com.expensejar.dto.DashboardDTO;
import com.expensejar.repository.BudgetRepository;
import com.expensejar.repository.CategoryRepository;
import com.expensejar.repository.ExpenseRepository;
import com.expensejar.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;

@Service
@Transactional(readOnly = true)
public class DashboardServiceImpl implements DashboardService {

    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final BudgetRepository budgetRepository;
    private final ExpenseRepository expenseRepository;

    public DashboardServiceImpl(UserRepository userRepository,
                                CategoryRepository categoryRepository,
                                BudgetRepository budgetRepository,
                                ExpenseRepository expenseRepository) {
        this.userRepository = userRepository;
        this.categoryRepository = categoryRepository;
        this.budgetRepository = budgetRepository;
        this.expenseRepository = expenseRepository;
    }

    @Override
    public DashboardDTO getDashboardStats() {
        long usersCount = userRepository.count();
        long categoriesCount = categoryRepository.count();
        long expensesCount = expenseRepository.count();
        long budgetsCount = budgetRepository.count();

        BigDecimal allTimeTotal = expenseRepository.sumAllExpenseAmount();

        YearMonth currentMonth = YearMonth.now();
        LocalDate start = currentMonth.atDay(1);
        LocalDate end = currentMonth.atEndOfMonth();

        BigDecimal currentMonthTotal = expenseRepository.sumExpenseAmountInDateRange(start, end);

        return DashboardDTO.builder()
                .totalUsers(usersCount)
                .totalCategories(categoriesCount)
                .totalExpenses(expensesCount)
                .totalBudgets(budgetsCount)
                .totalExpenseAmountAllTime(allTimeTotal)
                .currentMonthTotalExpenseAmount(currentMonthTotal)
                .build();
    }
}
