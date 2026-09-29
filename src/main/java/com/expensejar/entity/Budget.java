package com.expensejar.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
@Table(
    name = "budgets",
    uniqueConstraints = {
        @UniqueConstraint(name = "uk_user_category", columnNames = {"user_id", "category_id"})
    },
    indexes = {
        @Index(name = "idx_budget_user_cat", columnList = "user_id, category_id")
    }
)
public class Budget {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @Column(name = "monthly_limit", nullable = false, precision = 10, scale = 2)
    private BigDecimal monthlyLimit;

    public Budget() {}

    public Budget(Long id, User user, Category category, BigDecimal monthlyLimit) {
        this.id = id;
        this.user = user;
        this.category = category;
        this.monthlyLimit = monthlyLimit;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public Category getCategory() { return category; }
    public void setCategory(Category category) { this.category = category; }

    public BigDecimal getMonthlyLimit() { return monthlyLimit; }
    public void setMonthlyLimit(BigDecimal monthlyLimit) { this.monthlyLimit = monthlyLimit; }

    public static BudgetBuilder builder() { return new BudgetBuilder(); }

    public static class BudgetBuilder {
        private Long id;
        private User user;
        private Category category;
        private BigDecimal monthlyLimit;

        public BudgetBuilder id(Long id) { this.id = id; return this; }
        public BudgetBuilder user(User user) { this.user = user; return this; }
        public BudgetBuilder category(Category category) { this.category = category; return this; }
        public BudgetBuilder monthlyLimit(BigDecimal monthlyLimit) { this.monthlyLimit = monthlyLimit; return this; }

        public Budget build() {
            return new Budget(id, user, category, monthlyLimit);
        }
    }
}
