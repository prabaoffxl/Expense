package com.expensejar.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(
    name = "expenses",
    indexes = {
        @Index(name = "idx_expense_user_date", columnList = "user_id, expense_date"),
        @Index(name = "idx_expense_user_cat", columnList = "user_id, category_id")
    }
)
public class Expense {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal amount;

    @Column(name = "expense_date", nullable = false)
    private LocalDate expenseDate;

    @Column(length = 255)
    private String description;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public Expense() {}

    public Expense(Long id, User user, Category category, BigDecimal amount, LocalDate expenseDate, String description, LocalDateTime createdAt) {
        this.id = id;
        this.user = user;
        this.category = category;
        this.amount = amount;
        this.expenseDate = expenseDate;
        this.description = description;
        this.createdAt = createdAt;
    }

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public Category getCategory() { return category; }
    public void setCategory(Category category) { this.category = category; }

    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }

    public LocalDate getExpenseDate() { return expenseDate; }
    public void setExpenseDate(LocalDate expenseDate) { this.expenseDate = expenseDate; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public static ExpenseBuilder builder() { return new ExpenseBuilder(); }

    public static class ExpenseBuilder {
        private Long id;
        private User user;
        private Category category;
        private BigDecimal amount;
        private LocalDate expenseDate;
        private String description;
        private LocalDateTime createdAt;

        public ExpenseBuilder id(Long id) { this.id = id; return this; }
        public ExpenseBuilder user(User user) { this.user = user; return this; }
        public ExpenseBuilder category(Category category) { this.category = category; return this; }
        public ExpenseBuilder amount(BigDecimal amount) { this.amount = amount; return this; }
        public ExpenseBuilder expenseDate(LocalDate expenseDate) { this.expenseDate = expenseDate; return this; }
        public ExpenseBuilder description(String description) { this.description = description; return this; }
        public ExpenseBuilder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }

        public Expense build() {
            return new Expense(id, user, category, amount, expenseDate, description, createdAt);
        }
    }
}
