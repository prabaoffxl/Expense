package com.expensejar.dto;

import java.math.BigDecimal;

public class BudgetResponseDTO {
    private Long id;
    private Long userId;
    private String userName;
    private Long categoryId;
    private String categoryName;
    private BigDecimal monthlyLimit;

    public BudgetResponseDTO() {}

    public BudgetResponseDTO(Long id, Long userId, String userName, Long categoryId, String categoryName, BigDecimal monthlyLimit) {
        this.id = id;
        this.userId = userId;
        this.userName = userName;
        this.categoryId = categoryId;
        this.categoryName = categoryName;
        this.monthlyLimit = monthlyLimit;
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

    public BigDecimal getMonthlyLimit() { return monthlyLimit; }
    public void setMonthlyLimit(BigDecimal monthlyLimit) { this.monthlyLimit = monthlyLimit; }

    public static BudgetResponseDTOBuilder builder() { return new BudgetResponseDTOBuilder(); }

    public static class BudgetResponseDTOBuilder {
        private Long id;
        private Long userId;
        private String userName;
        private Long categoryId;
        private String categoryName;
        private BigDecimal monthlyLimit;

        public BudgetResponseDTOBuilder id(Long id) { this.id = id; return this; }
        public BudgetResponseDTOBuilder userId(Long userId) { this.userId = userId; return this; }
        public BudgetResponseDTOBuilder userName(String userName) { this.userName = userName; return this; }
        public BudgetResponseDTOBuilder categoryId(Long categoryId) { this.categoryId = categoryId; return this; }
        public BudgetResponseDTOBuilder categoryName(String categoryName) { this.categoryName = categoryName; return this; }
        public BudgetResponseDTOBuilder monthlyLimit(BigDecimal monthlyLimit) { this.monthlyLimit = monthlyLimit; return this; }

        public BudgetResponseDTO build() {
            return new BudgetResponseDTO(id, userId, userName, categoryId, categoryName, monthlyLimit);
        }
    }
}
