# Bank Wallet REST API 💰

A secure banking wallet REST API built with Spring Boot, featuring JWT authentication, currency conversion, money operations, and **Docker containerization with PostgreSQL**.

![Java](https://img.shields.io/badge/Java-17-orange)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.4.2-brightgreen)
![Docker](https://img.shields.io/badge/Docker-Container-blue)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-15-blue)
![JWT](https://img.shields.io/badge/JWT-Security-blue)
![Swagger](https://img.shields.io/badge/Swagger-OpenAPI-green)

---

## 📋 Table of Contents

- [Features](#features)
- [Tech Stack](#tech-stack)
- [Quick Start](#quick-start)
  - [With Docker (Recommended)](#with-docker-recommended)
  - [Without Docker (Local Development)](#without-docker-local-development)
- [API Documentation](#api-documentation)
- [Default Users](#default-users)
- [Multi-Currency Support](#multi-currency-support)
- [Error Handling](#error-handling)
- [Project Structure](#project-structure)
- [Configuration](#configuration)
- [Docker Deployment](#docker-deployment)
- [CI/CD Pipeline](#cicd-pipeline)

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
| **Docker Support** | Containerized with Docker and Docker Compose |
| **PostgreSQL Database** | Production-ready persistent database |
| **Docker Hub** | Pre-built images available |

---

## 🛠 Tech Stack
├── Backend
│ ├── Java 17
│ ├── Spring Boot 3.4.2
│ ├── Spring Security
│ ├── Spring Data JPA
│ └── JWT (jjwt 0.11.5)
│
├── Database
│ ├── PostgreSQL 15 (Production)
│ └── H2 (Local Development)
│
├── External APIs
│ └── Exchange Rates API (APILayer)
│
├── Tools
│ ├── Docker / Docker Compose
│ ├── Maven
│ ├── Lombok
│ └── Swagger/OpenAPI

text

---

## 🚀 Quick Start

### Prerequisites

| Tool | Version | Required For |
|------|---------|--------------|
| Java | 17+ | Local development |
| Docker | 20.10+ | Containerized deployment |
| Docker Compose | 2.0+ | Multi-container setup |
| Maven | 3.6+ | Building (optional with Docker) |

---

### With Docker (Recommended)

#### 1. Pull from Docker Hub

```bash
# Pull the latest image
docker pull ivanivanovg9/bank-wallet-api:latest
2. Run with Docker Compose (PostgreSQL + App)
bash
# Clone the repository
git clone https://github.com/ivanivanovg9/bank-wallet-rest-api.git
cd bank-wallet-rest-api

# Start the application with PostgreSQL
docker-compose up -d

# Check if services are running
docker-compose ps
3. Test the API
bash
# Register a new user
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{"username":"john","email":"john@example.com","password":"password123"}'

# Login
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"john","password":"password123"}'
4. Stop the application
bash
# Stop containers
docker-compose down

# Stop and remove volumes (deletes database data)
docker-compose down -v
Without Docker (Local Development)
1. Clone and build
bash
git clone https://github.com/ivanivanovg9/bank-wallet-rest-api.git
cd bank-wallet-rest-api
mvn clean install
2. Run with H2 database
bash
# Use default H2 database
mvn spring-boot:run
3. Run with PostgreSQL
bash
# Start PostgreSQL (or use Docker)
docker run -d -p 5432:5432 -e POSTGRES_DB=bankdb -e POSTGRES_USER=bankuser -e POSTGRES_PASSWORD=bankpass123 postgres:15-alpine

# Run with PostgreSQL profile
mvn spring-boot:run -Dspring-boot.run.profiles=docker
📚 API Documentation
Swagger UI
Once the application is running, access:

Resource	URL
Swagger UI	http://localhost:8080/swagger-ui/index.html
OpenAPI JSON	http://localhost:8080/v3/api-docs
Actuator Health	http://localhost:8080/actuator/health
Authentication Endpoints (Public)
Register
bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "john",
    "email": "john@example.com",
    "password": "password123"
  }'
Login
bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "john",
    "password": "password123"
  }'
Response:

json
{
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "id": 1,
  "username": "john",
  "email": "john@example.com",
  "role": "USER"
}
Wallet Operations (Authenticated)
All wallet endpoints require the JWT token in the header:

text
Authorization: Bearer <your-token>
Operation	Method	Endpoint
Deposit	POST	/api/wallets/{userId}/deposit
Withdraw	POST	/api/wallets/{userId}/withdraw
Transfer	POST	/api/wallets/transfer?from={userId}
Balance	GET	/api/wallets/{userId}/balance
Deposit Example
bash
curl -X POST http://localhost:8080/api/wallets/1/deposit \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <token>" \
  -d '{"amount": 100}'
Transfer Example
bash
curl -X POST "http://localhost:8080/api/wallets/transfer?from=1" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <token>" \
  -d '{
    "targetUserId": 2,
    "amount": 30
  }'
Admin Endpoints (ADMIN only)
Operation	Method	Endpoint
Get All Users	GET	/api/admin/users
Delete User	DELETE	/api/admin/users/{id}
👥 Default Users (H2 only)
The application creates 5 test users on startup when using H2. All users have password: password

ID	Username	Email	Role	Balance
1	ivan_p	ivan@example.com	USER	1000.00
2	maria_g	maria@example.com	USER	2500.00
3	georgi_d	georgi@example.com	USER	500.00
4	elena_v	elena@example.com	ADMIN	10000.00
5	petar_n	petar@example.com	USER	0.00
Quick Login Examples (H2 only)
Login as USER:

bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username": "ivan_p", "password": "password"}'
Login as ADMIN:

bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username": "elena_v", "password": "password"}'
💱 Multi-Currency Support
The API integrates with Exchange Rates API (APILayer) for real-time currency conversion.

Features
Deposit in USD, GBP, or EUR (default)

Amounts are automatically converted to EUR

All wallets store balances in EUR only

Example (USD Deposit)
bash
curl -X POST http://localhost:8080/api/wallets/1/deposit \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <token>" \
  -d '{
    "amount": 100,
    "currency": "USD"
  }'
Get a Free API Key
Register at apilayer.com

Subscribe to Exchange Rates API (Free plan)

Add your key to environment variables

🔒 Transaction Safety
Transfer operations use pessimistic locking to prevent race conditions:

java
@Lock(LockModeType.PESSIMISTIC_WRITE)
@Query("SELECT w FROM Wallet w WHERE w.user.id = :userId")
This ensures that concurrent transfer requests cannot corrupt wallet balances.

📊 Error Handling
All errors return a consistent JSON format:

json
{
  "status": 404,
  "message": "User not found with ID: 999",
  "timestamp": "2026-02-23T12:34:56.789"
}
HTTP Status Codes
Code	Description
200	Success
201	Created
400	Bad Request (validation error, insufficient funds)
401	Unauthorized (missing/invalid token)
403	Forbidden (insufficient role)
404	Not Found
409	Conflict (username/email already exists)
500	Internal Server Error
📁 Project Structure
text
bank-wallet-rest-api/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/example/bankwalletrestapi/
│   │   │       ├── controllers/     # REST endpoints
│   │   │       ├── services/        # Business logic
│   │   │       ├── repositories/    # Database access
│   │   │       ├── models/          # Entities & DTOs
│   │   │       ├── security/        # JWT & auth
│   │   │       ├── external/        # Currency API
│   │   │       ├── exceptions/      # Error handling
│   │   │       └── utils/           # Helper classes
│   │   └── resources/
│   │       ├── application.properties
│   │       ├── application-docker.properties
│   │       ├── schema.sql.backup
│   │       └── seed-data.sql.backup
│   └── test/
├── Dockerfile
├── docker-compose.yml
├── .dockerignore
├── pom.xml
└── README.md
⚙️ Configuration
Environment Variables
Variable	Description	Default
SPRING_PROFILES_ACTIVE	Spring profile	docker
JWT_SECRET	256-bit secret for JWT signing	mySecretKey...
JWT_EXPIRATION	Token validity in ms	86400000
POSTGRES_DB	Database name	bankdb
POSTGRES_USER	Database user	bankuser
POSTGRES_PASSWORD	Database password	bankpass123
Docker Compose Configuration
yaml
version: '3.8'
services:
  postgres:
    image: postgres:15-alpine
    environment:
      POSTGRES_DB: bankdb
      POSTGRES_USER: bankuser
      POSTGRES_PASSWORD: bankpass123
    ports:
      - "5432:5432"
    volumes:
      - postgres-data:/var/lib/postgresql/data

  app:
    image: ivanivanovg9/bank-wallet-api:latest
    ports:
      - "8080:8080"
    environment:
      SPRING_PROFILES_ACTIVE: docker
    depends_on:
      postgres:
        condition: service_healthy
🐳 Docker Deployment
Build Local Image
bash
# Build the Docker image
docker build -t bank-wallet-api .

# Run with Docker Compose
docker-compose up -d

# View logs
docker-compose logs -f app

# Stop containers
docker-compose down
Push to Docker Hub
bash
# Tag the image
docker tag bank-wallet-api ivanivanovg9/bank-wallet-api:latest
docker tag bank-wallet-api ivanivanovg9/bank-wallet-api:1.0.0

# Push to Docker Hub
docker push ivanivanovg9/bank-wallet-api:latest
docker push ivanivanovg9/bank-wallet-api:1.0.0
Deploy to Production Server
bash
# On production server
docker pull ivanivanovg9/bank-wallet-api:latest
docker-compose up -d
🔄 CI/CD Pipeline
The project includes GitHub Actions workflow for automated builds:

yaml
name: Build and Push Docker Image
on:
  push:
    branches: [ main ]
jobs:
  build:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v3
      - name: Build Docker image
        run: docker build -t ivanivanovg9/bank-wallet-api:latest .
      - name: Push to Docker Hub
        run: |
          docker login -u ${{ secrets.DOCKER_USERNAME }} -p ${{ secrets.DOCKER_PASSWORD }}
          docker push ivanivanovg9/bank-wallet-api:latest
🧪 Complete Test Flow
bash
# 1. Start with Docker Compose
docker-compose up -d

# 2. Register a new user
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{"username":"john","email":"john@example.com","password":"password123"}'

# 3. Login and get token
TOKEN=$(curl -s -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"john","password":"password123"}' \
  | grep -o '"token":"[^"]*' | cut -d'"' -f4)

# 4. Check balance
curl -X GET http://localhost:8080/api/wallets/1/balance \
  -H "Authorization: Bearer $TOKEN"

# 5. Deposit 100 EUR
curl -X POST http://localhost:8080/api/wallets/1/deposit \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{"amount": 100}'

# 6. Deposit 100 USD (auto-converted)
curl -X POST http://localhost:8080/api/wallets/1/deposit \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{
    "amount": 100,
    "currency": "USD"
  }'
📦 Docker Images
Pre-built images are available on Docker Hub:

bash
# Pull the latest version
docker pull ivanivanovg9/bank-wallet-api:latest

# Pull specific version
docker pull ivanivanovg9/bank-wallet-api:1.0.0
🤝 Contributing
Fork the repository

Create a feature branch (git checkout -b feature/amazing-feature)

Commit your changes (git commit -m 'Add amazing feature')

Push to the branch (git push origin feature/amazing-feature)

Open a Pull Request

📝 License
This project is licensed under the MIT License.

🙏 Acknowledgements
Spring Boot

JJWT

PostgreSQL

Docker

Exchange Rates API

🚀 Quick Start Summary
bash
# With Docker (Recommended)
docker pull ivanivanovg9/bank-wallet-api:latest
git clone https://github.com/ivanivanovg9/bank-wallet-rest-api.git
cd bank-wallet-rest-api
docker-compose up -d
