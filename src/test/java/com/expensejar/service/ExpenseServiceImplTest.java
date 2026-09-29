package com.expensejar.service;

import com.expensejar.dto.ExpenseRequestDTO;
import com.expensejar.dto.ExpenseResponseDTO;
import com.expensejar.dto.OverallTrendDTO;
import com.expensejar.entity.Budget;
import com.expensejar.entity.Category;
import com.expensejar.entity.Expense;
import com.expensejar.entity.User;
import com.expensejar.exception.InvalidExpenseException;
import com.expensejar.repository.BudgetRepository;
import com.expensejar.repository.CategoryRepository;
import com.expensejar.repository.ExpenseRepository;
import com.expensejar.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ExpenseServiceImplTest {

    @Mock
    private ExpenseRepository expenseRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private BudgetRepository budgetRepository;

    @InjectMocks
    private ExpenseServiceImpl expenseService;

    private User sampleUser;
    private Category sampleCategory;
    private Budget sampleBudget;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(expenseService, "alertThresholdPercent", 90.0);

        sampleUser = User.builder().id(1L).name("Alice").email("alice@example.com").build();
        sampleCategory = Category.builder().id(1L).name("Food").build();
        sampleBudget = Budget.builder().id(1L).user(sampleUser).category(sampleCategory).monthlyLimit(new BigDecimal("1000.00")).build();
    }

    @Test
    @DisplayName("Reject expense creation when amount is zero or negative")
    void testAddExpense_NegativeOrZeroAmount_ThrowsException() {
        ExpenseRequestDTO dtoZero = ExpenseRequestDTO.builder()
                .userId(1L).categoryId(1L).amount(BigDecimal.ZERO).expenseDate(LocalDate.now()).build();

        ExpenseRequestDTO dtoNegative = ExpenseRequestDTO.builder()
                .userId(1L).categoryId(1L).amount(new BigDecimal("-50.00")).expenseDate(LocalDate.now()).build();

        assertThrows(InvalidExpenseException.class, () -> expenseService.addExpense(dtoZero));
        assertThrows(InvalidExpenseException.class, () -> expenseService.addExpense(dtoNegative));
    }

    @Test
    @DisplayName("Reject expense creation when proposed spending exceeds category budget limit")
    void testAddExpense_ExceedsBudgetLimit_ThrowsException() {
        ExpenseRequestDTO requestDTO = ExpenseRequestDTO.builder()
                .userId(1L).categoryId(1L).amount(new BigDecimal("150.00")).expenseDate(LocalDate.now()).build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(sampleUser));
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(sampleCategory));
        when(budgetRepository.findByUserIdAndCategoryId(1L, 1L)).thenReturn(Optional.of(sampleBudget));

        // Existing spent is 900.00. Proposed total = 900 + 150 = 1050 > 1000 limit
        when(expenseRepository.sumTotalSpentByUserAndCategoryAndDateRange(eq(1L), eq(1L), any(), any()))
                .thenReturn(new BigDecimal("900.00"));

        InvalidExpenseException ex = assertThrows(InvalidExpenseException.class, () -> expenseService.addExpense(requestDTO));
        assertTrue(ex.getMessage().contains("exceeds monthly budget limit"));
    }

    @Test
    @DisplayName("Successfully add positive expense within budget limit (89% - NORMAL)")
    void testAddExpense_AlertThreshold_Below90Percent() {
        ExpenseRequestDTO requestDTO = ExpenseRequestDTO.builder()
                .userId(1L).categoryId(1L).amount(new BigDecimal("890.00")).expenseDate(LocalDate.now()).description("Grocery").build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(sampleUser));
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(sampleCategory));
        when(budgetRepository.findByUserIdAndCategoryId(1L, 1L)).thenReturn(Optional.of(sampleBudget));

        Expense savedExpense = Expense.builder()
                .id(10L).user(sampleUser).category(sampleCategory).amount(new BigDecimal("890.00")).expenseDate(LocalDate.now()).build();
        when(expenseRepository.save(any(Expense.class))).thenReturn(savedExpense);

        // Before saving proposed total is 0 + 890 = 890 <= 1000 limit. After saving total is 890.
        when(expenseRepository.sumTotalSpentByUserAndCategoryAndDateRange(eq(1L), eq(1L), any(), any()))
                .thenReturn(BigDecimal.ZERO)
                .thenReturn(new BigDecimal("890.00"));

        ExpenseResponseDTO response = expenseService.addExpense(requestDTO);

        assertNotNull(response);
        assertNotNull(response.getBudgetAlert());
        assertFalse(response.getBudgetAlert().isAlertTriggered());
        assertEquals("NORMAL", response.getBudgetAlert().getStatus());
        assertEquals(new BigDecimal("89.00"), response.getBudgetAlert().getPercentUsed());
    }

    @Test
    @DisplayName("Budget alert edge case: Exactly 90% spend triggers WARNING alert")
    void testAddExpense_AlertThreshold_Exactly90Percent() {
        ExpenseRequestDTO requestDTO = ExpenseRequestDTO.builder()
                .userId(1L).categoryId(1L).amount(new BigDecimal("900.00")).expenseDate(LocalDate.now()).build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(sampleUser));
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(sampleCategory));
        when(budgetRepository.findByUserIdAndCategoryId(1L, 1L)).thenReturn(Optional.of(sampleBudget));

        Expense savedExpense = Expense.builder().id(11L).user(sampleUser).category(sampleCategory).amount(new BigDecimal("900.00")).expenseDate(LocalDate.now()).build();
        when(expenseRepository.save(any(Expense.class))).thenReturn(savedExpense);

        when(expenseRepository.sumTotalSpentByUserAndCategoryAndDateRange(eq(1L), eq(1L), any(), any()))
                .thenReturn(BigDecimal.ZERO)
                .thenReturn(new BigDecimal("900.00"));

        ExpenseResponseDTO response = expenseService.addExpense(requestDTO);

        assertTrue(response.getBudgetAlert().isAlertTriggered());
        assertEquals("WARNING", response.getBudgetAlert().getStatus());
        assertEquals(new BigDecimal("90.00"), response.getBudgetAlert().getPercentUsed());
    }

    @Test
    @DisplayName("Budget alert edge case: 91% spend triggers WARNING alert")
    void testAddExpense_AlertThreshold_91Percent() {
        ExpenseRequestDTO requestDTO = ExpenseRequestDTO.builder()
                .userId(1L).categoryId(1L).amount(new BigDecimal("910.00")).expenseDate(LocalDate.now()).build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(sampleUser));
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(sampleCategory));
        when(budgetRepository.findByUserIdAndCategoryId(1L, 1L)).thenReturn(Optional.of(sampleBudget));

        Expense savedExpense = Expense.builder().id(12L).user(sampleUser).category(sampleCategory).amount(new BigDecimal("910.00")).expenseDate(LocalDate.now()).build();
        when(expenseRepository.save(any(Expense.class))).thenReturn(savedExpense);

        when(expenseRepository.sumTotalSpentByUserAndCategoryAndDateRange(eq(1L), eq(1L), any(), any()))
                .thenReturn(BigDecimal.ZERO)
                .thenReturn(new BigDecimal("910.00"));

        ExpenseResponseDTO response = expenseService.addExpense(requestDTO);

        assertTrue(response.getBudgetAlert().isAlertTriggered());
        assertEquals("WARNING", response.getBudgetAlert().getStatus());
        assertEquals(new BigDecimal("91.00"), response.getBudgetAlert().getPercentUsed());
    }

    @Test
    @DisplayName("Month-over-month trend calculation with division by zero handling when previous month is 0")
    void testGetMonthOverMonthTrend_DivisionByZeroHandling() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(sampleUser));

        YearMonth now = YearMonth.now();
        YearMonth prev = now.minusMonths(1);

        when(expenseRepository.sumTotalSpentByUserAndDateRange(eq(1L), eq(prev.atDay(1)), eq(prev.atEndOfMonth())))
                .thenReturn(BigDecimal.ZERO);
        when(expenseRepository.sumTotalSpentByUserAndDateRange(eq(1L), eq(now.atDay(1)), eq(now.atEndOfMonth())))
                .thenReturn(new BigDecimal("500.00"));

        when(expenseRepository.sumCategorySpendByUserAndDateRange(anyLong(), any(), any()))
                .thenReturn(Collections.emptyList());

        OverallTrendDTO trend = expenseService.getMonthOverMonthTrend(1L, 2);

        assertNotNull(trend);
        assertEquals(2, trend.getTrends().size());
        assertNull(trend.getTrends().get(0).getPercentChangeVsPreviousMonth());
        assertEquals(new BigDecimal("100.00"), trend.getTrends().get(1).getPercentChangeVsPreviousMonth());
    }
}
