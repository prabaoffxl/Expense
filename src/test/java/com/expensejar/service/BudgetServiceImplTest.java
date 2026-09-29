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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BudgetServiceImplTest {

    @Mock
    private BudgetRepository budgetRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private BudgetServiceImpl budgetService;

    private User sampleUser;
    private Category sampleCategory;

    @BeforeEach
    void setUp() {
        sampleUser = User.builder().id(1L).name("Alice").email("alice@example.com").build();
        sampleCategory = Category.builder().id(2L).name("Travel").build();
    }

    @Test
    @DisplayName("Reject budget creation when limit is negative or zero")
    void testSetBudget_NegativeOrZeroLimit_ThrowsException() {
        BudgetRequestDTO dtoZero = BudgetRequestDTO.builder().userId(1L).categoryId(2L).monthlyLimit(BigDecimal.ZERO).build();
        BudgetRequestDTO dtoNegative = BudgetRequestDTO.builder().userId(1L).categoryId(2L).monthlyLimit(new BigDecimal("-100.00")).build();

        assertThrows(InvalidExpenseException.class, () -> budgetService.setBudget(dtoZero));
        assertThrows(InvalidExpenseException.class, () -> budgetService.setBudget(dtoNegative));
    }

    @Test
    @DisplayName("Throw ResourceNotFoundException when user or category does not exist")
    void testSetBudget_UserOrCategoryNotFound_ThrowsException() {
        BudgetRequestDTO requestDTO = BudgetRequestDTO.builder().userId(99L).categoryId(2L).monthlyLimit(new BigDecimal("500.00")).build();

        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> budgetService.setBudget(requestDTO));
    }

    @Test
    @DisplayName("Successfully create new budget for user and category")
    void testSetBudget_Success_Create() {
        BudgetRequestDTO requestDTO = BudgetRequestDTO.builder().userId(1L).categoryId(2L).monthlyLimit(new BigDecimal("300.00")).build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(sampleUser));
        when(categoryRepository.findById(2L)).thenReturn(Optional.of(sampleCategory));
        when(budgetRepository.findByUserIdAndCategoryId(1L, 2L)).thenReturn(Optional.empty());

        Budget savedBudget = Budget.builder().id(5L).user(sampleUser).category(sampleCategory).monthlyLimit(new BigDecimal("300.00")).build();
        when(budgetRepository.save(any(Budget.class))).thenReturn(savedBudget);

        BudgetResponseDTO response = budgetService.setBudget(requestDTO);

        assertNotNull(response);
        assertEquals(5L, response.getId());
        assertEquals(new BigDecimal("300.00"), response.getMonthlyLimit());
        assertEquals("Travel", response.getCategoryName());
    }
}
