# ExpenseJar - Sequence Diagram: Add Expense & Alert Recalculation

This sequence diagram depicts the end-to-end execution flow when a client submits `POST /api/expenses`. It showcases DTO validation, service-layer business rules enforcement, database persistence, JPQL budget alert recalculation, and HTTP response assembly.

```mermaid
sequenceDiagram
    autonumber
    actor Client
    participant Controller as ExpenseController
    participant Service as ExpenseServiceImpl
    participant UserRepo as UserRepository
    participant CatRepo as CategoryRepository
    participant ExpRepo as ExpenseRepository
    participant BudgeRepo as BudgetRepository
    participant Database as Database (H2 / MySQL)

    Client->>Controller: POST /api/expenses (ExpenseRequestDTO)
    Note over Controller: Spring validates DTO (@NotNull, @Positive)

    alt DTO Validation Fails
        Controller-->>Client: 400 Bad Request (ErrorResponseDTO with fieldErrors)
    else DTO Validation Passes
        Controller->>Service: addExpense(requestDTO)
        Note over Service: Service validates amount > 0 & date != null

        alt Business Rule Violation (amount <= 0)
            Service-->>Controller: throw InvalidExpenseException
            Controller-->>Client: 400 Bad Request (ErrorResponseDTO)
        else Business Rule Passes
            Service->>UserRepo: findById(userId)
            UserRepo->>Database: SELECT * FROM users WHERE id = ?
            Database-->>UserRepo: User entity (or empty)

            alt User Not Found
                UserRepo-->>Service: Optional.empty()
                Service-->>Controller: throw ResourceNotFoundException
                Controller-->>Client: 404 Not Found (ErrorResponseDTO)
            end

            Service->>CatRepo: findById(categoryId)
            CatRepo->>Database: SELECT * FROM categories WHERE id = ?
            Database-->>CatRepo: Category entity (or empty)

            Service->>ExpRepo: save(expense)
            ExpRepo->>Database: INSERT INTO expenses (...) VALUES (...)
            Database-->>ExpRepo: Saved Expense entity

            Note over Service: Recalculate Budget Alert for Category & Month
            Service->>BudgeRepo: findByUserIdAndCategoryId(userId, categoryId)
            BudgeRepo->>Database: SELECT * FROM budgets WHERE user_id = ? AND category_id = ?
            Database-->>BudgeRepo: Budget entity (monthlyLimit = 1000.00)

            Service->>ExpRepo: sumTotalSpentByUserAndCategoryAndDateRange(userId, categoryId, start, end)
            ExpRepo->>Database: SELECT SUM(amount) FROM expenses WHERE ...
            Database-->>ExpRepo: spent = 920.00

            Note over Service: percentUsed = (920 / 1000) * 100 = 92.00%<br/>alertTriggered = true (>= 90%)<br/>status = "WARNING"

            Service->>Service: Assemble ExpenseResponseDTO with BudgetAlertDTO
            Service-->>Controller: ExpenseResponseDTO
            Controller-->>Client: 201 Created (JSON Response with budgetAlert)
        end
    end
```
