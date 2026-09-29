package com.expensejar.controller;

import com.expensejar.dto.BudgetRequestDTO;
import com.expensejar.dto.BudgetResponseDTO;
import com.expensejar.service.BudgetService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/budgets")
@Tag(name = "Budget Management", description = "Endpoints for setting and managing monthly budgets per category")
public class BudgetController {

    private final BudgetService budgetService;

    public BudgetController(BudgetService budgetService) {
        this.budgetService = budgetService;
    }

    @PutMapping
    @Operation(summary = "Set monthly budget for a user and category (Create or Update)")
    public ResponseEntity<BudgetResponseDTO> setBudget(@Valid @RequestBody BudgetRequestDTO requestDTO) {
        BudgetResponseDTO saved = budgetService.setBudget(requestDTO);
        return ResponseEntity.ok(saved);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get budget by ID")
    public ResponseEntity<BudgetResponseDTO> getBudgetById(@PathVariable Long id) {
        return ResponseEntity.ok(budgetService.getBudgetById(id));
    }

    @GetMapping("/user/{userId}")
    @Operation(summary = "Get all budgets for a user")
    public ResponseEntity<List<BudgetResponseDTO>> getBudgetsByUserId(@PathVariable Long userId) {
        return ResponseEntity.ok(budgetService.getBudgetsByUserId(userId));
    }

    @GetMapping
    @Operation(summary = "Get all budgets")
    public ResponseEntity<List<BudgetResponseDTO>> getAllBudgets() {
        return ResponseEntity.ok(budgetService.getAllBudgets());
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete budget by ID")
    public ResponseEntity<Void> deleteBudget(@PathVariable Long id) {
        budgetService.deleteBudget(id);
        return ResponseEntity.noContent().build();
    }
}
