# BlueSky Airline – Backend

Spring Boot backend for managing users and flights, with JWT authentication, JPA persistence on PostgreSQL, and REST/GraphQL endpoints.

## Tech Stack

- Java `21`
- Spring Boot `3.5.7` (Web, Data JPA, Security, Actuator, GraphQL)
- Database: PostgreSQL (driver included), optional H2 for development
- Build: Maven (`spring-boot-maven-plugin`)

## Requirements

- Java 21 (JDK)
- Maven 3.9+
- PostgreSQL running (port 5432) with a database `bluesky_airline`

## Configuration

Main properties are in `src/main/resources/application.properties`:

- Server: `server.port=3001`
- Datasource (PostgreSQL):
  - `spring.datasource.url=jdbc:postgresql://127.0.0.1:5432/bluesky_airline`
  - `spring.datasource.username=postgres`
  - `spring.datasource.password=postgres`
  - `spring.jpa.hibernate.ddl-auto=update`
- Security/JWT (development):
  - `jwt.secret=change-me-in-env`
  - `jwt.expiration-seconds=3600`
- External services:
  - OpenWeather: `openweather.apiKey`, `openweather.baseUrl`
  - ExchangeRate: `exchangerate.apiKey`, `exchangerate.baseUrl`
- Bootstrap admin (optional):
  - `bootstrap.admin.email`, `bootstrap.admin.password`

You can override any property via environment variables (Spring Boot notation):

- Examples: `SPRING_DATASOURCE_URL`, `SPRING_DATASOURCE_USERNAME`, `SPRING_DATASOURCE_PASSWORD`, `JWT_SECRET`, `JWT_EXPIRATION_SECONDS`, `OPENWEATHER_APIKEY`, `EXCHANGERATE_APIKEY`, `BOOTSTRAP_ADMIN_EMAIL`, `BOOTSTRAP_ADMIN_PASSWORD`.

### Database

Create the database (macOS/Linux):

```bash
psql -U postgres -h 127.0.0.1 -c "CREATE DATABASE bluesky_airline;"
```

If you prefer in-memory H2, start with overrides:

```bash
mvn -Dspring.datasource.url=jdbc:h2:mem:testdb \
    -Dspring.datasource.driver-class-name=org.h2.Driver \
    -Dspring.jpa.hibernate.ddl-auto=create-drop \
    spring-boot:run
```

## Run the Project

- Development (hot reload via DevTools):

```bash
mvn spring-boot:run
```

- Build and run from JAR:

```bash
mvn -DskipTests package
java -jar target/bluesky-airline-0.0.1-SNAPSHOT.jar
```

- Application listens on `http://localhost:3001/`

## Authentication & Authorization

- Public endpoints: `/auth/**`
- All other endpoints require a JWT Bearer token in `Authorization: Bearer <token>`
- Roles: `ADMIN`, `TOUR_OPERATOR`, `FLIGHT_MANAGER`
- Bootstrap admin: set `bootstrap.admin.email` and `bootstrap.admin.password` to create an admin user on first start

### Auth Flow

- Register:

```bash
curl -X POST http://localhost:3001/auth/register \
  -H 'Content-Type: application/json' \
  -d '{"name":"Alice","email":"alice@example.com","password":"pass"}'
```

- Login (returns JWT):

```bash
curl -X POST http://localhost:3001/auth/login \
  -H 'Content-Type: application/json' \
  -d '{"email":"alice@example.com","password":"pass"}'
```

## REST APIs

### Users

- `GET /users` — single endpoint with pagination/sorting and filters `mode`, `nameContains`, `emailDomain`

```bash
curl 'http://localhost:3001/users?mode=derived&nameContains=al&emailDomain=example.com&size=10&sort=name,asc' \
  -H 'Authorization: Bearer <token>'
```

- `GET /users/{id}` — user detail
- `POST /users` — create user (optional `roleIds`: array of UUIDs)

```bash
curl -X POST http://localhost:3001/users \
  -H 'Authorization: Bearer <token>' -H 'Content-Type: application/json' \
  -d '{"name":"Bob","email":"bob@example.com","roleIds":["<ROLE_UUID>"]}'
```

- `PUT /users/{id}` — update user (optional `roleIds`: array of UUIDs)
- `DELETE /users/{id}` — delete user

### Flights

- `GET /flights` — single endpoint with pagination/sorting and filters `status`, `code`, `from`, `to` (ISO date-time)
- `GET /flights/{id}` — flight detail
- `POST /flights` — create flight (roles: `ADMIN` or `FLIGHT_MANAGER`)
- `PUT /flights/{id}` — update flight (roles: `ADMIN` or `FLIGHT_MANAGER`)
- `DELETE /flights/{id}` — delete flight (roles: `ADMIN` or `FLIGHT_MANAGER`)
- `POST /flights/{id}/weather/refresh` — refresh weather for flight
- `GET /flights/{id}/price/convert?target=USD&base=EUR` — price conversion

```bash
curl 'http://localhost:3001/flights/{id}/price/convert?target=USD&base=EUR' \
  -H 'Authorization: Bearer <token>'
```

## GraphQL

Endpoint: `POST /graphql`

- Example query (cURL):

```bash
curl -X POST http://localhost:3001/graphql \
  -H 'Content-Type: application/json' \
  -d '{"query":"{ flights(page:0, size:5){ id code } }"}'
```

Available queries:

- `flights(page, size)` — paginated flight list
- `flight(id)` — flight detail
- `convertPrice(flightId, base, target)` — price conversion via ExchangeRate API

## Error Handling

Centralized in `ApiExceptionHandler`:

- `VALIDATION_ERROR` — 400 with `message` and `field`
- `BAD_JSON` — 400 on invalid JSON body

<!-- Actuator endpoints removed (not required by assignment) -->

## Code Structure

- Entrypoint: `bluesky.airline.Application`
- Main layers:
  - REST Controllers: `bluesky.airline.controllers.*`
  - GraphQL: `bluesky.airline.graphql.*`
  - Services: `bluesky.airline.services.*`
  - Repositories: `bluesky.airline.repositories.*`
  - Entities: `bluesky.airline.entities.*`
  - Security: `bluesky.airline.security.*`
  - Exceptions: `bluesky.airline.exceptions.*`

## Tests

- Run tests:

```bash
mvn test
```

## Security Notes

- Do not keep `jwt.secret` and API keys in `application.properties` in production; use environment variables or a secrets manager.
- Verify roles and permissions before exposing mutating endpoints in public environments.

## Troubleshooting

- Database connection error: check `SPRING_DATASOURCE_*` and that the DB is reachable.
- 401/403 on protected endpoints: verify JWT in `Authorization` and user roles.
