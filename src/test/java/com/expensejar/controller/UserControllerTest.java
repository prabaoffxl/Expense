package com.expensejar.controller;

import com.expensejar.dto.BudgetAlertDTO;
import com.expensejar.dto.MonthlySummaryDTO;
import com.expensejar.dto.UserRequestDTO;
import com.expensejar.dto.UserResponseDTO;
import com.expensejar.exception.DuplicateResourceException;
import com.expensejar.exception.ResourceNotFoundException;
import com.expensejar.service.ExpenseService;
import com.expensejar.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;

    @MockBean
    private ExpenseService expenseService;

    @Test
    @DisplayName("POST /api/users - Creates user successfully returning 201")
    void testCreateUser_Success() throws Exception {
        UserRequestDTO requestDTO = UserRequestDTO.builder().name("Charlie").email("charlie@example.com").build();
        UserResponseDTO responseDTO = UserResponseDTO.builder().id(3L).name("Charlie").email("charlie@example.com").createdAt(LocalDateTime.now()).build();

        when(userService.createUser(any(UserRequestDTO.class))).thenReturn(responseDTO);

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(3))
                .andExpect(jsonPath("$.email").value("charlie@example.com"));
    }

    @Test
    @DisplayName("POST /api/users - Duplicate email returns 409 Conflict")
    void testCreateUser_DuplicateEmail_Returns409() throws Exception {
        UserRequestDTO requestDTO = UserRequestDTO.builder().name("Alice").email("alice@example.com").build();

        when(userService.createUser(any(UserRequestDTO.class)))
                .thenThrow(new DuplicateResourceException("User with email 'alice@example.com' already exists"));

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.message").value("User with email 'alice@example.com' already exists"));
    }

    @Test
    @DisplayName("GET /api/users/{userId}/summary/current-month - Returns 200 OK with monthly summary")
    void testGetCurrentMonthSummary_Success() throws Exception {
        MonthlySummaryDTO summaryDTO = MonthlySummaryDTO.builder()
                .userId(1L)
                .userName("Alice Smith")
                .yearMonth("2026-09")
                .totalSpent(new BigDecimal("460.00"))
                .totalBudget(new BigDecimal("500.00"))
                .categorySummaries(Collections.emptyList())
                .build();

        when(expenseService.getCurrentMonthSummary(1L)).thenReturn(summaryDTO);

        mockMvc.perform(get("/api/users/1/summary/current-month"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(1))
                .andExpect(jsonPath("$.totalSpent").value(460.00));
    }

    @Test
    @DisplayName("GET /api/users/{userId}/alerts - Returns 200 OK with active budget alerts")
    void testGetBudgetAlerts_Success() throws Exception {
        BudgetAlertDTO alert = BudgetAlertDTO.builder()
                .categoryId(1L)
                .categoryName("Food")
                .monthlyLimit(new BigDecimal("500.00"))
                .spentAmount(new BigDecimal("460.00"))
                .remainingAmount(new BigDecimal("40.00"))
                .percentUsed(new BigDecimal("92.00"))
                .alertTriggered(true)
                .status("WARNING")
                .message("Category 'Food' spent ₹460.00 of ₹500.00 (92.00% used - Status: WARNING)")
                .build();

        when(expenseService.getBudgetAlerts(1L)).thenReturn(List.of(alert));

        mockMvc.perform(get("/api/users/1/alerts"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].categoryName").value("Food"))
                .andExpect(jsonPath("$[0].status").value("WARNING"))
                .andExpect(jsonPath("$[0].alertTriggered").value(true));
    }

    @Test
    @DisplayName("GET /api/users/{userId}/alerts - User not found returns 404")
    void testGetBudgetAlerts_UserNotFound() throws Exception {
        when(expenseService.getBudgetAlerts(99L))
                .thenThrow(new ResourceNotFoundException("User not found with id: 99"));

        mockMvc.perform(get("/api/users/99/alerts"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }
}
