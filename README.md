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

### Build & Run (using Maven)

<!-- Clean and compile -->

```bash
mvn clean compile
```

<!-- Run the application -->

```bash
mvn spring-boot:run
```

<!-- Run Tests -->

```bash
mvn test
```

The server will start at `http://localhost:3001

## Flow to follow for Test Application

Follow this flow to test the main functionalities using Postman.
Remember to copy the postman collection file `BlueSky_Airline.postman_collection.json` in the root of the project and import it in Postman.
This is a simple test flow, but you can test all the endpoints present in the collection.

### 1 - Authentication

**Check Roles**: `GET /roles`, Verify available roles and their codes (ADMIN, FLIGHT_MANAGER, TOUR_OPERATOR).
**Register a User**: `POST /auth/register`, the body is pre-filled in collection if you want.
**Login**: - `POST /auth/login`, the body is pre-filled in collection if you want.

In the postman, after login, there is a script for execute the token in the collection variables. (`collectionVariables.set`)

If you want to change logged User, you can do:
**Logout**: - `POST /auth/logout`, this command clear the token in the collection variables.

### 2 - Core Operations (Requires Token)

**Create some Users**: `POST /users`, (Admin only). Then you can test all the endpoints for users.
**Create at least an Aircraft**: `POST /aircrafts`, (Admin/Tour Operator only). Then you can test all the endpoints for aircrafts.
**Create at least two Airports**: `POST /airports`, (Admin only). Then you can test all the endpoints for airports.
**Test Compartments**: `POST /compartments`, (Admin only). There are already some compartments in the database but you can test all the endpoints for compartments.
**Create at least a Flight**: `POST /flights`, (Admin/Flight Manager only). Then you can test all the endpoints for flights (also external APIs refreshWeather `POST /flights/:id/weather/refresh` and convertPrice `GET /flights/:id/price/convert?target=USD`).
**Create at least a Reservation**: `POST /reservations`, (Admin/Tour Operator only). Then you can test all the endpoints for reservations.

Almost all GET List endpoints are paginated and some are filterable through query parameters.
For some controller, the creation is restricted to Admin role, but other endpoints are available for all or some roles.

### 3 - GraphQL

**List Flights**: `POST /graphql`, List all flights (remember to compile the query in the body).
