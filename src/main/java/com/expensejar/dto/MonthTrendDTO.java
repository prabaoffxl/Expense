package com.expensejar.dto;

import java.math.BigDecimal;
import java.util.List;

public class MonthTrendDTO {
    private String yearMonth;
    private BigDecimal totalSpent;
    private List<CategoryMonthlySpendDTO> categoryBreakdown;
    private BigDecimal percentChangeVsPreviousMonth;

    public MonthTrendDTO() {}

    public MonthTrendDTO(String yearMonth, BigDecimal totalSpent, List<CategoryMonthlySpendDTO> categoryBreakdown, BigDecimal percentChangeVsPreviousMonth) {
        this.yearMonth = yearMonth;
        this.totalSpent = totalSpent;
        this.categoryBreakdown = categoryBreakdown;
        this.percentChangeVsPreviousMonth = percentChangeVsPreviousMonth;
    }

    public String getYearMonth() { return yearMonth; }
    public void setYearMonth(String yearMonth) { this.yearMonth = yearMonth; }

    public BigDecimal getTotalSpent() { return totalSpent; }
    public void setTotalSpent(BigDecimal totalSpent) { this.totalSpent = totalSpent; }

    public List<CategoryMonthlySpendDTO> getCategoryBreakdown() { return categoryBreakdown; }
    public void setCategoryBreakdown(List<CategoryMonthlySpendDTO> categoryBreakdown) { this.categoryBreakdown = categoryBreakdown; }

    public BigDecimal getPercentChangeVsPreviousMonth() { return percentChangeVsPreviousMonth; }
    public void setPercentChangeVsPreviousMonth(BigDecimal percentChangeVsPreviousMonth) { this.percentChangeVsPreviousMonth = percentChangeVsPreviousMonth; }

    public static MonthTrendDTOBuilder builder() { return new MonthTrendDTOBuilder(); }

    public static class MonthTrendDTOBuilder {
        private String yearMonth;
        private BigDecimal totalSpent;
        private List<CategoryMonthlySpendDTO> categoryBreakdown;
        private BigDecimal percentChangeVsPreviousMonth;

        public MonthTrendDTOBuilder yearMonth(String yearMonth) { this.yearMonth = yearMonth; return this; }
        public MonthTrendDTOBuilder totalSpent(BigDecimal totalSpent) { this.totalSpent = totalSpent; return this; }
        public MonthTrendDTOBuilder categoryBreakdown(List<CategoryMonthlySpendDTO> categoryBreakdown) { this.categoryBreakdown = categoryBreakdown; return this; }
        public MonthTrendDTOBuilder percentChangeVsPreviousMonth(BigDecimal percentChangeVsPreviousMonth) { this.percentChangeVsPreviousMonth = percentChangeVsPreviousMonth; return this; }

        public MonthTrendDTO build() {
            return new MonthTrendDTO(yearMonth, totalSpent, categoryBreakdown, percentChangeVsPreviousMonth);
        }
    }
}
