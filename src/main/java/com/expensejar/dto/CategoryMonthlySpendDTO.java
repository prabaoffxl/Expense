package com.expensejar.dto;

import java.math.BigDecimal;

public class CategoryMonthlySpendDTO {
    private Long categoryId;
    private String categoryName;
    private BigDecimal amount;

    public CategoryMonthlySpendDTO() {}

    public CategoryMonthlySpendDTO(Long categoryId, String categoryName, BigDecimal amount) {
        this.categoryId = categoryId;
        this.categoryName = categoryName;
        this.amount = amount;
    }

    public Long getCategoryId() { return categoryId; }
    public void setCategoryId(Long categoryId) { this.categoryId = categoryId; }

    public String getCategoryName() { return categoryName; }
    public void setCategoryName(String categoryName) { this.categoryName = categoryName; }

    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }

    public static CategoryMonthlySpendDTOBuilder builder() { return new CategoryMonthlySpendDTOBuilder(); }

    public static class CategoryMonthlySpendDTOBuilder {
        private Long categoryId;
        private String categoryName;
        private BigDecimal amount;

        public CategoryMonthlySpendDTOBuilder categoryId(Long categoryId) { this.categoryId = categoryId; return this; }
        public CategoryMonthlySpendDTOBuilder categoryName(String categoryName) { this.categoryName = categoryName; return this; }
        public CategoryMonthlySpendDTOBuilder amount(BigDecimal amount) { this.amount = amount; return this; }

        public CategoryMonthlySpendDTO build() {
            return new CategoryMonthlySpendDTO(categoryId, categoryName, amount);
        }
    }
}
