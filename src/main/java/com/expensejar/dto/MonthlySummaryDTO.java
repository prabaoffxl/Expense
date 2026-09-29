package com.expensejar.dto;

import java.math.BigDecimal;
import java.util.List;

public class MonthlySummaryDTO {
    private Long userId;
    private String userName;
    private String yearMonth;
    private BigDecimal totalSpent;
    private BigDecimal totalBudget;
    private List<CategorySummaryDTO> categorySummaries;

    public MonthlySummaryDTO() {}

    public MonthlySummaryDTO(Long userId, String userName, String yearMonth, BigDecimal totalSpent, BigDecimal totalBudget, List<CategorySummaryDTO> categorySummaries) {
        this.userId = userId;
        this.userName = userName;
        this.yearMonth = yearMonth;
        this.totalSpent = totalSpent;
        this.totalBudget = totalBudget;
        this.categorySummaries = categorySummaries;
    }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }

    public String getYearMonth() { return yearMonth; }
    public void setYearMonth(String yearMonth) { this.yearMonth = yearMonth; }

    public BigDecimal getTotalSpent() { return totalSpent; }
    public void setTotalSpent(BigDecimal totalSpent) { this.totalSpent = totalSpent; }

    public BigDecimal getTotalBudget() { return totalBudget; }
    public void setTotalBudget(BigDecimal totalBudget) { this.totalBudget = totalBudget; }

    public List<CategorySummaryDTO> getCategorySummaries() { return categorySummaries; }
    public void setCategorySummaries(List<CategorySummaryDTO> categorySummaries) { this.categorySummaries = categorySummaries; }

    public static MonthlySummaryDTOBuilder builder() { return new MonthlySummaryDTOBuilder(); }

    public static class MonthlySummaryDTOBuilder {
        private Long userId;
        private String userName;
        private String yearMonth;
        private BigDecimal totalSpent;
        private BigDecimal totalBudget;
        private List<CategorySummaryDTO> categorySummaries;

        public MonthlySummaryDTOBuilder userId(Long userId) { this.userId = userId; return this; }
        public MonthlySummaryDTOBuilder userName(String userName) { this.userName = userName; return this; }
        public MonthlySummaryDTOBuilder yearMonth(String yearMonth) { this.yearMonth = yearMonth; return this; }
        public MonthlySummaryDTOBuilder totalSpent(BigDecimal totalSpent) { this.totalSpent = totalSpent; return this; }
        public MonthlySummaryDTOBuilder totalBudget(BigDecimal totalBudget) { this.totalBudget = totalBudget; return this; }
        public MonthlySummaryDTOBuilder categorySummaries(List<CategorySummaryDTO> categorySummaries) { this.categorySummaries = categorySummaries; return this; }

        public MonthlySummaryDTO build() {
            return new MonthlySummaryDTO(userId, userName, yearMonth, totalSpent, totalBudget, categorySummaries);
        }
    }
}
