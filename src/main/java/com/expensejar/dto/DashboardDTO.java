package com.expensejar.dto;

import java.math.BigDecimal;

public class DashboardDTO {
    private long totalUsers;
    private long totalCategories;
    private long totalExpenses;
    private long totalBudgets;
    private BigDecimal totalExpenseAmountAllTime;
    private BigDecimal currentMonthTotalExpenseAmount;

    public DashboardDTO() {}

    public DashboardDTO(long totalUsers, long totalCategories, long totalExpenses, long totalBudgets,
                        BigDecimal totalExpenseAmountAllTime, BigDecimal currentMonthTotalExpenseAmount) {
        this.totalUsers = totalUsers;
        this.totalCategories = totalCategories;
        this.totalExpenses = totalExpenses;
        this.totalBudgets = totalBudgets;
        this.totalExpenseAmountAllTime = totalExpenseAmountAllTime;
        this.currentMonthTotalExpenseAmount = currentMonthTotalExpenseAmount;
    }

    public long getTotalUsers() { return totalUsers; }
    public void setTotalUsers(long totalUsers) { this.totalUsers = totalUsers; }

    public long getTotalCategories() { return totalCategories; }
    public void setTotalCategories(long totalCategories) { this.totalCategories = totalCategories; }

    public long getTotalExpenses() { return totalExpenses; }
    public void setTotalExpenses(long totalExpenses) { this.totalExpenses = totalExpenses; }

    public long getTotalBudgets() { return totalBudgets; }
    public void setTotalBudgets(long totalBudgets) { this.totalBudgets = totalBudgets; }

    public BigDecimal getTotalExpenseAmountAllTime() { return totalExpenseAmountAllTime; }
    public void setTotalExpenseAmountAllTime(BigDecimal totalExpenseAmountAllTime) { this.totalExpenseAmountAllTime = totalExpenseAmountAllTime; }

    public BigDecimal getCurrentMonthTotalExpenseAmount() { return currentMonthTotalExpenseAmount; }
    public void setCurrentMonthTotalExpenseAmount(BigDecimal currentMonthTotalExpenseAmount) { this.currentMonthTotalExpenseAmount = currentMonthTotalExpenseAmount; }

    public static DashboardDTOBuilder builder() { return new DashboardDTOBuilder(); }

    public static class DashboardDTOBuilder {
        private long totalUsers;
        private long totalCategories;
        private long totalExpenses;
        private long totalBudgets;
        private BigDecimal totalExpenseAmountAllTime;
        private BigDecimal currentMonthTotalExpenseAmount;

        public DashboardDTOBuilder totalUsers(long totalUsers) { this.totalUsers = totalUsers; return this; }
        public DashboardDTOBuilder totalCategories(long totalCategories) { this.totalCategories = totalCategories; return this; }
        public DashboardDTOBuilder totalExpenses(long totalExpenses) { this.totalExpenses = totalExpenses; return this; }
        public DashboardDTOBuilder totalBudgets(long totalBudgets) { this.totalBudgets = totalBudgets; return this; }
        public DashboardDTOBuilder totalExpenseAmountAllTime(BigDecimal totalExpenseAmountAllTime) { this.totalExpenseAmountAllTime = totalExpenseAmountAllTime; return this; }
        public DashboardDTOBuilder currentMonthTotalExpenseAmount(BigDecimal currentMonthTotalExpenseAmount) { this.currentMonthTotalExpenseAmount = currentMonthTotalExpenseAmount; return this; }

        public DashboardDTO build() {
            return new DashboardDTO(totalUsers, totalCategories, totalExpenses, totalBudgets, totalExpenseAmountAllTime, currentMonthTotalExpenseAmount);
        }
    }
}
