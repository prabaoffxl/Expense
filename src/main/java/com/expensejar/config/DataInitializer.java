package com.expensejar.config;

import com.expensejar.entity.Budget;
import com.expensejar.entity.Category;
import com.expensejar.entity.Expense;
import com.expensejar.entity.User;
import com.expensejar.repository.BudgetRepository;
import com.expensejar.repository.CategoryRepository;
import com.expensejar.repository.ExpenseRepository;
import com.expensejar.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;
import java.time.YearMonth;

@Configuration
public class DataInitializer {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

    @Bean
    public CommandLineRunner initData(UserRepository userRepository,
                                      CategoryRepository categoryRepository,
                                      BudgetRepository budgetRepository,
                                      ExpenseRepository expenseRepository) {
        return args -> {
            if (userRepository.count() > 0) {
                log.info("Database already seeded. Skipping initial data loading.");
                return;
            }

            log.info("Seeding initial data for ExpenseJar college assessment...");

            // 1. Create Users
            User alice = userRepository.save(User.builder()
                    .name("Alice Smith")
                    .email("alice@example.com")
                    .build());

            User bob = userRepository.save(User.builder()
                    .name("Bob Jones")
                    .email("bob@example.com")
                    .build());

            // 2. Create Categories
            Category food = categoryRepository.save(Category.builder().name("Food").build());
            Category travel = categoryRepository.save(Category.builder().name("Travel").build());
            Category stationery = categoryRepository.save(Category.builder().name("Stationery").build());
            Category entertainment = categoryRepository.save(Category.builder().name("Entertainment").build());

            // 3. Create Budgets
            // Alice's budgets
            budgetRepository.save(Budget.builder().user(alice).category(food).monthlyLimit(new BigDecimal("500.00")).build());
            budgetRepository.save(Budget.builder().user(alice).category(travel).monthlyLimit(new BigDecimal("200.00")).build());
            budgetRepository.save(Budget.builder().user(alice).category(stationery).monthlyLimit(new BigDecimal("100.00")).build());
            budgetRepository.save(Budget.builder().user(alice).category(entertainment).monthlyLimit(new BigDecimal("150.00")).build());

            // Bob's budgets
            budgetRepository.save(Budget.builder().user(bob).category(food).monthlyLimit(new BigDecimal("600.00")).build());
            budgetRepository.save(Budget.builder().user(bob).category(travel).monthlyLimit(new BigDecimal("300.00")).build());

            // 4. Create Historical Expenses across current month, last month, and 2 months ago
            YearMonth now = YearMonth.now();
            YearMonth lastMonth = now.minusMonths(1);
            YearMonth twoMonthsAgo = now.minusMonths(2);

            // 2 Months Ago Expenses
            expenseRepository.save(Expense.builder()
                    .user(alice).category(food).amount(new BigDecimal("120.00"))
                    .expenseDate(twoMonthsAgo.atDay(5)).description("Grocery shopping").build());
            expenseRepository.save(Expense.builder()
                    .user(alice).category(food).amount(new BigDecimal("200.00"))
                    .expenseDate(twoMonthsAgo.atDay(18)).description("Dining out").build());
            expenseRepository.save(Expense.builder()
                    .user(alice).category(travel).amount(new BigDecimal("140.00"))
                    .expenseDate(twoMonthsAgo.atDay(10)).description("Monthly bus pass").build());
            expenseRepository.save(Expense.builder()
                    .user(alice).category(stationery).amount(new BigDecimal("70.00"))
                    .expenseDate(twoMonthsAgo.atDay(12)).description("Notebooks and pens").build());

            expenseRepository.save(Expense.builder()
                    .user(bob).category(food).amount(new BigDecimal("400.00"))
                    .expenseDate(twoMonthsAgo.atDay(15)).description("Cafeteria card refill").build());

            // Last Month Expenses
            expenseRepository.save(Expense.builder()
                    .user(alice).category(food).amount(new BigDecimal("380.00"))
                    .expenseDate(lastMonth.atDay(8)).description("Supermarket & Snacks").build());
            expenseRepository.save(Expense.builder()
                    .user(alice).category(travel).amount(new BigDecimal("180.00"))
                    .expenseDate(lastMonth.atDay(14)).description("Taxi & Train tickets").build());
            expenseRepository.save(Expense.builder()
                    .user(alice).category(entertainment).amount(new BigDecimal("120.00"))
                    .expenseDate(lastMonth.atDay(20)).description("Movie tickets & Popcorn").build());

            expenseRepository.save(Expense.builder()
                    .user(bob).category(food).amount(new BigDecimal("450.00"))
                    .expenseDate(lastMonth.atDay(10)).description("Monthly mess charges").build());
            expenseRepository.save(Expense.builder()
                    .user(bob).category(travel).amount(new BigDecimal("200.00"))
                    .expenseDate(lastMonth.atDay(22)).description("Gas refill").build());

            // Current Month Expenses (designed to trigger WARNING alerts within budget limits)
            // Alice Food: 250.00 + 210.00 = 460.00 / 500.00 = 92% (WARNING alert)
            expenseRepository.save(Expense.builder()
                    .user(alice).category(food).amount(new BigDecimal("250.00"))
                    .expenseDate(now.atDay(2)).description("Monthly pantry stock").build());
            expenseRepository.save(Expense.builder()
                    .user(alice).category(food).amount(new BigDecimal("210.00"))
                    .expenseDate(now.atDay(15)).description("Weekend party catering").build());

            // Alice Travel: 150.00 / 200.00 = 75% (NORMAL)
            expenseRepository.save(Expense.builder()
                    .user(alice).category(travel).amount(new BigDecimal("150.00"))
                    .expenseDate(now.atDay(5)).description("Subway pass").build());

            // Alice Stationery: 95.00 / 100.00 = 95% (WARNING alert, within limit)
            expenseRepository.save(Expense.builder()
                    .user(alice).category(stationery).amount(new BigDecimal("95.00"))
                    .expenseDate(now.atDay(12)).description("Engineering drawing kit & textbook").build());

            // Bob Travel: 290.00 / 300.00 = 96.67% (WARNING alert, within limit)
            expenseRepository.save(Expense.builder()
                    .user(bob).category(travel).amount(new BigDecimal("290.00"))
                    .expenseDate(now.atDay(7)).description("Intercity train trip").build());

            log.info("Data seeding successfully completed! 2 users, 4 categories, 6 budgets, and 12 expenses created.");
        };
    }
}
