package com.expensejar.dto;

import java.math.BigDecimal;

public class CategorySummaryDTO {
    private Long categoryId;
    private String categoryName;
    private BigDecimal spentAmount;
    private BigDecimal monthlyLimit;
    private BigDecimal remainingAmount;
    private BigDecimal percentUsed;
    private String budgetStatus;

    public CategorySummaryDTO() {}

    public CategorySummaryDTO(Long categoryId, String categoryName, BigDecimal spentAmount, BigDecimal monthlyLimit,
                              BigDecimal remainingAmount, BigDecimal percentUsed, String budgetStatus) {
        this.categoryId = categoryId;
        this.categoryName = categoryName;
        this.spentAmount = spentAmount;
        this.monthlyLimit = monthlyLimit;
        this.remainingAmount = remainingAmount;
        this.percentUsed = percentUsed;
        this.budgetStatus = budgetStatus;
    }

    public Long getCategoryId() { return categoryId; }
    public void setCategoryId(Long categoryId) { this.categoryId = categoryId; }

    public String getCategoryName() { return categoryName; }
    public void setCategoryName(String categoryName) { this.categoryName = categoryName; }

    public BigDecimal getSpentAmount() { return spentAmount; }
    public void setSpentAmount(BigDecimal spentAmount) { this.spentAmount = spentAmount; }

    public BigDecimal getMonthlyLimit() { return monthlyLimit; }
    public void setMonthlyLimit(BigDecimal monthlyLimit) { this.monthlyLimit = monthlyLimit; }

    public BigDecimal getRemainingAmount() { return remainingAmount; }
    public void setRemainingAmount(BigDecimal remainingAmount) { this.remainingAmount = remainingAmount; }

    public BigDecimal getPercentUsed() { return percentUsed; }
    public void setPercentUsed(BigDecimal percentUsed) { this.percentUsed = percentUsed; }

    public String getBudgetStatus() { return budgetStatus; }
    public void setBudgetStatus(String budgetStatus) { this.budgetStatus = budgetStatus; }

    public static CategorySummaryDTOBuilder builder() { return new CategorySummaryDTOBuilder(); }

    public static class CategorySummaryDTOBuilder {
        private Long categoryId;
        private String categoryName;
        private BigDecimal spentAmount;
        private BigDecimal monthlyLimit;
        private BigDecimal remainingAmount;
        private BigDecimal percentUsed;
        private String budgetStatus;

        public CategorySummaryDTOBuilder categoryId(Long categoryId) { this.categoryId = categoryId; return this; }
        public CategorySummaryDTOBuilder categoryName(String categoryName) { this.categoryName = categoryName; return this; }
        public CategorySummaryDTOBuilder spentAmount(BigDecimal spentAmount) { this.spentAmount = spentAmount; return this; }
        public CategorySummaryDTOBuilder monthlyLimit(BigDecimal monthlyLimit) { this.monthlyLimit = monthlyLimit; return this; }
        public CategorySummaryDTOBuilder remainingAmount(BigDecimal remainingAmount) { this.remainingAmount = remainingAmount; return this; }
        public CategorySummaryDTOBuilder percentUsed(BigDecimal percentUsed) { this.percentUsed = percentUsed; return this; }
        public CategorySummaryDTOBuilder budgetStatus(String budgetStatus) { this.budgetStatus = budgetStatus; return this; }

        public CategorySummaryDTO build() {
            return new CategorySummaryDTO(categoryId, categoryName, spentAmount, monthlyLimit, remainingAmount, percentUsed, budgetStatus);
        }
    }
}
