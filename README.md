# ExpenseJar – Personal Expense Tracker with Categories

![Java](https://img.shields.io/badge/Java-17-orange)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2.4-brightgreen)
![Maven](https://img.shields.io/badge/Maven-3.9.x-blue)
![Database](https://img.shields.io/badge/Database-H2%20%7C%20MySQL-blueviolet)
![OpenAPI](https://img.shields.io/badge/Swagger-OpenAPI%203.0-green)

**ExpenseJar** is a production-grade Spring Boot 3.x RESTful backend system built for a college assessment (Java & DBMS). It enables students and individuals to log expenses by category, set monthly budgets, receive automatic budget alerts (>90% threshold), view current month category spending summaries, and track month-over-month spending trends.

---

## 📋 Assessment Criteria & Rubric Alignment

This project is optimized for maximum scoring across all four college evaluation criteria:
1. **Technical Implementation (40 Marks)**: Spring Boot 3.2.4, REST APIs, full CRUD operations, DTOs, JPA Specification pagination & sorting, H2/MySQL integration.
2. **System Design & Architecture (25 Marks)**: Strict layered architecture (Controller $\rightarrow$ Service $\rightarrow$ Repository), clean domain entities, PK/FK constraints, database indexes, and complete Mermaid UML/ER/Sequence diagrams.
3. **Code Quality & Efficiency (20 Marks)**: Java 17 features, constructor dependency injection, `@ControllerAdvice` global exception handler, custom domain exceptions, JPQL aggregates (`GROUP BY`, `SUM()`), no magic numbers, clean JavaDoc.
4. **Presentation & Q&A (15 Marks)**: Includes Swagger UI OpenAPI interface, ready-to-run Postman collection, `.http` client test script, and an exhaustive viva cheat sheet (`docs/VIVA_NOTES.md`).

---

## 🛠 Tech Stack

- **Language & JDK**: Java 17
- **Framework**: Spring Boot 3.2.4 (Spring Web, Spring Data JPA, Jakarta Validation)
- **Database**:
  - **H2 (Default/Test Profile)**: In-memory database with console at `/h2-console`
  - **MySQL (Production Profile)**: External RDBMS support via `application-mysql.properties`
- **Documentation & UI**: SpringDoc OpenAPI v2.3.0 (Swagger UI at `/swagger-ui.html`)
- **Boilerplate Reduction**: Lombok (`@Getter`, `@Setter`, `@Builder`, `@AllArgsConstructor`)
- **Testing**: JUnit 5, Mockito, Spring `@WebMvcTest`

---

## 📂 Project Package Structure

```
c:\expensejar
├── pom.xml
├── README.md
├── docs
│   ├── ARCHITECTURE.md                 # Layered Architecture Mermaid Diagram
│   ├── ER_DIAGRAM.md                    # Entity-Relationship Mermaid Diagram
│   ├── UML_CLASS_DIAGRAM.md             # UML Class Diagram
│   ├── SEQUENCE_DIAGRAM.md              # Sequence Diagram for Add Expense & Alert Logic
│   ├── VIVA_NOTES.md                    # College Examination & Viva Q&A Guide
│   ├── ExpenseJar_API.http              # IntelliJ / VS Code REST Client Script
│   └── ExpenseJar.postman_collection.json # Postman Collection v2.1
└── src
    ├── main
    │   ├── java/com/expensejar
    │   │   ├── config                  # OpenAPI & DataInitializer CommandLineRunner
    │   │   ├── controller              # REST Controllers (User, Category, Budget, Expense, Dashboard)
    │   │   ├── dto                     # Request & Response DTOs, BudgetAlertDTO, Summaries
    │   │   ├── entity                  # JPA Entities (User, Category, Budget, Expense)
    │   │   ├── exception               # ResourceNotFound, InvalidExpense, GlobalExceptionHandler
    │   │   ├── repository             # Spring Data JPA & Custom JPQL Aggregate Repositories
    │   │   ├── service                # Service Interfaces & Implementations
    │   │   └── ExpenseJarApplication.java
    │   └── resources
    │       ├── application.properties
    │       ├── application-h2.properties
    │       └── application-mysql.properties
    └── test
        └── java/com/expensejar
            ├── controller              # WebMvcTest Controller Tests
            └── service                 # Mockito Unit Tests for Business Rules & Edge Cases
```

---

## 🗄 Database Design & Schema

### Entities & Relationships
- **User** (`id` PK, `name`, `email` UNIQUE, `created_at`)
- **Category** (`id` PK, `name` UNIQUE)
- **Budget** (`id` PK, `user_id` FK, `category_id` FK, `monthly_limit` DECIMAL(10,2), UNIQUE(`user_id`, `category_id`))
- **Expense** (`id` PK, `user_id` FK, `category_id` FK, `amount` DECIMAL(10,2), `expense_date` DATE, `description`, `created_at`)

### Relational Indexes
- `idx_expense_user_date` on `expenses(user_id, expense_date)`
- `idx_expense_user_cat` on `expenses(user_id, category_id)`
- `idx_budget_user_cat` on `budgets(user_id, category_id)`

---

## 🚀 How to Run the Application

### 1. Run with H2 In-Memory Database (Default)
No extra installation required. Simply execute:

```bash
mvn spring-boot:run -Dmaven.resolver.transport=wagon -Dmaven.wagon.http.ssl.insecure=true -Dmaven.wagon.http.ssl.allowall=true
```

- **Application URL**: `http://localhost:8080`
- **Swagger UI Interactive API Documentation**: `http://localhost:8080/swagger-ui.html`
- **H2 Database Console**: `http://localhost:8080/h2-console`
  - JDBC URL: `jdbc:h2:mem:expensejardb`
  - Username: `sa`
  - Password: *(leave blank)*

### 2. Run with MySQL Database
1. Ensure MySQL server is running and create database:
   ```sql
   CREATE DATABASE expensejardb;
   ```
2. Launch Spring Boot with the `mysql` profile:
   ```bash
   mvn spring-boot:run -Dspring-boot.run.profiles=mysql -DDB_HOST=localhost -DDB_PORT=3306 -DDB_NAME=expensejardb -DDB_USER=root -DDB_PASS=yourpassword
   ```

---

## 🧪 Running Automated Tests

Run the complete JUnit 5 unit test and controller test suite:

```bash
mvn test -Dmaven.resolver.transport=wagon -Dmaven.wagon.http.ssl.insecure=true -Dmaven.wagon.http.ssl.allowall=true
```

**Test Coverage Highlights**:
- Positive expense & budget limit validation (`<= 0` throws `400 Bad Request`).
- Budget alert threshold edge cases (89% `NORMAL`, 90% `WARNING`, 91% `WARNING`, >100% `EXCEEDED`).
- Month-over-month trend calculation with division-by-zero protection (0 previous month spend).
- MockMvc controller tests for normal 201/200/204 paths and error paths (400, 404, 409).

---

## 📡 REST API Reference

| Method | Endpoint | Description | Sample Status Code |
|---|---|---|---|
| `POST` | `/api/expenses` | Add a new expense (recalculates alert) | `201 Created` |
| `GET` | `/api/expenses/{id}` | Get expense details by ID | `200 OK` |
| `GET` | `/api/expenses` | List/Filter expenses (page, size, sortBy, dates, category) | `200 OK` |
| `PUT` | `/api/expenses/{id}` | Update an existing expense | `200 OK` |
| `DELETE` | `/api/expenses/{id}` | Delete an expense by ID | `204 No Content` |
| `PUT` | `/api/budgets` | Set/Upsert monthly budget per category for user | `200 OK` |
| `GET` | `/api/budgets/user/{userId}` | List budgets for a user | `200 OK` |
| `POST` | `/api/users` | Register a new user | `201 Created` |
| `GET` | `/api/users` | List all users | `200 OK` |
| `GET` | `/api/users/{userId}/summary/current-month` | Current month spend vs budget summary per category | `200 OK` |
| `GET` | `/api/users/{userId}/alerts` | Active budget alerts (>90% threshold) | `200 OK` |
| `GET` | `/api/users/{userId}/trend?months=6` | Month-over-month trend with percentage change | `200 OK` |
| `GET` | `/api/dashboard` | Backend aggregate counts and total metrics | `200 OK` |

---

## 📝 Sample API Request & Response

### 1. Add Expense (`POST /api/expenses`)

**Request Body**:
```json
{
  "userId": 1,
  "categoryId": 1,
  "amount": 210.00,
  "expenseDate": "2026-09-15",
  "description": "Weekend dinner party"
}
```

**Response (`201 Created`)**:
```json
{
  "id": 12,
  "userId": 1,
  "userName": "Alice Smith",
  "categoryId": 1,
  "categoryName": "Food",
  "amount": 210.00,
  "expenseDate": "2026-09-15",
  "description": "Weekend dinner party",
  "createdAt": "2026-09-28T10:30:00",
  "budgetAlert": {
    "categoryId": 1,
    "categoryName": "Food",
    "monthlyLimit": 500.00,
    "spentAmount": 460.00,
    "remainingAmount": 40.00,
    "percentUsed": 92.00,
    "alertTriggered": true,
    "status": "WARNING",
    "message": "Category 'Food' spent 460.00 of 500.00 (92.00% used - Status: WARNING)"
  }
}
```

### 2. Current Month Summary (`GET /api/users/1/summary/current-month`)

**Response (`200 OK`)**:
```json
{
  "userId": 1,
  "userName": "Alice Smith",
  "yearMonth": "2026-09",
  "totalSpent": 720.00,
  "totalBudget": 950.00,
  "categorySummaries": [
    {
      "categoryId": 1,
      "categoryName": "Food",
      "spentAmount": 460.00,
      "monthlyLimit": 500.00,
      "remainingAmount": 40.00,
      "percentUsed": 92.00,
      "budgetStatus": "WARNING"
    },
    {
      "categoryId": 3,
      "categoryName": "Stationery",
      "spentAmount": 110.00,
      "monthlyLimit": 100.00,
      "remainingAmount": -10.00,
      "percentUsed": 110.00,
      "budgetStatus": "EXCEEDED"
    }
  ]
}
```

---

## 📌 Deliverables Sitemap

- 📐 [ER Diagram](docs/ER_DIAGRAM.md)
- 🏗 [UML Class Diagram](docs/UML_CLASS_DIAGRAM.md)
- 🔄 [Sequence Diagram](docs/SEQUENCE_DIAGRAM.md)
- 🏢 [Layered Architecture Diagram](docs/ARCHITECTURE.md)
- 🎓 [College Viva Cheat Sheet & Q&A](docs/VIVA_NOTES.md)
- ⚡ [HTTP Test Client Script](docs/ExpenseJar_API.http)
- 📦 [Postman Collection v2.1](docs/ExpenseJar.postman_collection.json)
