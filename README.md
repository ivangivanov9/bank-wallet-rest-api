# Bank Wallet REST API 💰

A secure banking wallet REST API built with Spring Boot, featuring JWT authentication, currency conversion, and money operations.

![Java](https://img.shields.io/badge/Java-17-orange)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.4.2-brightgreen)
![JWT](https://img.shields.io/badge/JWT-Security-blue)
![H2](https://img.shields.io/badge/H2-Database-yellow)
![Swagger](https://img.shields.io/badge/Swagger-OpenAPI-green)

---

## 📋 Table of Contents

- [Features](#features)
- [Tech Stack](#tech-stack)
- [Quick Start](#quick-start)
- [API Documentation](#api-documentation)
- [Default Users](#default-users)
- [Multi-Currency Support](#multi-currency-support)
- [Error Handling](#error-handling)
- [Project Structure](#project-structure)
- [Configuration](#configuration)

---

## ✨ Features

| Feature | Description |
|---------|-------------|
| **JWT Authentication** | Secure stateless authentication |
| **User Management** | Register and manage users |
| **Wallet Operations** | Deposit, withdraw, transfer |
| **Multi-Currency** | Deposit in USD/GBP (auto-converted to EUR) |
| **Role-Based Access** | USER and ADMIN roles |
| **Transaction Safety** | Pessimistic locking for transfers |
| **Swagger UI** | Interactive API documentation |
| **H2 Database** | In-memory with test data |

---

## 🛠 Tech Stack

```
├── Backend
│   ├── Java 17
│   ├── Spring Boot 3.4.2
│   ├── Spring Security
│   ├── Spring Data JPA
│   └── JWT (jjwt 0.11.5)
│
├── Database
│   └── H2 (In-memory)
│
├── External APIs
│   └── Exchange Rates API (APILayer)
│
├── Tools
│   ├── Lombok
│   ├── Maven
│   └── Swagger/OpenAPI
```

---

## 🚀 Quick Start

### Prerequisites

```
Java 17
Maven 3.6+
Git
```

### Installation

```bash
# Clone the repository
git clone https://github.com/yourusername/bank-wallet-rest-api.git
cd bank-wallet-rest-api

# Build the project
mvn clean install

# Run the application
mvn spring-boot:run
```

The application will start at: **http://localhost:8080**

---

## 📚 API Documentation

### Swagger UI

Once the application is running, access:

| Resource | URL |
|----------|-----|
| **Swagger UI** | http://localhost:8080/swagger-ui.html |
| **OpenAPI JSON** | http://localhost:8080/v3/api-docs |

### Authentication Endpoints (Public)

#### Register
```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "john",
    "email": "john@example.com",
    "password": "password123"
  }'
```

#### Login
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "john",
    "password": "password123"
  }'
```

**Response:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "id": 1,
  "username": "john",
  "email": "john@example.com",
  "role": "USER"
}
```

### Wallet Operations (Authenticated)

All wallet endpoints require the JWT token in the header:

```
Authorization: Bearer <your-token>
```

| Operation | Method | Endpoint |
|-----------|--------|----------|
| **Deposit** | `POST` | `/api/wallets/{userId}/deposit` |
| **Withdraw** | `POST` | `/api/wallets/{userId}/withdraw` |
| **Transfer** | `POST` | `/api/wallets/transfer?from={userId}` |
| **Balance** | `GET` | `/api/wallets/{userId}/balance` |

#### Deposit Example
```bash
curl -X POST http://localhost:8080/api/wallets/1/deposit \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <token>" \
  -d '{"amount": 100}'
```

#### Transfer Example
```bash
curl -X POST "http://localhost:8080/api/wallets/transfer?from=1" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <token>" \
  -d '{
    "targetUserId": 2,
    "amount": 30
  }'
```

### Admin Endpoints (ADMIN only)

| Operation | Method | Endpoint |
|-----------|--------|----------|
| **Get All Users** | `GET` | `/api/admin/users` |
| **Delete User** | `DELETE` | `/api/admin/users/{id}` |

---

## 👥 Default Users

The application creates 5 test users on startup. **All users have password: `password`**

| ID | Username | Email | Role | Balance |
|----|----------|-------|------|---------|
| 1 | ivan_p | ivan@example.com | USER | 1000.00 |
| 2 | maria_g | maria@example.com | USER | 2500.00 |
| 3 | georgi_d | georgi@example.com | USER | 500.00 |
| 4 | elena_v | elena@example.com | **ADMIN** | 10000.00 |
| 5 | petar_n | petar@example.com | USER | 0.00 |

### Quick Login Examples

**Login as USER:**
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username": "ivan_p", "password": "password"}'
```

**Login as ADMIN:**
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username": "elena_v", "password": "password"}'
```

---

## 💱 Multi-Currency Support

The API integrates with **Exchange Rates API (APILayer)** for real-time currency conversion.

### Features
- Deposit in **USD**, **GBP**, or **EUR** (default)
- Amounts are automatically converted to **EUR**
- All wallets store balances in EUR only

### Example (USD Deposit)
```bash
curl -X POST http://localhost:8080/api/wallets/1/deposit \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <token>" \
  -d '{
    "amount": 100,
    "currency": "USD"
  }'
```

### Get a Free API Key
1. Register at [apilayer.com](https://apilayer.com)
2. Subscribe to **Exchange Rates API** (Free plan)
3. Add your key to `application.properties`

---

## 🔒 Transaction Safety

Transfer operations use **pessimistic locking** to prevent race conditions:

```java
@Lock(LockModeType.PESSIMISTIC_WRITE)
@Query("SELECT w FROM Wallet w WHERE w.user.id = :userId")
```

This ensures that concurrent transfer requests cannot corrupt wallet balances.

---

## 📊 Error Handling

All errors return a consistent JSON format:

```json
{
  "status": 404,
  "message": "User not found with ID: 999",
  "timestamp": "2026-02-23T12:34:56.789"
}
```

### HTTP Status Codes

| Code | Description |
|------|-------------|
| 200 | Success |
| 201 | Created |
| 400 | Bad Request (validation error, insufficient funds) |
| 401 | Unauthorized (missing/invalid token) |
| 403 | Forbidden (insufficient role) |
| 404 | Not Found |
| 409 | Conflict (username/email already exists) |
| 500 | Internal Server Error |

---

## 📁 Project Structure

```
bank-wallet-rest-api/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/example/bankwalletrestapi/
│   │   │       ├── controllers/     # REST endpoints
│   │   │       ├── services/         # Business logic
│   │   │       ├── repositories/     # Database access
│   │   │       ├── models/           # Entities & DTOs
│   │   │       ├── security/         # JWT & auth
│   │   │       ├── external/         # Currency API
│   │   │       ├── exceptions/       # Error handling
│   │   │       └── utils/            # Helper classes
│   │   └── resources/
│   │       ├── application.properties
│   │       ├── schema.sql
│   │       └── seed-data.sql
│   └── test/
├── pom.xml
└── README.md
```

---

## ⚙️ Configuration

### application.properties

```properties
# JWT Configuration
app.jwt.secret=${JWT_SECRET:your-256-bit-secret-key}
app.jwt.expiration-ms=${JWT_EXPIRATION:86400000}

# Exchange Rates API
apilayer.api-key=${APILAYER_KEY:your-api-key}
apilayer.base-url=${APILAYER_URL:http://api.exchangeratesapi.io/v1/}

# H2 Console
spring.h2.console.enabled=true
spring.h2.console.path=/h2-console
```

### Environment Variables (Production)

| Variable | Description | Required |
|----------|-------------|----------|
| `JWT_SECRET` | 256-bit secret for JWT signing | Yes |
| `APILAYER_KEY` | Exchange Rates API key | Yes |
| `JWT_EXPIRATION` | Token validity in ms | No (default: 24h) |
| `APILAYER_URL` | API base URL | No |

---

## 🧪 Complete Test Flow

```bash
# 1. Login as Ivan
TOKEN=$(curl -s -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username": "ivan_p", "password": "password"}' \
  | grep -o '"token":"[^"]*' | cut -d'"' -f4)

# 2. Check balance
curl -X GET http://localhost:8080/api/wallets/1/balance \
  -H "Authorization: Bearer $TOKEN"

# 3. Deposit 100 EUR
curl -X POST http://localhost:8080/api/wallets/1/deposit \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{"amount": 100}'

# 4. Deposit 100 USD (auto-converted)
curl -X POST http://localhost:8080/api/wallets/1/deposit \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{
    "amount": 100,
    "currency": "USD"
  }'
```

---

## 🚀 Quick Start Summary

```bash
# 1. Clone and run
git clone https://github.com/yourusername/bank-wallet-rest-api.git
cd bank-wallet-rest-api
mvn spring-boot:run

# 2. Login as admin
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username": "elena_v", "password": "password"}'

# 3. Open Swagger UI
open http://localhost:8080/swagger-ui.html
```

---
