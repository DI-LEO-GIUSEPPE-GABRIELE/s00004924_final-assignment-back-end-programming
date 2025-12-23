# BlueSky Airline – Backend Server

This is the backend server for the "BlueSky Airline" management system. It provides a RESTful API and GraphQL structure for managing users, flights, reservations, airports and aircraft. The system is built with Spring Boot and Maven.
The application implements a Test Driven Development (TDD) approach to ensure quality and robustness.

## Project Overview

The application handles the core operations of an airline company managent system, including:

**Roles Management**: Each user is assigned role (ADMIN, FLIGHT_MANAGER, TOUR_OPERATOR) that determines their access level.
**User Management**: Registration, authentication and users management for role ADMIN.
**Flight Management**: Full flights management for roles FLIGHT_MANAGER and ADMIN.
**Compartment Management**: Full compartments management for role ADMIN.
**Aircraft and Airports**: Full aircrafts and airports management for roles ADMIN and TOUR_OPERATOR.
**Reservations**: Full reservations management for roles ADMIN and TOUR_OPERATOR.

## Data Model (ERD diagram)

The data model is represented by an Entity-Relationship Diagram (ERD) that shows the relationships between the different entities in the system. The ERD is shown in the figure in the route of the project. (erd-diagram.png)

### Database Setup

**In terminal**

Create a PostgreSQL database named `bluesky_airline`.

```bash
psql -U postgres -h localhost -c "CREATE DATABASE bluesky_airline;"
```

**In Editor (PgAdmin or DBeaver or similar)**

Create a new database connection with this details (you can change this in application.properties):

- Host: `localhost`
- Port: `5432`
- Database: `bluesky_airline`
- Username: `postgres`
- Password: `postgres`

### Configuration

The application is configured in application.properties.
Keywords for setup:

- `server.port`: 3001
- `spring.datasource`: DB connection details (in previous step).
- `jwt.secret`: Secret key for token signing (generate a secure random key long enough 256 bits).
- `openweather.apiKey`: API key for weather data (my API key is in application.properties but you can get it from https://openweathermap.org/).
- `exchangerate.apiKey`: API key for currency conversion (my API key is in application.properties but you can get it from https://www.exchangerate-api.com/).

### 3. Build & Run

```bash
# Clean and compile
mvn clean compile

# Run the application
mvn spring-boot:run
```

The server will start at `http://localhost:3001
.

## 🧪 How to Test (Step-by-Step)

Follow this flow to test the main functionalities using Postman
or cURL.

### Phase 1: Authentication

1.  **Register a User** (e.g., as an ADMIN): - `POST /auth/register` - Body: `{"name": "Admin User", "email": "admin@bluesky.com", "password": "password123", "roleCode": 0}` - _Note: roleCode 0=ADMI
    , 1=FLIGHT_MANAGER, 2=TOUR_OPERATOR_
2.  **Logi\_**:
    - `POST /auth/login`
    - Body: `{"email": "admin@bluesky.com", "password": "password123"}`
    - **Co_y the Token** returned in the response (`{"token": "eyJhbG..."}`).

### Phase 2: Core Operations (Requires Token)

_Add `Authorization: Bearer <YOUR_TOKEN>` header to all subsequent requests._

3.  **Create a Flight** (Admin/Manager only):
    - `POST /flights`
    - Body: JSON representing a flight (ensure related Airport/Aircraft IDs exist or use existing ones).
4.  **List Flights**:
    - `GET /flights`
    - Check if your new flight appears.
5.  **Refresh Weather**:
    - `POST /flights/{id}/weather/refresh`
    - Fetches live weather for the flight's departure airport.
6.  **Convert Price**:
    - `GET /flights/{id}/price/convert?target=USD`
    - Converts the flight price from EUR to USD.

### Phase 3: User Management

7.  **List Users**:
    - `GET /users`
    - View all registered users.
8.  **Get User Details**:
    - `GET /users/{id}`
    - Verify the `password` field is **not** present in the response (Security check).

## 📡 API Endpoints Summary

### Auth

- `POST /auth/register` - Register new user
- `POST /auth/login` - Login and get JWT

### Resources

- `GET /users` - List users
- `GET /flights` - List flights
- `GET /airports` - List airports
- `GET /aircrafts` - List aircraft
- `GET /reservations` - List reservations
- `GET /roles` - List roles
- `GET /operators` - List tour operators

### GraphQL

- `POST /graphql` - Query flights via GraphQL

_For a complete list of payloads and examples, import the `BlueSky_Airline.postman_collection.json` file into Postman._
