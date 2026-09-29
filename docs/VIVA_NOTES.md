# ExpenseJar - College Assessment Viva Cheat Sheet & Q&A

This document serves as an exhaustive presentation and viva preparation guide for the **ExpenseJar** Java & DBMS assessment. It explains key architectural decisions, database design rationale, business logic algorithms, and anticipated examiner questions.

---

## 1. Key Architectural & System Design Decisions

### Q1: Why did you use DTOs (Data Transfer Objects) instead of returning JPA Entities directly?
**Answer**:
1. **Security & Data Hiding**: Prevents exposing sensitive domain internals, internal database IDs, or unneeded fields.
2. **Preventing Circular Reference Errors**: Entities with `@ManyToOne` / `@OneToMany` relationships cause infinite recursive JSON serialization loops with Jackson unless managed. DTOs break circular dependencies cleanly.
3. **Decoupling**: Allows database schema changes (e.g., column renaming, table splitting) without breaking external REST API contracts.
4. **Custom Response Structures**: Enables embedding computed fields like `budgetAlert` inside `ExpenseResponseDTO` without dirtying persistent database entities.

### Q2: Why validate business rules in the Service layer when Bean Validation (@NotNull, @Positive) already exists on DTOs?
**Answer**:
- **Two-Line Defense Strategy**:
  - **DTO Bean Validation** acts as the *first line of defense* at the HTTP boundary, catching syntactically invalid JSON payload errors before executing code.
  - **Service-Layer Validation** acts as the *second line of defense*, enforcing domain logic that requires database state (e.g., checking if proposed total spend exceeds category monthly budget limit). Business rules belong in Java service code so that they are enforced regardless of how the service is invoked.

### Q3: How is the strict Budget Limit Cap enforced when logging or updating expenses?
**Answer**:
- Before persisting an expense (`addExpense` or `updateExpense`), the service layer calculates:
  $$\text{proposedTotal} = \text{currentSpentInMonth} + \text{newExpenseAmount}$$
- If a monthly budget limit exists for that user and category and $\text{proposedTotal} > \text{monthlyLimit}$, the service immediately rejects the entry by throwing an `InvalidExpenseException`, returning **400 Bad Request** with a detailed error message (*e.g., "Expense amount exceeds monthly budget limit for category 'Food'. Limit: ₹500.00, Current Spent: ₹460.00, Attempted: ₹100.00"*).

### Q4: Explain LAZY vs EAGER fetching in JPA/Hibernate. What did you choose and why?
**Answer**:
- **EAGER Fetching**: Hibernate immediately loads associated entities using SQL JOINs whenever the parent entity is fetched. This causes severe performance degradation and the **N+1 SELECT Problem**.
- **LAZY Fetching (`FetchType.LAZY`)**: Associated entities are loaded on-demand only when explicitly accessed.
- **Our Design Choice**: We used `FetchType.LAZY` on all `@ManyToOne` relationships. For queries requiring associations, we use **JPQL FETCH JOINs** to retrieve all needed data in a single optimized SQL query.

---

## 2. Core Business Rules & Algorithm Explanations

### Q5: How does the Budget Alert recalculation algorithm work?
**Answer**:
1. When an expense within budget limits is logged, updated, or deleted, total spend for that `userId`, `categoryId`, and `YearMonth` is queried.
2. It computes:
   $$\text{percentUsed} = \left( \frac{\text{spentAmount}}{\text{monthlyLimit}} \right) \times 100$$
3. **Threshold Status Logic**:
   - If $\text{percentUsed} \ge 100.0\% \Rightarrow \text{status} = \text{"EXCEEDED"}$, $\text{alertTriggered} = \text{true}$
   - Else if $\text{percentUsed} \ge 90.0\% \Rightarrow \text{status} = \text{"WARNING"}$, $\text{alertTriggered} = \text{true}$
   - Else $\Rightarrow \text{status} = \text{"NORMAL"}$, $\text{alertTriggered} = \text{false}$

### Q6: How is the Month-over-Month trend calculated, handling 0 spend in the previous month?
**Answer**:
1. The endpoint `GET /api/users/{userId}/trend?months=6` retrieves monthly summaries for the last $N$ months chronologically.
2. **Division-by-Zero Handling**:
   - If $\text{prevSpent} == 0$ and $\text{currentSpent} == 0 \Rightarrow \text{percentChange} = 0.00\%$
   - If $\text{prevSpent} == 0$ and $\text{currentSpent} > 0 \Rightarrow \text{percentChange} = +100.00\%$ (handled safely without throwing `ArithmeticException`).

---

## 3. Database & SQL Query Optimization

### Q7: Why did you use Database Indexes on (user_id, expense_date) and (user_id, category_id)?
**Answer**:
- Prevents Full Table Scans ($O(N)$) during range queries and monthly aggregations.
- **Composite Index `idx_expense_user_date`**: B-Tree lookup ($O(\log N)$) for date-range queries.
- **Composite Index `idx_expense_user_cat`**: Accelerates per-category budget lookups and aggregations.

---

## 4. Quick Viva Presentation Summary

| Feature | Implementation Detail |
|---|---|
| **Tech Stack** | Java 17, Spring Boot 3.2.4, Spring Data JPA, Lombok, Jakarta Validation, H2/MySQL |
| **Strict Budget Rule** | Blocks and rejects expenses if total monthly spend exceeds monthly budget limit |
| **API Docs & UI** | Swagger UI at `/swagger-ui.html` & Web Dashboard at `/dashboard` |
| **Testing** | JUnit 5 & Mockito for unit tests; `@WebMvcTest` for REST controller tests |
