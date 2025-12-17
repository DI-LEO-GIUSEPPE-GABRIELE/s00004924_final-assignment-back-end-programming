# BlueSky Airline – Backend System

This is the backend for the **BlueSky Airline** management system. It provides a robust RESTful API (and GraphQL endpoint) for managing users, flights, reservations, and aircraft. The system is built with **Spring Boot 3** and follows strict architectural patterns.

## 📋 Project Overview

The application handles the core operations of an airline, including:
-   **User Management**: Registration, authentication (JWT), and role-based access control.
-   **Flight Management**: Scheduling, updating, and tracking flights.
-   **Reservations**: Managing booking statuses.
-   **Fleet & Airports**: Managing aircraft and airport data.
-   **External Integrations**: Real-time weather data (OpenWeatherMap) and currency conversion (ExchangeRate-API).

## 🏗️ Architecture & Techniques

This project adheres to specific architectural guidelines and patterns:

-   **Layered Architecture**:
    -   **Controllers**: Handle HTTP requests and map DTOs. They **never** access repositories directly.
    -   **Services**: Encapsulate business logic and transaction management. All controllers inject services.
    -   **Repositories**: Handle data persistence using Spring Data JPA.
-   **Dependency Injection**: Uses **Field Injection** (`@Autowired`) on private fields, consistent throughout the codebase.
-   **DTO Pattern**: Data Transfer Objects are used for API requests and responses to decouple the internal domain model from the external API.
-   **Security**:
    -   **JWT Authentication**: Stateless authentication using JSON Web Tokens.
    -   **RBAC**: Role-Based Access Control (`ADMIN`, `FLIGHT_MANAGER`, `TOUR_OPERATOR`).
    -   **Data Protection**: Sensitive fields (like passwords) are strictly hidden using `@JsonIgnore` and never returned in API responses.

## 📊 Data Model (ERD)

The following diagram represents the current database structure derived from the entity classes:

```mermaid
erDiagram
    USER ||--o{ USER_ROLES : "has roles"
    ROLE ||--o{ USER_ROLES : "assigned to"
    USER ||--|| TOUR_OPERATOR : "is associated with"
    TOUR_OPERATOR ||--o{ RESERVATION : "makes"
    FLIGHT ||--o{ RESERVATION : "has"
    FLIGHT ||--|| WEATHER_DATA : "has current"
    FLIGHT }|--|| AIRPORT : "departs from"
    FLIGHT }|--|| AIRPORT : "arrives at"
    FLIGHT }|--|| AIRCRAFT : "uses"
    AIRCRAFT <|-- PASSENGER_AIRCRAFT : "inherits"
    AIRCRAFT <|-- CARGO_AIRCRAFT : "inherits"

    USER {
        UUID id
        string name
        string surname
        string username
        string email
        string password
        string avatar_url
    }
    ROLE {
        UUID id
        string role_name
    }
    TOUR_OPERATOR {
        UUID id
        string company_name
        string vat_number
    }
    FLIGHT {
        UUID id
        string flight_code
        datetime departure_date
        datetime arrival_date
        decimal base_price
        enum status
    }
    RESERVATION {
        UUID id
        datetime reservation_date
        enum status
        decimal total_price
    }
    AIRPORT {
        UUID id
        string code
        string name
        string city
        string country
    }
    AIRCRAFT {
        UUID id
        string brand
        string model
        string type
    }
    PASSENGER_AIRCRAFT {
        int total_seats
    }
    CARGO_AIRCRAFT {
        int max_load_capacity
    }
    WEATHER_DATA {
        UUID id
        double temperature
        string description
        datetime retrieved_at
    }
```

## 🛠️ Tech Stack

-   **Language**: Java 21
-   **Framework**: Spring Boot 3.5.7
-   **Database**: PostgreSQL
-   **Persistence**: Spring Data JPA / Hibernate
-   **Security**: Spring Security + JJWT
-   **API**: REST & GraphQL
-   **Build Tool**: Maven

## 👥 Roles & Permissions

| Role | Description | Key Permissions |
| :--- | :--- | :--- |
| **ADMIN** | System Administrator | Full access to all resources (Users, Flights, Fleet, etc.). |
| **FLIGHT_MANAGER** | Flight Operations Manager | Can create, update, and delete flights. Can manage weather data. |
| **TOUR_OPERATOR** | External Operator | Can view flights and manage reservations. |

## 🚀 Getting Started

### Prerequisites
-   Java 21 JDK
-   Maven 3.9+
-   PostgreSQL (running on port 5432)

### 1. Database Setup
Create a PostgreSQL database named `bluesky_airline`.
```bash
psql -U postgres -h localhost -c "CREATE DATABASE bluesky_airline;"
```

### 2. Configuration
The application is configured via `src/main/resources/application.properties`.
Key settings:
-   `server.port`: 3001
-   `spring.datasource.*`: DB connection details.
-   `jwt.secret`: Secret key for token signing.
-   `openweather.apiKey`: API key for weather data.
-   `exchangerate.apiKey`: API key for currency conversion.

### 3. Build & Run
```bash
# Clean and compile
mvn clean compile

# Run the application
mvn spring-boot:run
```
The server will start at `http://localhost:3001`.

## 🧪 How to Test (Step-by-Step)

Follow this flow to test the main functionalities using Postman or cURL.

### Phase 1: Authentication
1.  **Register a User** (e.g., as an ADMIN):
    -   `POST /auth/register`
    -   Body: `{"name": "Admin User", "email": "admin@bluesky.com", "password": "password123", "roleCode": 0}`
    -   *Note: roleCode 0=ADMIN, 1=FLIGHT_MANAGER, 2=TOUR_OPERATOR*
2.  **Login**:
    -   `POST /auth/login`
    -   Body: `{"email": "admin@bluesky.com", "password": "password123"}`
    -   **Copy the Token** returned in the response (`{"token": "eyJhbG..."}`).

### Phase 2: Core Operations (Requires Token)
*Add `Authorization: Bearer <YOUR_TOKEN>` header to all subsequent requests.*

3.  **Create a Flight** (Admin/Manager only):
    -   `POST /flights`
    -   Body: JSON representing a flight (ensure related Airport/Aircraft IDs exist or use existing ones).
4.  **List Flights**:
    -   `GET /flights`
    -   Check if your new flight appears.
5.  **Refresh Weather**:
    -   `POST /flights/{id}/weather/refresh`
    -   Fetches live weather for the flight's departure airport.
6.  **Convert Price**:
    -   `GET /flights/{id}/price/convert?target=USD`
    -   Converts the flight price from EUR to USD.

### Phase 3: User Management
7.  **List Users**:
    -   `GET /users`
    -   View all registered users.
8.  **Get User Details**:
    -   `GET /users/{id}`
    -   Verify the `password` field is **not** present in the response (Security check).

## 📡 API Endpoints Summary

### Auth
-   `POST /auth/register` - Register new user
-   `POST /auth/login` - Login and get JWT

### Resources
-   `GET /users` - List users
-   `GET /flights` - List flights
-   `GET /airports` - List airports
-   `GET /aircrafts` - List aircraft
-   `GET /reservations` - List reservations
-   `GET /roles` - List roles
-   `GET /operators` - List tour operators

### GraphQL
-   `POST /graphql` - Query flights via GraphQL

*For a complete list of payloads and examples, import the `BlueSky_Airline.postman_collection.json` file into Postman.*
