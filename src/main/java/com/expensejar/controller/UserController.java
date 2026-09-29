package com.expensejar.controller;

import com.expensejar.dto.*;
import com.expensejar.service.ExpenseService;
import com.expensejar.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@Tag(name = "User Management", description = "Endpoints for managing users, monthly summaries, budget alerts, and spending trends")
public class UserController {

    private final UserService userService;
    private final ExpenseService expenseService;

    // Constructor Injection
    public UserController(UserService userService, ExpenseService expenseService) {
        this.userService = userService;
        this.expenseService = expenseService;
    }

    @PostMapping
    @Operation(summary = "Create a new user")
    public ResponseEntity<UserResponseDTO> createUser(@Valid @RequestBody UserRequestDTO requestDTO) {
        UserResponseDTO created = userService.createUser(requestDTO);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get user by ID")
    public ResponseEntity<UserResponseDTO> getUserById(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    @GetMapping
    @Operation(summary = "Get all users")
    public ResponseEntity<List<UserResponseDTO>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an existing user")
    public ResponseEntity<UserResponseDTO> updateUser(@PathVariable Long id, @Valid @RequestBody UserRequestDTO requestDTO) {
        return ResponseEntity.ok(userService.updateUser(id, requestDTO));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a user")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{userId}/summary/current-month")
    @Operation(summary = "Get current month spending summary per category for a user")
    public ResponseEntity<MonthlySummaryDTO> getCurrentMonthSummary(@PathVariable Long userId) {
        return ResponseEntity.ok(expenseService.getCurrentMonthSummary(userId));
    }

    @GetMapping("/{userId}/alerts")
    @Operation(summary = "Get active budget alerts (categories crossing >90% of monthly budget)")
    public ResponseEntity<List<BudgetAlertDTO>> getBudgetAlerts(@PathVariable Long userId) {
        return ResponseEntity.ok(expenseService.getBudgetAlerts(userId));
    }

    @GetMapping("/{userId}/trend")
    @Operation(summary = "Get month-over-month spending trend with per-category breakdown and percentage change")
    public ResponseEntity<OverallTrendDTO> getMonthOverMonthTrend(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "6") int months) {
        return ResponseEntity.ok(expenseService.getMonthOverMonthTrend(userId, months));
    }
}
