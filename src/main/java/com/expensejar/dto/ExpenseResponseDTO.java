package com.expensejar.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class ExpenseResponseDTO {
    private Long id;
    private Long userId;
    private String userName;
    private Long categoryId;
    private String categoryName;
    private BigDecimal amount;
    private LocalDate expenseDate;
    private String description;
    private LocalDateTime createdAt;
    private BudgetAlertDTO budgetAlert;

    public ExpenseResponseDTO() {}

    public ExpenseResponseDTO(Long id, Long userId, String userName, Long categoryId, String categoryName,
                              BigDecimal amount, LocalDate expenseDate, String description, LocalDateTime createdAt,
                              BudgetAlertDTO budgetAlert) {
        this.id = id;
        this.userId = userId;
        this.userName = userName;
        this.categoryId = categoryId;
        this.categoryName = categoryName;
        this.amount = amount;
        this.expenseDate = expenseDate;
        this.description = description;
        this.createdAt = createdAt;
        this.budgetAlert = budgetAlert;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }

    public Long getCategoryId() { return categoryId; }
    public void setCategoryId(Long categoryId) { this.categoryId = categoryId; }

    public String getCategoryName() { return categoryName; }
    public void setCategoryName(String categoryName) { this.categoryName = categoryName; }

    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }

    public LocalDate getExpenseDate() { return expenseDate; }
    public void setExpenseDate(LocalDate expenseDate) { this.expenseDate = expenseDate; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public BudgetAlertDTO getBudgetAlert() { return budgetAlert; }
    public void setBudgetAlert(BudgetAlertDTO budgetAlert) { this.budgetAlert = budgetAlert; }

    public static ExpenseResponseDTOBuilder builder() { return new ExpenseResponseDTOBuilder(); }

    public static class ExpenseResponseDTOBuilder {
        private Long id;
        private Long userId;
        private String userName;
        private Long categoryId;
        private String categoryName;
        private BigDecimal amount;
        private LocalDate expenseDate;
        private String description;
        private LocalDateTime createdAt;
        private BudgetAlertDTO budgetAlert;

        public ExpenseResponseDTOBuilder id(Long id) { this.id = id; return this; }
        public ExpenseResponseDTOBuilder userId(Long userId) { this.userId = userId; return this; }
        public ExpenseResponseDTOBuilder userName(String userName) { this.userName = userName; return this; }
        public ExpenseResponseDTOBuilder categoryId(Long categoryId) { this.categoryId = categoryId; return this; }
        public ExpenseResponseDTOBuilder categoryName(String categoryName) { this.categoryName = categoryName; return this; }
        public ExpenseResponseDTOBuilder amount(BigDecimal amount) { this.amount = amount; return this; }
        public ExpenseResponseDTOBuilder expenseDate(LocalDate expenseDate) { this.expenseDate = expenseDate; return this; }
        public ExpenseResponseDTOBuilder description(String description) { this.description = description; return this; }
        public ExpenseResponseDTOBuilder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }
        public ExpenseResponseDTOBuilder budgetAlert(BudgetAlertDTO budgetAlert) { this.budgetAlert = budgetAlert; return this; }

        public ExpenseResponseDTO build() {
            return new ExpenseResponseDTO(id, userId, userName, categoryId, categoryName, amount, expenseDate, description, createdAt, budgetAlert);
        }
    }
}
