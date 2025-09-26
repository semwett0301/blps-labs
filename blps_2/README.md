# Lab 2 — Business Logic: Transactions and Access Control

This module evolves the Lab 1 business process with declarative transaction management and role‑based access control using Spring Security and JWT. It also uses Bitronix as the JTA transaction manager. The domain remains an online book ordering and delivery process.

### Tech stack
- Spring Boot 2.7.2 (Web, Data JPA, Security)
- Java 17, Maven
- PostgreSQL + Hibernate
- JTA with Bitronix (transaction manager)
- Springdoc OpenAPI (Swagger UI)
- JWT (`java-jwt`), HTTP-only cookies, custom filters
- MapStruct, Lombok, Gson

---

## Task description (Variant 73)

Enhance the Lab 1 application by adding transaction management and access control aligned with the stated access policy.

Transaction management requirements:
1. Refactor the teacher‑approved use cases (or new ones by agreement), grouping interdependent operations into transactions.
2. Implement transaction management using Spring JTA.
3. Use declarative transaction management in the implemented/modified use cases.
4. Use Bitronix as the transaction manager.

Access control requirements:
1. Design/specify and agree a set of privileges delineating which operations are available.
2. Specify and agree a set of roles that map to those privileges.
3. Implement the model using Spring Security. Store user accounts in a relational DB and use JWT for authentication.

Execution rules:
1. All changes to the implemented business process must be reflected in its model, REST API, and test requests.
2. Deploy the updated app on the helios server.

Report contents mirror the specification on the screenshot: control‑flow model (BPMN 2.0), role/privilege specification, UML diagrams, full REST API, source code link, and conclusions.

---

## Requirements and run

Environment:
- Java 17
- Maven 3.6+
- PostgreSQL 12+

Build and run locally:
```bash
./mvnw clean package
java -jar target/code-0.0.1-SNAPSHOT.jar
# or
./mvnw spring-boot:run
```

Swagger UI: `/swagger-ui/index.html` (springdoc-openapi).

---

## Database and transactions

`src/main/resources/application.properties` (defaults):
```text
spring.datasource.url=jdbc:postgresql://localhost:5432/blps_2_db
spring.datasource.username=<username>
spring.datasource.password=<password>
spring.jpa.generate-ddl=true
spring.jpa.hibernate.ddl-auto=create
spring.sql.init.mode=never
spring.mvc.pathmatch.matching-strategy=ant_path_matcher
```

JTA/Bitronix:
- Dependency: `spring-boot-starter-jta-bitronix`
- Configuration: see `config/BitronixConfiguration.java` and `config/DataSourceConfiguration.java`
- Declarative transactions are applied at service layer methods via Spring (`@Transactional`).

Override DB creds in production via JVM args or environment variables.

---

## Security and roles (JWT, cookies)

- Spring Security with stateless sessions and JWT.
- Filters: `AuthorizationFilter` (authN), `UpdateTokenFilter` (silent refresh).
- Tokens are delivered in HTTP‑only cookies:
  - `access-token` — short‑lived JWT with authorities
  - `refresh-token` — longer‑lived JWT for refresh
- Roles: `USER`, `COURIER` (see `model.modelUtils.Role` and `/roles` endpoint).
- Access rules (see `security/SecurityConfig.java`):
  - Public: `/auth/login`, `/auth/register`, `/auth/logout`, `/auth/refresh`, `/roles/**`
  - `USER`: POST `/orders`, `/time/**`, GET `/books`
  - `USER` or `COURIER`: GET `/orders`, GET `/orders/{id}`, POST `/orders/{id}/cancel`
  - `COURIER`: POST `/orders/{id}/accept`, POST `/orders/{id}/reject`, POST `/orders/{id}/complete`

Auth flow:
1. Register `/auth/register`.
2. Login `/auth/login` → sets cookies and returns role.
3. Subsequent requests send cookies; access token is refreshed transparently by `UpdateTokenFilter`.
4. Manual refresh available at `/auth/refresh`.
5. Logout clears cookies.

---

## REST API

Base URL: `http://localhost:8080`

### Auth
- POST `/auth/register` — register a user
  - Body: `{ username, password, role }`
- POST `/auth/login` — login and receive JWT cookies
  - Body: `{ username, password }`
  - Response: `ResponseUserAuthorized { role }` + cookies
- POST `/auth/refresh` — refresh tokens using `refresh-token` cookie
- POST `/auth/logout` — clear cookies

### Roles
- GET `/roles` — list available roles

### Books
- GET `/books` — list available books (role: `USER`)

### Orders
- GET `/orders` — list orders of current principal (`USER` or `COURIER`)
- POST `/orders` — create order (role: `USER`)
  - Body: `RequestCreateOrder { day: LocalDate, books: List<ReservedBook> }`
- GET `/orders/{orderId}` — get order
- POST `/orders/{orderId}/accept` — accept order (role: `COURIER`)
- POST `/orders/{orderId}/reject` — reject order (role: `COURIER`)
- POST `/orders/{orderId}/complete` — complete order (role: `COURIER`)
- POST `/orders/{orderId}/cancel` — cancel order (role: `USER` or `COURIER`)

### Time windows
- GET `/time/{orderId}` — available delivery windows
- POST `/time/{orderId}` — set delivery window

Domain exceptions: `OrderNotFoundException`, `BookIsNotAvailableException`, `TimeIsNotAvailableException`, `IncorrectTimePeriodException`, `OrderHasBeenAlreadyAcceptedException`, `TimeHasBeenAlreadyChosenException`, `UserNotFoundException`, `UserAlreadyExistException`.
