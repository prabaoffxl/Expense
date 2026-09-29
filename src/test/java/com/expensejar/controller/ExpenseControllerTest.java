package com.expensejar.controller;

import com.expensejar.dto.BudgetAlertDTO;
import com.expensejar.dto.ExpenseRequestDTO;
import com.expensejar.dto.ExpenseResponseDTO;
import com.expensejar.exception.InvalidExpenseException;
import com.expensejar.exception.ResourceNotFoundException;
import com.expensejar.service.ExpenseService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ExpenseController.class)
class ExpenseControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ExpenseService expenseService;

    @Test
    @DisplayName("POST /api/expenses - Normal Path returns 201 Created with budget alert")
    void testAddExpense_Success() throws Exception {
        ExpenseRequestDTO requestDTO = ExpenseRequestDTO.builder()
                .userId(1L)
                .categoryId(2L)
                .amount(new BigDecimal("150.00"))
                .expenseDate(LocalDate.of(2026, 9, 15))
                .description("Bus fare")
                .build();

        BudgetAlertDTO alert = BudgetAlertDTO.builder()
                .categoryId(2L)
                .categoryName("Travel")
                .monthlyLimit(new BigDecimal("200.00"))
                .spentAmount(new BigDecimal("150.00"))
                .remainingAmount(new BigDecimal("50.00"))
                .percentUsed(new BigDecimal("75.00"))
                .alertTriggered(false)
                .status("NORMAL")
                .message("Category 'Travel' spent ₹150.00 of ₹200.00 (75.00% used)")
                .build();

        ExpenseResponseDTO responseDTO = ExpenseResponseDTO.builder()
                .id(100L)
                .userId(1L)
                .userName("Alice Smith")
                .categoryId(2L)
                .categoryName("Travel")
                .amount(new BigDecimal("150.00"))
                .expenseDate(LocalDate.of(2026, 9, 15))
                .description("Bus fare")
                .createdAt(LocalDateTime.now())
                .budgetAlert(alert)
                .build();

        when(expenseService.addExpense(any(ExpenseRequestDTO.class))).thenReturn(responseDTO);

        mockMvc.perform(post("/api/expenses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(100))
                .andExpect(jsonPath("$.amount").value(150.00))
                .andExpect(jsonPath("$.categoryName").value("Travel"))
                .andExpect(jsonPath("$.budgetAlert.status").value("NORMAL"))
                .andExpect(jsonPath("$.budgetAlert.alertTriggered").value(false));
    }

    @Test
    @DisplayName("POST /api/expenses - Validation Violation returns 400 Bad Request")
    void testAddExpense_ValidationFailure() throws Exception {
        ExpenseRequestDTO invalidRequest = ExpenseRequestDTO.builder()
                .userId(null)
                .categoryId(2L)
                .amount(new BigDecimal("-10.00"))
                .expenseDate(null)
                .build();

        mockMvc.perform(post("/api/expenses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.fieldErrors").exists());
    }

    @Test
    @DisplayName("POST /api/expenses - Business Rule Violation (service level) returns 400 Bad Request")
    void testAddExpense_BusinessRuleViolation() throws Exception {
        ExpenseRequestDTO requestDTO = ExpenseRequestDTO.builder()
                .userId(1L)
                .categoryId(2L)
                .amount(new BigDecimal("50.00"))
                .expenseDate(LocalDate.of(2026, 9, 15))
                .build();

        when(expenseService.addExpense(any(ExpenseRequestDTO.class)))
                .thenThrow(new InvalidExpenseException("Expense amount must be positive (> 0)"));

        mockMvc.perform(post("/api/expenses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Expense amount must be positive (> 0)"));
    }

    @Test
    @DisplayName("GET /api/expenses/{id} - Not Found returns 404")
    void testGetExpenseById_NotFound() throws Exception {
        when(expenseService.getExpenseById(999L))
                .thenThrow(new ResourceNotFoundException("Expense not found with id: 999"));

        mockMvc.perform(get("/api/expenses/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("Expense not found with id: 999"));
    }

    @Test
    @DisplayName("GET /api/expenses - Filtered list returns 200 OK with page structure")
    void testGetExpenses_WithPagination() throws Exception {
        ExpenseResponseDTO item = ExpenseResponseDTO.builder()
                .id(1L).userId(1L).userName("Alice").categoryId(2L).categoryName("Travel")
                .amount(new BigDecimal("100.00")).expenseDate(LocalDate.now()).build();

        when(expenseService.getExpenses(eq(1L), any(), any(), any(), any(), any(), any(Pageable.class)))
                .thenReturn(new PageImpl<>(Collections.singletonList(item)));

        mockMvc.perform(get("/api/expenses?userId=1&page=0&size=10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(1))
                .andExpect(jsonPath("$.totalElements").value(1));
    }

    @Test
    @DisplayName("DELETE /api/expenses/{id} - Returns 204 No Content")
    void testDeleteExpense_Success() throws Exception {
        doNothing().when(expenseService).deleteExpense(1L);

        mockMvc.perform(delete("/api/expenses/1"))
                .andExpect(status().isNoContent());
    }
}
