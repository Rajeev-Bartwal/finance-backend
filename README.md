# Finance Dashboard Backend

Spring Boot 3 + H2 + JWT backend for a role-based finance dashboard.

---

## Tech Stack

| Layer | Technology |
|---|---|
| Language | Java 17 |
| Framework | Spring Boot 3.2 |
| Security | Spring Security 6 + JWT (jjwt 0.12) |
| ORM | Spring Data JPA + Hibernate |
| Database | H2 (in-memory) |
| API Docs | SpringDoc OpenAPI (Swagger UI) |
| Validation | Jakarta Bean Validation |
| Build | Maven |
| Tests | JUnit 5 + Mockito |

---

## Getting Started

### Prerequisites
- Java 17+
- Maven 3.8+

### Run

```bash
git clone https://github.com/your-username/finance-backend.git
cd finance-backend
./mvnw spring-boot:run
```

Server starts at: **http://localhost:8080**

### Useful URLs

| URL | Description |
|---|---|
| http://localhost:8080/swagger-ui.html | Swagger UI — interactive API docs |
| http://localhost:8080/h2-console | H2 database console |
| http://localhost:8080/api-docs | Raw OpenAPI JSON |

### H2 Console Login

```
JDBC URL:  jdbc:h2:mem:financedb
Username:  sa
Password:  (leave blank)
```

---

## Seeded Accounts

Created automatically on startup:

| Username | Password | Role |
|---|---|---|
| `admin` | `password123` | ADMIN |
| `analyst` | `password123` | ANALYST |
| `viewer` | `password123` | VIEWER |

20 sample transactions are also seeded (income + expenses across the last 3 months).

---

## Access Control

| Action | VIEWER | ANALYST | ADMIN |
|---|---|---|---|
| Login / view profile | ✅ | ✅ | ✅ |
| View transactions | ✅ | ✅ | ✅ |
| Dashboard summary + recent | ✅ | ✅ | ✅ |
| Category breakdown + trends | ❌ | ✅ | ✅ |
| Create / update transactions | ❌ | ✅ | ✅ |
| Delete transactions | ❌ | ❌ | ✅ |
| Manage users | ❌ | ❌ | ✅ |

---

## API Endpoints

### Auth (public)
```
POST /api/auth/register
POST /api/auth/login
GET  /api/auth/me          (requires token)
```

### Users (Admin only)
```
GET    /api/users
GET    /api/users/{id}
PUT    /api/users/{id}
PATCH  /api/users/{id}/status
DELETE /api/users/{id}
```

### Transactions
```
GET    /api/transactions                     (all roles)
GET    /api/transactions/{id}                (all roles)
POST   /api/transactions                     (analyst, admin)
PUT    /api/transactions/{id}                (analyst, admin)
DELETE /api/transactions/{id}                (admin)
```

Filter params for GET /api/transactions:
- `type` — INCOME or EXPENSE
- `category` — partial match
- `start_date` / `end_date` — YYYY-MM-DD
- `search` — matches notes or category
- `page` / `size` — pagination

### Dashboard
```
GET /api/dashboard/summary             (all roles)
GET /api/dashboard/recent              (all roles)
GET /api/dashboard/category-breakdown  (analyst, admin)
GET /api/dashboard/trends?months=6     (analyst, admin)
```

---

## Using Swagger UI

1. Open http://localhost:8080/swagger-ui.html
2. Hit **POST /api/auth/login** with `admin / password123`
3. Copy the token from the response
4. Click **Authorize** (top right), paste: `<your-token>` — Swagger adds the `Bearer` prefix
5. Now all protected endpoints are unlocked

---

## Project Structure

```
src/main/java/com/finance/
├── config/          SecurityConfig, SwaggerConfig, DataSeeder
├── controllers/     AuthController, UserController, TransactionController, DashboardController
├── services/        AuthService, UserService, TransactionService, DashboardService
├── repositories/    UserRepository, TransactionRepository
├── models/          User, Transaction
├── security/        JwtUtil, JwtAuthFilter, CustomUserDetailsService
├── dto/
│   ├── request/     LoginRequest, RegisterRequest, CreateTransactionRequest, etc.
│   └── response/    ApiResponse, UserResponse, TransactionResponse, etc.
├── enums/           Role, TransactionType
└── exception/       GlobalExceptionHandler + custom exceptions
```

---

## Running Tests

```bash
./mvnw test
```

Covers: AuthService, UserService, TransactionService — 14 test cases total.

---

## Design Notes

- **H2 in-memory**: Data resets on every restart. Good for dev/testing, swap to PostgreSQL/MySQL for production by changing the datasource config.
- **Soft delete**: Deleted users and transactions are not removed — `deleted_at` is set instead. Hidden automatically via `@SQLRestriction`.
- **BigDecimal for money**: Avoids floating-point rounding issues.
- **Partial updates**: PUT endpoints only change fields that are non-null in the request body.
- **Swagger**: Full OpenAPI docs at `/swagger-ui.html` with JWT auth built in.
