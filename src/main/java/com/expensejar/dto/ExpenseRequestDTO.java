package com.expensejar.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.time.LocalDate;

public class ExpenseRequestDTO {

    @NotNull(message = "User ID is required")
    private Long userId;

    @NotNull(message = "Category ID is required")
    private Long categoryId;

    @NotNull(message = "Expense amount is required")
    @Positive(message = "Expense amount must be positive (> 0)")
    private BigDecimal amount;

    @NotNull(message = "Expense date is required")
    private LocalDate expenseDate;

    private String description;

    public ExpenseRequestDTO() {}

    public ExpenseRequestDTO(Long userId, Long categoryId, BigDecimal amount, LocalDate expenseDate, String description) {
        this.userId = userId;
        this.categoryId = categoryId;
        this.amount = amount;
        this.expenseDate = expenseDate;
        this.description = description;
    }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public Long getCategoryId() { return categoryId; }
    public void setCategoryId(Long categoryId) { this.categoryId = categoryId; }

    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }

    public LocalDate getExpenseDate() { return expenseDate; }
    public void setExpenseDate(LocalDate expenseDate) { this.expenseDate = expenseDate; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public static ExpenseRequestDTOBuilder builder() { return new ExpenseRequestDTOBuilder(); }

    public static class ExpenseRequestDTOBuilder {
        private Long userId;
        private Long categoryId;
        private BigDecimal amount;
        private LocalDate expenseDate;
        private String description;

        public ExpenseRequestDTOBuilder userId(Long userId) { this.userId = userId; return this; }
        public ExpenseRequestDTOBuilder categoryId(Long categoryId) { this.categoryId = categoryId; return this; }
        public ExpenseRequestDTOBuilder amount(BigDecimal amount) { this.amount = amount; return this; }
        public ExpenseRequestDTOBuilder expenseDate(LocalDate expenseDate) { this.expenseDate = expenseDate; return this; }
        public ExpenseRequestDTOBuilder description(String description) { this.description = description; return this; }

        public ExpenseRequestDTO build() {
            return new ExpenseRequestDTO(userId, categoryId, amount, expenseDate, description);
        }
    }
}
