package com.expensejar.dto;

import java.math.BigDecimal;

public class BudgetAlertDTO {
    private Long categoryId;
    private String categoryName;
    private BigDecimal monthlyLimit;
    private BigDecimal spentAmount;
    private BigDecimal remainingAmount;
    private BigDecimal percentUsed;
    private boolean alertTriggered;
    private String status;
    private String message;

    public BudgetAlertDTO() {}

    public BudgetAlertDTO(Long categoryId, String categoryName, BigDecimal monthlyLimit, BigDecimal spentAmount,
                          BigDecimal remainingAmount, BigDecimal percentUsed, boolean alertTriggered,
                          String status, String message) {
        this.categoryId = categoryId;
        this.categoryName = categoryName;
        this.monthlyLimit = monthlyLimit;
        this.spentAmount = spentAmount;
        this.remainingAmount = remainingAmount;
        this.percentUsed = percentUsed;
        this.alertTriggered = alertTriggered;
        this.status = status;
        this.message = message;
    }

    public Long getCategoryId() { return categoryId; }
    public void setCategoryId(Long categoryId) { this.categoryId = categoryId; }

    public String getCategoryName() { return categoryName; }
    public void setCategoryName(String categoryName) { this.categoryName = categoryName; }

    public BigDecimal getMonthlyLimit() { return monthlyLimit; }
    public void setMonthlyLimit(BigDecimal monthlyLimit) { this.monthlyLimit = monthlyLimit; }

    public BigDecimal getSpentAmount() { return spentAmount; }
    public void setSpentAmount(BigDecimal spentAmount) { this.spentAmount = spentAmount; }

    public BigDecimal getRemainingAmount() { return remainingAmount; }
    public void setRemainingAmount(BigDecimal remainingAmount) { this.remainingAmount = remainingAmount; }

    public BigDecimal getPercentUsed() { return percentUsed; }
    public void setPercentUsed(BigDecimal percentUsed) { this.percentUsed = percentUsed; }

    public boolean isAlertTriggered() { return alertTriggered; }
    public void setAlertTriggered(boolean alertTriggered) { this.alertTriggered = alertTriggered; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public static BudgetAlertDTOBuilder builder() { return new BudgetAlertDTOBuilder(); }

    public static class BudgetAlertDTOBuilder {
        private Long categoryId;
        private String categoryName;
        private BigDecimal monthlyLimit;
        private BigDecimal spentAmount;
        private BigDecimal remainingAmount;
        private BigDecimal percentUsed;
        private boolean alertTriggered;
        private String status;
        private String message;

        public BudgetAlertDTOBuilder categoryId(Long categoryId) { this.categoryId = categoryId; return this; }
        public BudgetAlertDTOBuilder categoryName(String categoryName) { this.categoryName = categoryName; return this; }
        public BudgetAlertDTOBuilder monthlyLimit(BigDecimal monthlyLimit) { this.monthlyLimit = monthlyLimit; return this; }
        public BudgetAlertDTOBuilder spentAmount(BigDecimal spentAmount) { this.spentAmount = spentAmount; return this; }
        public BudgetAlertDTOBuilder remainingAmount(BigDecimal remainingAmount) { this.remainingAmount = remainingAmount; return this; }
        public BudgetAlertDTOBuilder percentUsed(BigDecimal percentUsed) { this.percentUsed = percentUsed; return this; }
        public BudgetAlertDTOBuilder alertTriggered(boolean alertTriggered) { this.alertTriggered = alertTriggered; return this; }
        public BudgetAlertDTOBuilder status(String status) { this.status = status; return this; }
        public BudgetAlertDTOBuilder message(String message) { this.message = message; return this; }

        public BudgetAlertDTO build() {
            return new BudgetAlertDTO(categoryId, categoryName, monthlyLimit, spentAmount, remainingAmount, percentUsed, alertTriggered, status, message);
        }
    }
}
