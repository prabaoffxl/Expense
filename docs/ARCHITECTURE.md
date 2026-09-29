# ExpenseJar - Layered Architecture Diagram

This document details the strict layered architectural design of **ExpenseJar**, following high-cohesion, low-coupling principles, constructor dependency injection, DTO isolation, and centralized error handling.

```mermaid
graph TD
    Client["Client / Postman / Swagger UI / Web Frontend"]

    subgraph Presentation_Layer["Presentation Layer (Controller)"]
        UC["UserController (/api/users)"]
        CC["CategoryController (/api/categories)"]
        BC["BudgetController (/api/budgets)"]
        EC["ExpenseController (/api/expenses)"]
        DC["DashboardController (/api/dashboard)"]
        GEH["GlobalExceptionHandler (@ControllerAdvice)"]
    end

    subgraph DTO_Layer["DTO Layer (Data Transfer Objects)"]
        ReqDTO["Request DTOs (UserRequestDTO, ExpenseRequestDTO, BudgetRequestDTO)"]
        ResDTO["Response DTOs (UserResponseDTO, ExpenseResponseDTO, MonthlySummaryDTO)"]
        AlertDTO["BudgetAlertDTO (90% Threshold & Exceeded Warnings)"]
        ErrDTO["ErrorResponseDTO (Consistent JSON Error Payload)"]
    end

    subgraph Business_Layer["Business Service Layer (Service)"]
        US["UserService / UserServiceImpl"]
        CS["CategoryService / CategoryServiceImpl"]
        BS["BudgetService / BudgetServiceImpl"]
        ES["ExpenseService / ExpenseServiceImpl"]
        DS["DashboardService / DashboardServiceImpl"]
    end

    subgraph Data_Access_Layer["Data Access Layer (Repository)"]
        UR["UserRepository (Spring Data JPA)"]
        CR["CategoryRepository (Spring Data JPA)"]
        BR["BudgetRepository (Spring Data JPA + Fetch Join)"]
        ER["ExpenseRepository (Spring Data JPA + Specification + JPQL Aggregations)"]
    end

    subgraph Entity_Layer["Domain Entity Layer (JPA Entities)"]
        UserEnt["User Entity"]
        CatEnt["Category Entity"]
        BudgetEnt["Budget Entity"]
        ExpenseEnt["Expense Entity"]
    end

    subgraph Database_Layer["Database Layer"]
        H2["H2 In-Memory DB (Dev/Test Profile)"]
        MySQL["MySQL Relational DB (Production Profile)"]
    end

    %% Flow arrows
    Client -->|HTTP REST JSON Requests| Presentation_Layer
    Presentation_Layer -->|Validates Input & Delegates| DTO_Layer
    Presentation_Layer -->|Calls Service Methods| Business_Layer
    Business_Layer -->|Enforces Business Rules & Alert Logic| Data_Access_Layer
    Data_Access_Layer -->|Maps to/from Entities| Entity_Layer
    Data_Access_Layer -->|Executes SQL/JPQL Queries| Database_Layer
    GEH -.->|Catches Exceptions & Returns ErrorResponseDTO| Client
```

## Architectural Design Principles

1. **Strict Layer Separation**: Controllers NEVER communicate directly with Repositories. All access passes through the Service layer.
2. **DTO Isolation**: Entities are strictly encapsulated within the backend persistence layer. REST controllers return and accept DTOs only, preventing internal database schema leaks.
3. **Constructor Injection**: All dependencies are injected via constructors (`final` fields) rather than field `@Autowired`, facilitating clean unit testing with Mockito.
4. **Declarative Transaction Management**: Service methods use `@Transactional` and `@Transactional(readOnly = true)` to ensure atomic operations and optimize database read performance.
5. **Centralized Error Handling**: `@ControllerAdvice` maps custom domain exceptions (`ResourceNotFoundException`, `InvalidExpenseException`, `DuplicateResourceException`) to consistent HTTP response codes (404, 400, 409) and JSON formats.
