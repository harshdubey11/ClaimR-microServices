# ClaimR

A microservices-based expense and budget management platform that enables users to submit expenses and manage multi-level approval workflows with secure JWT authentication and event-driven email notifications.

## Features

- JWT-based user authentication and authorization
- Role-based access control for employees, managers, and finance users
- Expense creation, update, submission, and tracking
- Multi-level approval workflow with expense lifecycle validation
- Service-to-service communication using Spring Cloud OpenFeign
- Retry and recovery mechanisms using Spring Retry
- Centralized API routing through Spring Cloud Gateway
- Event-driven communication using Apache Kafka
- Asynchronous email notifications for approval status changes
- Email Service consumes approval events and sends HTML email through Gmail SMTP
- PostgreSQL-backed microservices
- React frontend for authentication, expenses, approvals, and analytics
- Nginx serves the frontend, supports React Router fallback, and proxies `/api` to the gateway
- Docker Compose orchestration for application services

## Architecture

```text
                         React Frontend
                              |
                          Nginx (80)
                              |
                          /api proxy
                              |
                  Spring Cloud Gateway (8080)
                              |
       -------------------------------------------------
       |                     |                       |
+--------------+   +-----------------+   +-------------------+
| Auth Service |   | Expense Service |   | Approval Service  |
|    (9002)    |   |     (9000)      |   |      (9001)       |
+--------------+   +-----------------+   +-------------------+
       \                    |                      /
        \                   |                     /
         ---------------- PostgreSQL -------------
                              |
                       Approval Service
                              |
                        Kafka Broker
                              |
                 approval-status-changed
                              |
                        Email Service (9003)
                              |
                          Gmail SMTP
                              |
                       Employee's email
```

The browser sends requests to the same-origin `/api` path. Nginx forwards those requests to the API Gateway and falls back to `index.html` for React Router routes. The frontend container uses `API_GATEWAY_HOST` to locate the gateway on the shared Docker Compose network.

The Approval Service publishes approval status change events to the Kafka topic `approval-status-changed`. The Email Service consumes those events and sends email notifications asynchronously. The synchronous approval workflow and existing service-to-service communication remain in place.

## Tech Stack

### Backend

- Java 17
- Spring Boot
- Spring Security
- Spring Data JPA
- Spring Cloud Gateway
- Spring Cloud OpenFeign
- Spring Retry
- Spring for Apache Kafka

### Database

- PostgreSQL

### Messaging and Notifications

- Apache Kafka and Spring Kafka
- Gmail SMTP
- `JavaMailSender`
- HTML email notifications

### Frontend

- React 19, TypeScript, and Vite
- Redux Toolkit and RTK Query
- React Router and Axios
- Material UI and Tailwind CSS
- Nginx for static hosting and API reverse proxying

### DevOps

- Docker and Docker Compose
- Multi-stage Docker builds
- Nginx

## Getting Started

### Clone the repository

```bash
git clone https://github.com/<your-username>/ClaimR.git
cd ClaimR
```

### Configure environment variables

Create a `.env` file from the backend `.env.example` and configure the required database, JWT, and service settings.

For the Email Service, configure Gmail credentials using an app password:

```env
MAIL_USERNAME=your-gmail-address@gmail.com
MAIL_PASSWORD=your-gmail-app-password
```

The frontend uses `VITE_API_URL=/api` during its Vite production build. The Docker build accepts this value as a build argument. At runtime, set `API_GATEWAY_HOST` to the API Gateway's Compose service name on the shared network, for example `api-gateway`.

### Build the services

Build the backend JARs individually with Gradle:

```bash
cd expenseService
./gradlew clean build

cd ../approvalService
./gradlew clean build

cd ../authService
./gradlew clean build
```

The Email Service builds its JAR in its multi-stage Docker build. The frontend also uses a multi-stage Docker build to generate Vite production assets and serve them with Nginx.

### Run the application

From the directory containing `docker-compose.yml`:

```bash
docker compose up --build -d
```

Docker Compose builds and starts the configured services, including PostgreSQL, Kafka, backend microservices, API Gateway, Email Service, and frontend.

Publish the frontend container's port `80` as host port `3000` to access the application at [http://localhost:3000](http://localhost:3000). The API Gateway is available at [http://localhost:8080](http://localhost:8080).

Inspect running containers and follow application logs with:

```bash
docker compose ps
docker compose logs -f
```

To test only the frontend image from the `ClaimR-UI` directory while the API Gateway is available on host port `8080`:

```bash
docker build --build-arg VITE_API_URL=/api -t claimr-frontend .
docker run --rm -p 3000:80 -e API_GATEWAY_HOST=host.docker.internal claimr-frontend
```
