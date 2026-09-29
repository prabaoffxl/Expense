package com.expensejar.service;

import com.expensejar.dto.*;
import com.expensejar.entity.Budget;
import com.expensejar.entity.Category;
import com.expensejar.entity.Expense;
import com.expensejar.entity.User;
import com.expensejar.exception.InvalidExpenseException;
import com.expensejar.exception.ResourceNotFoundException;
import com.expensejar.repository.BudgetRepository;
import com.expensejar.repository.CategoryRepository;
import com.expensejar.repository.ExpenseRepository;
import com.expensejar.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ExpenseServiceImpl implements ExpenseService {

    private final ExpenseRepository expenseRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final BudgetRepository budgetRepository;

    @Value("${expensejar.budget.alert-threshold-percent:90.0}")
    private double alertThresholdPercent;

    public ExpenseServiceImpl(ExpenseRepository expenseRepository,
                              UserRepository userRepository,
                              CategoryRepository categoryRepository,
                              BudgetRepository budgetRepository) {
        this.expenseRepository = expenseRepository;
        this.userRepository = userRepository;
        this.categoryRepository = categoryRepository;
        this.budgetRepository = budgetRepository;
    }

    /**
     * Add expense with mandatory service-layer business rules validation.
     * Enforces budget limit check: rejects expense if proposed total exceeds category budget limit.
     */
    @Override
    public ExpenseResponseDTO addExpense(ExpenseRequestDTO requestDTO) {
        validateExpenseRequest(requestDTO);

        User user = userRepository.findById(requestDTO.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + requestDTO.getUserId()));

        Category category = categoryRepository.findById(requestDTO.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + requestDTO.getCategoryId()));

        // Enforce Budget Limit Rule: Reject entry if spend exceeds monthly limit
        checkBudgetLimit(user.getId(), category, requestDTO.getAmount(), requestDTO.getExpenseDate(), null);

        Expense expense = Expense.builder()
                .user(user)
                .category(category)
                .amount(requestDTO.getAmount())
                .expenseDate(requestDTO.getExpenseDate())
                .description(requestDTO.getDescription() != null ? requestDTO.getDescription().trim() : null)
                .build();

        Expense saved = expenseRepository.save(expense);
        BudgetAlertDTO alert = calculateBudgetAlert(user.getId(), category.getId(), category.getName(), saved.getExpenseDate());

        return mapToDTO(saved, alert);
    }

    /**
     * Get expense by ID.
     */
    @Override
    @Transactional(readOnly = true)
    public ExpenseResponseDTO getExpenseById(Long id) {
        Expense expense = expenseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Expense not found with id: " + id));

        BudgetAlertDTO alert = calculateBudgetAlert(
                expense.getUser().getId(),
                expense.getCategory().getId(),
                expense.getCategory().getName(),
                expense.getExpenseDate()
        );

        return mapToDTO(expense, alert);
    }

    /**
     * Search and filter expenses with dynamic specifications and pagination.
     */
    @Override
    @Transactional(readOnly = true)
    public Page<ExpenseResponseDTO> getExpenses(Long userId, Long categoryId, LocalDate startDate, LocalDate endDate,
                                                BigDecimal minAmount, BigDecimal maxAmount, Pageable pageable) {
        Specification<Expense> spec = Specification.where(null);

        if (userId != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("user").get("id"), userId));
        }
        if (categoryId != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("category").get("id"), categoryId));
        }
        if (startDate != null) {
            spec = spec.and((root, query, cb) -> cb.greaterThanOrEqualTo(root.get("expenseDate"), startDate));
        }
        if (endDate != null) {
            spec = spec.and((root, query, cb) -> cb.lessThanOrEqualTo(root.get("expenseDate"), endDate));
        }
        if (minAmount != null) {
            spec = spec.and((root, query, cb) -> cb.greaterThanOrEqualTo(root.get("amount"), minAmount));
        }
        if (maxAmount != null) {
            spec = spec.and((root, query, cb) -> cb.lessThanOrEqualTo(root.get("amount"), maxAmount));
        }

        Page<Expense> expensePage = expenseRepository.findAll(spec, pageable);

        return expensePage.map(expense -> {
            BudgetAlertDTO alert = calculateBudgetAlert(
                    expense.getUser().getId(),
                    expense.getCategory().getId(),
                    expense.getCategory().getName(),
                    expense.getExpenseDate()
            );
            return mapToDTO(expense, alert);
        });
    }

    /**
     * Update an existing expense and recalculate budget alert. Enforces budget limit check.
     */
    @Override
    public ExpenseResponseDTO updateExpense(Long id, ExpenseRequestDTO requestDTO) {
        validateExpenseRequest(requestDTO);

        Expense expense = expenseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Expense not found with id: " + id));

        User user = userRepository.findById(requestDTO.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + requestDTO.getUserId()));

        Category category = categoryRepository.findById(requestDTO.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + requestDTO.getCategoryId()));

        // Enforce Budget Limit Rule for updates
        checkBudgetLimit(user.getId(), category, requestDTO.getAmount(), requestDTO.getExpenseDate(), id);

        expense.setUser(user);
        expense.setCategory(category);
        expense.setAmount(requestDTO.getAmount());
        expense.setExpenseDate(requestDTO.getExpenseDate());
        expense.setDescription(requestDTO.getDescription() != null ? requestDTO.getDescription().trim() : null);

        Expense updated = expenseRepository.save(expense);
        BudgetAlertDTO alert = calculateBudgetAlert(user.getId(), category.getId(), category.getName(), updated.getExpenseDate());

        return mapToDTO(updated, alert);
    }

    /**
     * Delete expense by ID.
     */
    @Override
    public void deleteExpense(Long id) {
        if (!expenseRepository.existsById(id)) {
            throw new ResourceNotFoundException("Expense not found with id: " + id);
        }
        expenseRepository.deleteById(id);
    }

    /**
     * Get current month summary per category for a user.
     */
    @Override
    @Transactional(readOnly = true)
    public MonthlySummaryDTO getCurrentMonthSummary(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        YearMonth currentMonth = YearMonth.now();
        LocalDate start = currentMonth.atDay(1);
        LocalDate end = currentMonth.atEndOfMonth();

        List<Category> allCategories = categoryRepository.findAll();
        List<Budget> userBudgets = budgetRepository.findByUserId(userId);

        BigDecimal totalSpent = BigDecimal.ZERO;
        BigDecimal totalBudget = BigDecimal.ZERO;

        List<CategorySummaryDTO> summaries = new ArrayList<>();

        for (Category category : allCategories) {
            BigDecimal spent = expenseRepository.sumTotalSpentByUserAndCategoryAndDateRange(userId, category.getId(), start, end);
            totalSpent = totalSpent.add(spent);

            Optional<Budget> budgetOpt = userBudgets.stream()
                    .filter(b -> b.getCategory().getId().equals(category.getId()))
                    .findFirst();

            BigDecimal limit = null;
            BigDecimal remaining = null;
            BigDecimal percentUsed = null;
            String status = "NO_BUDGET";

            if (budgetOpt.isPresent()) {
                limit = budgetOpt.get().getMonthlyLimit();
                totalBudget = totalBudget.add(limit);
                remaining = limit.subtract(spent);

                if (limit.compareTo(BigDecimal.ZERO) > 0) {
                    percentUsed = spent.multiply(new BigDecimal("100")).divide(limit, 2, RoundingMode.HALF_UP);
                    if (percentUsed.compareTo(new BigDecimal("100.00")) > 0) {
                        status = "EXCEEDED";
                    } else if (percentUsed.compareTo(BigDecimal.valueOf(alertThresholdPercent)) >= 0) {
                        status = "WARNING";
                    } else {
                        status = "NORMAL";
                    }
                }
            }

            summaries.add(CategorySummaryDTO.builder()
                    .categoryId(category.getId())
                    .categoryName(category.getName())
                    .spentAmount(spent)
                    .monthlyLimit(limit)
                    .remainingAmount(remaining)
                    .percentUsed(percentUsed)
                    .budgetStatus(status)
                    .build());
        }

        return MonthlySummaryDTO.builder()
                .userId(user.getId())
                .userName(user.getName())
                .yearMonth(currentMonth.toString())
                .totalSpent(totalSpent)
                .totalBudget(totalBudget)
                .categorySummaries(summaries)
                .build();
    }

    /**
     * Get list of active budget alerts (> 90% budget) for current month.
     */
    @Override
    @Transactional(readOnly = true)
    public List<BudgetAlertDTO> getBudgetAlerts(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException("User not found with id: " + userId);
        }

        YearMonth currentMonth = YearMonth.now();
        List<Budget> userBudgets = budgetRepository.findByUserId(userId);
        List<BudgetAlertDTO> alerts = new ArrayList<>();

        for (Budget budget : userBudgets) {
            BudgetAlertDTO alert = calculateBudgetAlert(userId, budget.getCategory().getId(), budget.getCategory().getName(), currentMonth.atDay(1));
            if (alert != null && alert.isAlertTriggered()) {
                alerts.add(alert);
            }
        }

        return alerts;
    }

    /**
     * Get month-over-month trend for the last N months.
     */
    @Override
    @Transactional(readOnly = true)
    public OverallTrendDTO getMonthOverMonthTrend(Long userId, int months) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        int effectiveMonths = Math.max(months, 1);
        YearMonth currentYearMonth = YearMonth.now();

        List<MonthTrendDTO> monthTrends = new ArrayList<>();

        for (int i = effectiveMonths - 1; i >= 0; i--) {
            YearMonth targetMonth = currentYearMonth.minusMonths(i);
            LocalDate start = targetMonth.atDay(1);
            LocalDate end = targetMonth.atEndOfMonth();

            BigDecimal monthTotal = expenseRepository.sumTotalSpentByUserAndDateRange(userId, start, end);
            List<Object[]> rawCategorySpends = expenseRepository.sumCategorySpendByUserAndDateRange(userId, start, end);

            List<CategoryMonthlySpendDTO> categoryBreakdown = new ArrayList<>();
            for (Object[] row : rawCategorySpends) {
                Long catId = (Long) row[0];
                String catName = (String) row[1];
                BigDecimal catSpent = (BigDecimal) row[2];
                categoryBreakdown.add(CategoryMonthlySpendDTO.builder()
                        .categoryId(catId)
                        .categoryName(catName)
                        .amount(catSpent)
                        .build());
            }

            BigDecimal percentChange = null;
            if (!monthTrends.isEmpty()) {
                BigDecimal prevMonthTotal = monthTrends.get(monthTrends.size() - 1).getTotalSpent();
                if (prevMonthTotal.compareTo(BigDecimal.ZERO) == 0) {
                    if (monthTotal.compareTo(BigDecimal.ZERO) == 0) {
                        percentChange = BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
                    } else {
                        percentChange = new BigDecimal("100.00");
                    }
                } else {
                    BigDecimal diff = monthTotal.subtract(prevMonthTotal);
                    percentChange = diff.multiply(new BigDecimal("100")).divide(prevMonthTotal, 2, RoundingMode.HALF_UP);
                }
            }

            monthTrends.add(MonthTrendDTO.builder()
                    .yearMonth(targetMonth.toString())
                    .totalSpent(monthTotal)
                    .categoryBreakdown(categoryBreakdown)
                    .percentChangeVsPreviousMonth(percentChange)
                    .build());
        }

        return OverallTrendDTO.builder()
                .userId(user.getId())
                .userName(user.getName())
                .monthsRequested(effectiveMonths)
                .trends(monthTrends)
                .build();
    }

    /**
     * Enforce strict budget limit checking. Reject expense if current spent + new amount > category budget limit.
     */
    private void checkBudgetLimit(Long userId, Category category, BigDecimal amount, LocalDate expenseDate, Long updatingExpenseId) {
        Optional<Budget> budgetOpt = budgetRepository.findByUserIdAndCategoryId(userId, category.getId());
        if (budgetOpt.isEmpty()) {
            return; // No budget defined for this category -> unlimited
        }

        BigDecimal limit = budgetOpt.get().getMonthlyLimit();
        if (limit == null || limit.compareTo(BigDecimal.ZERO) <= 0) {
            return;
        }

        YearMonth yearMonth = YearMonth.from(expenseDate);
        LocalDate start = yearMonth.atDay(1);
        LocalDate end = yearMonth.atEndOfMonth();

        BigDecimal currentSpent = expenseRepository.sumTotalSpentByUserAndCategoryAndDateRange(userId, category.getId(), start, end);

        if (updatingExpenseId != null) {
            Expense existing = expenseRepository.findById(updatingExpenseId).orElse(null);
            if (existing != null && existing.getCategory().getId().equals(category.getId())
                    && YearMonth.from(existing.getExpenseDate()).equals(yearMonth)) {
                currentSpent = currentSpent.subtract(existing.getAmount());
            }
        }

        BigDecimal proposedTotal = currentSpent.add(amount);
        if (proposedTotal.compareTo(limit) > 0) {
            throw new InvalidExpenseException(String.format(
                    "Expense amount exceeds monthly budget limit for category '%s'. Monthly Limit: ₹%s, Current Spent: ₹%s, Attempted: ₹%s (Proposed Total: ₹%s)",
                    category.getName(), limit, currentSpent, amount, proposedTotal
            ));
        }
    }

    /**
     * Enforce strict service-layer business rules.
     */
    private void validateExpenseRequest(ExpenseRequestDTO requestDTO) {
        if (requestDTO.getAmount() == null || requestDTO.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidExpenseException("Expense amount must be positive (> 0)");
        }
        if (requestDTO.getExpenseDate() == null) {
            throw new InvalidExpenseException("Expense date cannot be null");
        }
        if (requestDTO.getExpenseDate().isAfter(LocalDate.now().plusYears(1))) {
            throw new InvalidExpenseException("Expense date cannot be more than 1 year in the future");
        }
    }

    /**
     * Calculate budget alert status for given user, category, and date.
     */
    private BudgetAlertDTO calculateBudgetAlert(Long userId, Long categoryId, String categoryName, LocalDate date) {
        Optional<Budget> budgetOpt = budgetRepository.findByUserIdAndCategoryId(userId, categoryId);
        if (budgetOpt.isEmpty()) {
            return null;
        }

        Budget budget = budgetOpt.get();
        BigDecimal limit = budget.getMonthlyLimit();
        if (limit == null || limit.compareTo(BigDecimal.ZERO) <= 0) {
            return null;
        }

        YearMonth yearMonth = YearMonth.from(date);
        LocalDate start = yearMonth.atDay(1);
        LocalDate end = yearMonth.atEndOfMonth();

        BigDecimal spent = expenseRepository.sumTotalSpentByUserAndCategoryAndDateRange(userId, categoryId, start, end);
        BigDecimal remaining = limit.subtract(spent);

        BigDecimal percentUsed = spent.multiply(new BigDecimal("100")).divide(limit, 2, RoundingMode.HALF_UP);
        boolean triggered = percentUsed.compareTo(BigDecimal.valueOf(alertThresholdPercent)) >= 0;

        String status;
        if (percentUsed.compareTo(new BigDecimal("100.00")) > 0) {
            status = "EXCEEDED";
        } else if (triggered) {
            status = "WARNING";
        } else {
            status = "NORMAL";
        }

        String msg = String.format("Category '%s' spent ₹%s of ₹%s (%s%% used - Status: %s)",
                categoryName, spent, limit, percentUsed, status);

        return BudgetAlertDTO.builder()
                .categoryId(categoryId)
                .categoryName(categoryName)
                .monthlyLimit(limit)
                .spentAmount(spent)
                .remainingAmount(remaining)
                .percentUsed(percentUsed)
                .alertTriggered(triggered)
                .status(status)
                .message(msg)
                .build();
    }

    private ExpenseResponseDTO mapToDTO(Expense expense, BudgetAlertDTO alert) {
        return ExpenseResponseDTO.builder()
                .id(expense.getId())
                .userId(expense.getUser().getId())
                .userName(expense.getUser().getName())
                .categoryId(expense.getCategory().getId())
                .categoryName(expense.getCategory().getName())
                .amount(expense.getAmount())
                .expenseDate(expense.getExpenseDate())
                .description(expense.getDescription())
                .createdAt(expense.getCreatedAt())
                .budgetAlert(alert)
                .build();
    }
}
