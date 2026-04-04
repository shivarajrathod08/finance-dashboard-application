<div align="center">

# 💰 Finance Dashboard Application

### A secure, role-based financial data management system built with Spring Boot

[![Java](https://img.shields.io/badge/Java-17+-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)](https://www.oracle.com/java/)
[![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.x-6DB33F?style=for-the-badge&logo=springboot&logoColor=white)](https://spring.io/projects/spring-boot)
[![Spring Security](https://img.shields.io/badge/Spring_Security-JWT-6DB33F?style=for-the-badge&logo=springsecurity&logoColor=white)](https://spring.io/projects/spring-security)
[![MySQL](https://img.shields.io/badge/MySQL-8.0-4479A1?style=for-the-badge&logo=mysql&logoColor=white)](https://www.mysql.com/)
[![Swagger](https://img.shields.io/badge/Swagger-OpenAPI_3-85EA2D?style=for-the-badge&logo=swagger&logoColor=black)](https://swagger.io/)
[![Maven](https://img.shields.io/badge/Maven-3.9+-C71A36?style=for-the-badge&logo=apachemaven&logoColor=white)](https://maven.apache.org/)


[Features](#-features) • [Tech Stack](#-tech-stack) • [Setup](#-setup--installation) • [API Docs](#-api-testing-with-swagger) • [Screenshots](#-screenshots)

</div>

---

## 📋 Description

The **Finance Dashboard Application** is a production-ready backend system that enables secure management of financial records with fine-grained, role-based access control. Built on Spring Boot with JWT authentication, it exposes a clean REST API for creating and querying income and expense records — protected by three distinct user roles: **ADMIN**, **ANALYST**, and **VIEWER**.

The entire database schema is auto-generated on startup via Hibernate/JPA, and every endpoint is documented and testable through an integrated Swagger UI — making it easy to explore, demo, and extend.

> 🎯 Designed with clean architecture, security best practices, and real-world usage patterns in mind.

---

## ✨ Features

| Feature | Description |
|---|---|
| 🔐 **JWT Authentication** | Stateless login with signed Bearer tokens — no session state on the server |
| 👥 **Role-Based Access Control** | Three roles (`ADMIN`, `ANALYST`, `VIEWER`) with per-endpoint `@PreAuthorize` guards |
| 💳 **Financial Record Management** | Create, read, filter, and soft-delete `INCOME` / `EXPENSE` transactions |
| 🛡️ **Secure REST APIs** | All endpoints (except `/auth/**`) require a valid JWT |
| 🗄️ **Auto Database Creation** | Hibernate DDL auto-creates and updates the MySQL schema on startup |
| 📖 **Swagger UI** | Full interactive API documentation at `/swagger-ui.html` with Bearer auth support |
| 📊 **Dashboard Summaries** | Aggregated totals — income, expense, net balance, category breakdowns, monthly trends |
| ⚡ **Rate Limiting** | Per-IP token-bucket rate limiter (60 req/min) to prevent abuse |
| 🧪 **Unit & Integration Tests** | JUnit 5 + Mockito unit tests; MockMvc integration tests against H2 in-memory DB |

---

## 🛠️ Tech Stack

```
Backend
├── Language        →  Java 17+
├── Framework       →  Spring Boot 3.x
├── Security        →  Spring Security 6 + JWT (jjwt 0.12)
├── Persistence     →  Spring Data JPA + Hibernate 6
├── Database        →  MySQL 8.0
├── API Docs        →  SpringDoc OpenAPI 3 (Swagger UI)
├── Rate Limiting   →  Bucket4j 8
├── Build Tool      →  Maven 3.9+
└── Utilities       →  Lombok, SLF4J + Logback
```

---

## 🗂️ Project Structure

```
finance-dashboard/
├── src/
│   ├── main/
│   │   ├── java/com/finance/dashboard/
│   │   │   ├── FinanceDashboardApplication.java
│   │   │   ├── config/
│   │   │   │   ├── SecurityConfig.java              # Spring Security + JWT filter chain
│   │   │   │   ├── OpenApiConfig.java               # Swagger / OpenAPI metadata
│   │   │   │   ├── RateLimitingFilter.java          # Per-IP Bucket4j rate limiter
│   │   │   │   └── DataInitializer.java             # Seeds roles + default admin on startup
│   │   │   ├── controller/
│   │   │   │   ├── AuthController.java              # POST /api/auth/login, /register
│   │   │   │   ├── UserController.java              # ADMIN user management
│   │   │   │   ├── FinancialRecordController.java   # CRUD for financial records
│   │   │   │   └── DashboardController.java         # Aggregated dashboard endpoints
│   │   │   ├── dto/
│   │   │   │   ├── request/                         # LoginRequest, CreateUserRequest, etc.
│   │   │   │   └── response/                        # AuthResponse, UserResponse, ErrorResponse
│   │   │   ├── entity/
│   │   │   │   ├── User.java
│   │   │   │   ├── Role.java
│   │   │   │   └── FinancialRecord.java
│   │   │   ├── enums/
│   │   │   │   ├── RoleName.java                    # VIEWER | ANALYST | ADMIN
│   │   │   │   └── TransactionType.java             # INCOME | EXPENSE
│   │   │   ├── exception/
│   │   │   │   ├── GlobalExceptionHandler.java      # @ControllerAdvice uniform error responses
│   │   │   │   ├── ResourceNotFoundException.java
│   │   │   │   ├── DuplicateResourceException.java
│   │   │   │   └── BadRequestException.java
│   │   │   ├── repository/
│   │   │   │   ├── UserRepository.java
│   │   │   │   ├── RoleRepository.java
│   │   │   │   └── FinancialRecordRepository.java
│   │   │   ├── security/
│   │   │   │   ├── CustomUserDetailsService.java
│   │   │   │   └── jwt/
│   │   │   │       ├── JwtTokenProvider.java
│   │   │   │       ├── JwtAuthenticationFilter.java
│   │   │   │       └── JwtAuthenticationEntryPoint.java
│   │   │   └── service/
│   │   │       ├── AuthService.java + impl/
│   │   │       ├── UserService.java + impl/
│   │   │       ├── FinancialRecordService.java + impl/
│   │   │       └── DashboardService.java + impl/
│   │   └── resources/
│   │       ├── application.properties
│   │       ├── application-test.properties          # H2 config for tests
│   │       └── sample-data.sql
│   └── test/
│       └── java/com/finance/dashboard/
│           |
│           |___ FinancedashboardapplicationApplicationTests
│             
│           
│               
│               
├── pom.xml
|
└── README.md
```

---

## ⚙️ Setup & Installation

### Prerequisites

Ensure the following are installed:

- ✅ [Java 17+](https://adoptium.net/) — verify with `java -version`
- ✅ [Maven 3.9+](https://maven.apache.org/) — verify with `mvn -version`
- ✅ [MySQL 8.0+](https://dev.mysql.com/downloads/) — running locally or via Docker

---

### Step 1 — Clone the Repository

```bash
git clone https://github.com/shivarajrathod08/finance-dashboard-application.git
cd finance-dashboard
```

---

### Step 2 — Configure MySQL

Open `src/main/resources/application.properties` and update your credentials:

```properties
# ── Database ────────────────────────────────────────────────────
spring.datasource.url=jdbc:mysql://localhost:3306/finance_dashboard?createDatabaseIfNotExist=true&useSSL=false&serverTimezone=UTC
spring.datasource.username=root
spring.datasource.password=YOUR_MYSQL_PASSWORD

# ── Hibernate — auto-creates schema on first run ────────────────
spring.jpa.hibernate.ddl-auto=update
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQLDialect

# ── JWT ─────────────────────────────────────────────────────────
app.jwt.secret=your-256-bit-secret-key-here
app.jwt.expiration-ms=86400000
```

> 💡 `createDatabaseIfNotExist=true` means you **do not** need to manually create the database — MySQL creates it automatically on first startup.

---

### Step 3 — Build the Project

```bash
mvn clean install -DskipTests
```

Expected output:

```
[INFO] ------------------------------------------------------------------------
[INFO] BUILD SUCCESS
[INFO] ------------------------------------------------------------------------
[INFO] Total time:  12.345 s
[INFO] Finished at: 2026-04-04T19:28:00+05:30
[INFO] ------------------------------------------------------------------------
```          

---

### Step 4 — Run the Application

```bash
mvn spring-boot:run
```

---

## 🚀 Running the Application

### ✅ Successful Startup Output

On a successful run you will see Tomcat start, the HikariCP connection pool connect to MySQL, and the application ready message — exactly as shown below:

**Database Connection & Security Filter Chain:**

![Console Output — Database Connection](https://github.com/shivarajrathod08/finance-dashboard-application/blob/main/db%20connection.png?raw=true)

```
2026-04-04 11:00:49 [main] INFO  o.s.o.j.p.SpringPersistenceUnitInfo - No LoadTimeWeaver setup: ignoring JPA class transformer
2026-04-04 11:00:49 [main] INFO  com.zaxxer.hikari.HikariDataSource  - HikariPool-1 - Starting...
2026-04-04 11:00:49 [main] INFO  com.zaxxer.hikari.pool.HikariPool   - HikariPool-1 - Added connection com.mysql.cj.jdbc.ConnectionImpl@54defd69
2026-04-04 11:00:49 [main] INFO  com.zaxxer.hikari.HikariDataSource  - HikariPool-1 - Start completed.
```

**Tomcat Started & Application Ready:**

![Console Output — Application Started](https://github.com/shivarajrathod08/finance-dashboard-application/blob/main/tom-cat-started.png?raw=true)

```
2026-04-04 11:00:54 [main] INFO  o.s.b.w.e.tomcat.TomcatWebServer   - Tomcat started on port(s): 8080 (http)
2026-04-04 11:00:54 [main] INFO  c.f.d.FinanceDashboardApplication   - Started FinanceDashboardApplication in 8.981 seconds
```

The app is now live at: **`http://localhost:8080`**

| Resource | URL |
|---|---|
| 📖 Swagger UI | http://localhost:8080/swagger-ui.html |
| 📄 OpenAPI JSON | http://localhost:8080/api-docs |

> 🔑 **Default Admin Credentials** (auto-seeded by `DataInitializer` on first run):
> ```
> Username : admin
> Password : Admin@123
> ```
> ⚠️ Change this password before any production deployment.

---

## 📖 API Testing with Swagger

### Open Swagger UI

Navigate to **[http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)**

![Swagger UI — Full API Overview](https://github.com/shivarajrathod08/finance-dashboard-application/blob/main/swagger%20%20api.png?raw=true)

The UI shows all API groups:
- 🔑 **Authentication** — `/api/auth/login`, `/api/auth/register`
- 👤 **User Management** — ADMIN-only CRUD on users & roles
- 💳 **Financial Records** — Create, filter, search, soft-delete
- 📊 **Dashboard** — Aggregated summaries and monthly breakdowns

---

### Step 1 — Login and Get a JWT Token

**Endpoint:** `POST /api/auth/login`

**Request Body:**
```json
{
  "username": "admin",
  "password": "Admin@123"
}
```

![Swagger — Auth Login Request & Response](https://github.com/shivarajrathod08/finance-dashboard-application/blob/main/user%20login.png?raw=true)

**Response — 200 OK:**
```json
{
  "token": "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJhZG1pbiIsImlhdCI6MTc3NTI4MTEyNiwiZXhwIjoxNzc1MzY3NTI2fQ.OV-WkGSrL4DE-AV1cdZOEQu9YVHs5RyQyOy_55tTEy1wt9N6d2Sgr8vhs2_vWN6KTZBuXyaNP0OuqsQNKBA0Tg",
  "tokenType": "Bearer",
  "username": "admin",
  "role": "ADMIN",
  "expiresAt": "2026-04-05T11:08:46"
}
```

---

### Step 2 — Authorize in Swagger UI

1. Copy the `token` value from the login response
2. Click the **`Authorize 🔒`** button at the top-right of Swagger UI
3. Paste your token into the `bearerAuth` field:
   ```
   eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhZG1pbi...
   ```
4. Click **Authorize** → **Close**

> All subsequent Swagger requests will automatically include `Authorization: Bearer <token>`.

---

### Step 3 — Create a Financial Record

**Endpoint:** `POST /api/records` *(ADMIN only)*

**Request Body:**
```json
{
  "amount": 5000,
  "type": "INCOME",
  "category": "Salary",
  "date": "2026-04-04",
  "description": "Monthly salary"
}
```

![Swagger — Create Record Request & Response](https://github.com/shivarajrathod08/finance-dashboard-application/blob/main/financial%20records.png?raw=true)

**Response — 201 Created:**
```json
{
  "id": 2,
  "amount": 5000,
  "type": "INCOME",
  "category": "Salary",
  "date": "2026-04-04",
  "description": "Monthly salary",
  "createdBy": "admin",
  "createdAt": "2026-04-04T11:14:27.28995",
  "updatedAt": "2026-04-04T11:14:27.28995"
}
```

---

### Role-Based Access Summary

| Endpoint | VIEWER | ANALYST | ADMIN |
|---|:---:|:---:|:---:|
| `POST /api/auth/login` | ✅ | ✅ | ✅ |
| `GET /api/dashboard/summary` | ✅ | ✅ | ✅ |
| `GET /api/records` | ❌ | ✅ | ✅ |
| `POST /api/records` | ❌ | ❌ | ✅ |
| `PUT /api/records/{id}` | ❌ | ❌ | ✅ |
| `DELETE /api/records/{id}` | ❌ | ❌ | ✅ |
| `GET /api/admin/users` | ❌ | ❌ | ✅ |

---

### Error Response Format

All errors return a consistent JSON envelope:

**403 Forbidden** (insufficient role):
```json
{
  "timestamp": "2026-04-04T19:42:00",
  "status": 403,
  "error": "Forbidden",
  "message": "Access denied: insufficient permissions",
  "path": "/api/records"
}
```

**400 Bad Request** (validation failure):
```json
{
  "timestamp": "2026-04-04T19:42:00",
  "status": 400,
  "error": "Bad Request",
  "message": "Validation failed",
  "path": "/api/records",
  "validationErrors": {
    "amount": "Amount must be positive",
    "date": "Date cannot be in the future"
  }
}
```

---

## 🗄️ Database Verification (MySQL)

After running the application, Hibernate auto-creates the schema. You can verify it directly in MySQL:

```sql
USE finance_dashboard;
SHOW TABLES;
SELECT * FROM financial_records;
```

![MySQL — Tables and Records Output](https://github.com/shivarajrathod08/finance-dashboard-application/blob/main/financial%20records.png?raw=true)

**Tables created automatically:**
```
+------------------------------+
| Tables_in_finance_dashboard  |
+------------------------------+
| financial_records            |
| roles                        |
| users                        |
+------------------------------+
3 rows in set (0.01 sec)
```

**Financial records after API calls:**
```
+----+---------+----------+----------------------------+------------+---------+----------------+--------+----------------------------+---------------+
| id | amount  | category | created_at                 | date       | deleted | description    | type   | updated_at                 | created_by_id |
+----+---------+----------+----------------------------+------------+---------+----------------+--------+----------------------------+---------------+
|  1 | 5000.00 | Salary   | 2026-04-01 14:01:02.496229 | 2026-04-01 | 0x00    | Monthly salary | INCOME | 2026-04-01 14:01:02.496229 |             1 |
|  2 | 5000.00 | Salary   | 2026-04-02 14:12:29.195353 | 2026-04-02 | 0x00    | Monthly salary | INCOME | 2026-04-02 14:12:29.195353 |             1 |
|  3 | 5000.00 | Salary   | 2026-04-04 05:44:27.289950 | 2026-04-04 | 0x00    | Monthly salary | INCOME | 2026-04-04 05:44:27.289950 |             1 |   
+----+---------+----------+----------------------------+------------+---------+----------------+--------+----------------------------+---------------+
3 rows in set (0.00 sec)
```

> 📌 Note the `deleted = 0x00` (false) column — records are **soft-deleted** (flagged, never physically removed), preserving a full audit trail.

---

## 📸 Screenshots

### 1. Application Console — Database Connected

![Database Connection](https://github.com/shivarajrathod08/finance-dashboard-application/blob/main/db%20connection.png?raw=true)

> HikariCP connection pool successfully connects to MySQL and the security filter chain (including `JwtAuthenticationFilter`) is registered.

---

### 2. Application Console — Started Successfully

![Console Output](https://github.com/shivarajrathod08/finance-dashboard-application/blob/main/tom-cat-started.png?raw=true)

> Tomcat starts on port 8080 and `FinanceDashboardApplication` is fully loaded in under 9 seconds.

---

### 3. Swagger UI — Full API Overview

![Swagger UI](https://github.com/shivarajrathod08/finance-dashboard-application/blob/main/swagger%20%20api.png?raw=true)

> All API groups — Authentication, User Management, Financial Records, and Dashboard — are visible and fully documented with OpenAPI 3.

---

### 4. Swagger — Login & JWT Response

![Auth Login](https://github.com/shivarajrathod08/finance-dashboard-application/blob/main/user%20login.png?raw=true)

> `POST /api/auth/login` returns a signed JWT token, the user's role, and expiry timestamp in a single response.

---

### 5. Swagger — Create Financial Record

![API Records](https://github.com/shivarajrathod08/finance-dashboard-application/blob/main/financial%20records.png?raw=true)

> `POST /api/records` (ADMIN only) creates an income/expense transaction and returns the persisted record with timestamps.

---

### 6. MySQL — Tables & Records

![MySQL Output](https://github.com/shivarajrathod08/finance-dashboard-application/blob/main/financial%20records.png?raw=true)

> Hibernate auto-generates `financial_records`, `roles`, and `users` tables. Records created via the API appear immediately with soft-delete support.

---

## 🔒 Security Notes

- **Passwords** are hashed with `BCrypt` (strength 12) — never stored in plaintext
- **JWT tokens** are signed with HMAC-SHA256; the secret key is configured via `application.properties` and should be moved to environment variables or a secrets manager (e.g. AWS Secrets Manager, HashiCorp Vault) in production
- **Sessions are stateless** — the server holds zero session state; the JWT is the sole proof of authentication
- **Role enforcement** is applied at the method level via `@PreAuthorize`, not just at the URL level, preventing privilege escalation even from internal calls
- **Soft-delete** ensures financial records are never physically destroyed, supporting audit compliance requirements
- **Rate limiting** (60 requests/min per IP) is enforced before Spring Security to prevent brute-force attacks

---

## 🚀 Future Improvements

- [ ] **Refresh Token** — Add a `/auth/refresh` endpoint so users don't need to re-login after token expiry
- [ ] **Redis Caching** — Cache dashboard aggregate queries to reduce DB load under high traffic
- [ ] **Distributed Rate Limiting** — Replace in-memory Bucket4j with a Redis-backed bucket for multi-node deployments
- [ ] **Email Notifications** — Alert users on login from new IPs or large transactions
- [ ] **Export to CSV/PDF** — Allow ANALYST and ADMIN to export filtered records
- [ ] **Audit Log** — Track every create/update/delete with who did it and when
- [ ] **Docker & Docker Compose** — Containerise the app + MySQL for one-command local setup
- [ ] **CI/CD Pipeline** — GitHub Actions workflow for build, test, and deploy on push

---

## 👨‍💻 Author
**Rathlavath Shivaraj**
