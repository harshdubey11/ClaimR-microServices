ClaimR

A microservices-based expense and budget management platform that enables users to submit expenses and manage multi-level approval workflows with secure JWT authentication and event-driven email notifications.

Features

JWT-based user authentication and authorization

Role-based access control (Employee, Manager, Finance)

Expense creation, update, submission, and tracking

Multi-level approval workflow

State-driven expense lifecycle validation

Service-to-service communication using Spring Cloud OpenFeign

Retry and recovery mechanisms using Spring Retry

Centralized API routing through Spring Cloud Gateway

Event-driven communication using Apache Kafka

Asynchronous email notifications for approval status changes

Email Service consuming approval events and sending HTML email notifications through Gmail SMTP

Dockerized microservices with PostgreSQL integration

React frontend for authentication, expense management, approvals, and analytics

Nginx serves the production frontend, handles React Router fallback, and proxies /api requests to the API Gateway

Docker Compose orchestration for running the application services together

Architecture

                       React Frontend
                            |
                       Nginx (80)
                            |
                       /api proxy
                            |
                  Spring Cloud Gateway
                       (8080)
                            |
       ---------------------------------------------
       |                    |                      |
+-------------+   +----------------+   +------------------+
| Auth Service|   | Expense Service|   | Approval Service |
|    (9002)   |   |     (9000)     |   |      (9001)      |
+-------------+   +----------------+   +------------------+
       \                  |                    /
        \                 |                   /
         \----------------+------------------/
                         |
                     PostgreSQL
                         |
                Approval Service
                         |
                    Kafka Broker
                         |
             approval-status-changed
                         |
                   Email Service
                      (9003)
                         |
                     Gmail SMTP
                         |
                  Employee's Email

The browser sends requests to the same-origin /api path. Nginx forwards these requests to the API Gateway, and React Router paths fall back to index.html.

The frontend container uses API_GATEWAY_HOST to locate the gateway through the shared Docker Compose network.

The Approval Service publishes approval status change events to the Kafka topic approval-status-changed. The Email Service independently consumes these events and sends email notifications asynchronously. The existing synchronous approval workflow and service-to-service communication remain intact.

Tech Stack

Backend

Java 17

Spring Boot

Spring Security

Spring Data JPA

Spring Cloud Gateway

Spring Cloud OpenFeign

Spring Retry

Spring for Apache Kafka

Database

PostgreSQL

Messaging and Notifications

Apache Kafka

Spring Kafka

Gmail SMTP

JavaMailSender

HTML email notifications

Frontend

React 19, TypeScript, and Vite

Redux Toolkit and RTK Query

React Router and Axios

Material UI and Tailwind CSS

Nginx for static hosting and API reverse proxying

DevOps

Docker

Docker Compose

Multi-stage Docker builds

Nginx

----------------------------------------------
Getting Started

Clone the repository

Configure environment variables

Create a .env file from the backend .env.example and configure the required database, JWT, and service settings.

For the Email Service, configure:

MAIL_USERNAME=your-gmail-address@gmail.com
MAIL_PASSWORD=your-gmail-app-password

The Email Service uses these credentials for Gmail SMTP authentication.

The frontend uses VITE_API_URL=/api during its Vite production build. The Docker build passes this value as a build argument.

The frontend container uses API_GATEWAY_HOST to connect to the API Gateway through the shared Docker network (for example, api-gateway).

Build the services

Build the backend JARs individually using Gradle:

cd expenseService
./gradlew clean build

cd ../approvalService
./gradlew clean build

cd ../authService
./gradlew clean build

The Email Service uses a multi-stage Docker build that generates its JAR internally using Maven.

The frontend also uses a multi-stage Docker build that generates the Vite production build and serves it through Nginx.

Run the application

From the directory containing docker-compose.yml:

docker compose up --build -d

Docker Compose builds the configured service images and starts the application containers, including PostgreSQL, Kafka, backend microservices, API Gateway, Email Service, and frontend.

Publish the frontend container's port 80 as host port 3000 to access the application at:

http://localhost:3000

The API Gateway is available at:

http://localhost:8080

To inspect running containers:

docker compose ps

To view application logs:

docker compose logs -f

To test only the frontend image locally while the API Gateway is available on host port 8080, run from ClaimR-UI:

docker build --build-arg VITE_API_URL=/api -t claimr-frontend .
docker run --rm -p 3000:80 -e API_GATEWAY_HOST=host.docker.internal claimr-frontend
