package com.expensejar.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public class BudgetRequestDTO {

    @NotNull(message = "User ID is required")
    private Long userId;

    @NotNull(message = "Category ID is required")
    private Long categoryId;

    @NotNull(message = "Monthly limit is required")
    @Positive(message = "Monthly limit must be positive (> 0)")
    private BigDecimal monthlyLimit;

    public BudgetRequestDTO() {}

    public BudgetRequestDTO(Long userId, Long categoryId, BigDecimal monthlyLimit) {
        this.userId = userId;
        this.categoryId = categoryId;
        this.monthlyLimit = monthlyLimit;
    }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public Long getCategoryId() { return categoryId; }
    public void setCategoryId(Long categoryId) { this.categoryId = categoryId; }

    public BigDecimal getMonthlyLimit() { return monthlyLimit; }
    public void setMonthlyLimit(BigDecimal monthlyLimit) { this.monthlyLimit = monthlyLimit; }

    public static BudgetRequestDTOBuilder builder() { return new BudgetRequestDTOBuilder(); }

    public static class BudgetRequestDTOBuilder {
        private Long userId;
        private Long categoryId;
        private BigDecimal monthlyLimit;

        public BudgetRequestDTOBuilder userId(Long userId) { this.userId = userId; return this; }
        public BudgetRequestDTOBuilder categoryId(Long categoryId) { this.categoryId = categoryId; return this; }
        public BudgetRequestDTOBuilder monthlyLimit(BigDecimal monthlyLimit) { this.monthlyLimit = monthlyLimit; return this; }

        public BudgetRequestDTO build() {
            return new BudgetRequestDTO(userId, categoryId, monthlyLimit);
        }
    }
}
