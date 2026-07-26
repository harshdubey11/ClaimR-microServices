# ClaimR

A microservices-based expense and budget management platform that enables users to submit expenses and manage multi-level approval workflows with secure JWT authentication.

## Features

- JWT-based user authentication and authorization
- Role-based access control (Employee, Manager, Finance)
- Expense creation, update, submission, and tracking
- Multi-level approval workflow
- State-driven expense lifecycle validation
- Service-to-service communication using Spring Cloud OpenFeign
- Retry and recovery mechanisms using Spring Retry
- Centralized API routing through Spring Cloud Gateway
- Dockerized microservices with PostgreSQL integration

---

## Architecture

```
                 Spring Cloud Gateway
                         |
     ---------------------------------------------
     |                    |                      |
     |                    |                      |
+-------------+   +----------------+   +------------------+
| Auth Service|   | Expense Service|   | Approval Service |
+-------------+   +----------------+   +------------------+
        \                |                  /
         \               |                 /
          \--------------+----------------/
                  PostgreSQL
```

---

## Tech Stack

### Backend

- Java 17
- Spring Boot
- Spring Security
- Spring Data JPA
- Spring Cloud Gateway
- Spring Cloud OpenFeign
- Spring Retry

### Database

- PostgreSQL

### DevOps

- Docker
- Docker Compose

---

## Project Structure

```
ClaimR/
│
├── auth-service/
├── expense-service/
├── approval-service/
├── gateway/
│
├── docker-compose.yml
├── .env.example
├── .gitignore
└── README.md
```

---

## Getting Started

### Clone the repository

```bash
git clone https://github.com/<your-username>/ClaimR.git
cd ClaimR
```

### Configure environment variables

Create a `.env` file using `.env.example`.

### Build the services

```bash
./gradlew build
```

(or build each service individually)

### Run the application

```bash
docker compose up --build
```