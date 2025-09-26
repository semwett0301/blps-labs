# Lab 1 — Business Logic of Software Systems (BLPS)

This project implements a selected business process of an online book service using Spring Boot, modeled according to BPMN 2.0. Data is stored in PostgreSQL, and the public interface is exposed via REST API.

## Contents
- Business process overview and artifacts
- Requirements and run instructions
- Database configuration
- Stack and architecture
- REST API specification with curl examples
- Testing with Insomnia
- Deployment on helios
- Report contents
- Defense questions

---

## Business process overview

The service supports placing and delivering a book order:
1. The user browses available books.
2. The user creates an order for a chosen day; books are reserved in the warehouse.
3. The system proposes available delivery time windows.
4. The user selects a time window.
5. The system assigns a courier and moves the order to the approval state.
6. The courier accepts or declines the order.
7. If accepted, the order is delivered and completed; if declined, the time is cleared and re-selected.
8. The user can cancel the order until completion.

BPMN/UML materials:
- BPMN: `documentation/bpmn_1.drawio`
- Process illustration: `documentation/src.png`

---

## Requirements and run

Environment:
- Java 11
- Maven 3.6+
- PostgreSQL 12+

Build and run locally:
```bash
# from the blps_1 directory
./mvnw clean package
java -jar target/code-0.0.1-SNAPSHOT.jar
# or
./mvnw spring-boot:run
```

The app listens on port 8080 by default. Swagger UI (if configured) is available at `/swagger-ui.html`.

---

## Database configuration

`src/main/resources/application.properties`:

```text
spring.datasource.driver-Class-Name=org.postgresql.Driver
spring.datasource.url=jdbc:postgresql://localhost:5432/studs
spring.datasource.username=<username>
spring.datasource.password=<password>
spring.jpa.hibernate.ddl-auto=update
spring.mvc.pathmatch.matching-strategy=ant_path_matcher
```

For production, override via environment variables or JVM args:
```bash
java -jar \
  -Dspring.datasource.url="jdbc:postgresql://<host>:5432/<db>" \
  -Dspring.datasource.username="<user>" \
  -Dspring.datasource.password="<pass>" \
  target/code-0.0.1-SNAPSHOT.jar
```

---

## Stack and architecture

- Spring Boot 2.7.2, Spring Web, Spring Data JPA
- PostgreSQL, Hibernate (`ddl-auto=update` for schema generation)
- MapStruct for DTO ↔ Entity mapping
- Lombok for boilerplate reduction
- (Optional) Springfox Swagger 2 for REST documentation

Key packages:
- `com.example.code.api` — REST controllers (`BookController`, `OrderController`)
- `com.example.code.services` — domain services for warehouse/delivery
- `com.example.code.model.entities` — entities (`Book`, `Order`, `Reservation`, `User`)
- `com.example.code.model.dto` — request/response DTOs
- `com.example.code.model.mappers` — MapStruct mappers
- `com.example.code.repositories` — Spring Data repositories

Core statuses/objects:
- `OrderStatus` drives the order lifecycle
- `TimePeriod` describes a delivery window
- Book reservation via `Reservation` and the warehouse service

Authentication (educational): controllers rely on `user_id` cookie (UUID) to associate orders with a user.

---

## REST API specification

Base URL: `http://localhost:8080`

### Books
- GET `/books` — list of available books
  - Response: `List<ResponseAvailableBook>`

### Orders
- GET `/orders` — list of user orders
  - Cookie: `user_id=<uuid>`
  - Response: `List<ResponseOrder>`

- POST `/orders` — create order and reserve books
  - Cookie: `user_id=<uuid>`
  - Body: `RequestCreateOrder { day: LocalDate, books: List<ReservedBook> }`
  - Response: `ResponseCreateOrder`

- GET `/orders/time/{orderId}` — get available delivery windows
  - Response: `ResponseAvailableTime { periods: List<TimePeriod> }`

- POST `/orders/time/{orderId}` — set delivery time and assign courier
  - Body: `TimePeriod { start, end }`
  - Response: selected `TimePeriod`

- POST `/orders/acceptance/{orderId}` — accept order (courier)
- DELETE `/orders/acceptance/{orderId}` — decline order (courier)

- GET `/orders/{orderId}` — get order
- PATCH `/orders/{orderId}` — complete order
- DELETE `/orders/{orderId}` — cancel order

Error codes map to domain exceptions: `OrderNotFound`, `BookIsNotAvailable`, `TimeIsNotAvailable`, `IncorrectTimePeriod`, `OrderHasBeenAlreadyAccepted`, `OrderHasBeenAlreadyOnApprove`, `OrderHasntBeenAccepted`.

```bash
# 1) List books
curl -s http://localhost:8080/books | jq .

# 2) List orders for a user
curl -s --cookie "user_id=1b4e28ba-2fa1-11d2-883f-0016d3cca427" \
  http://localhost:8080/orders | jq .

# 3) Create an order (reserve books)
curl -s -X POST http://localhost:8080/orders \
  --cookie "user_id=1b4e28ba-2fa1-11d2-883f-0016d3cca427" \
  -H 'Content-Type: application/json' \
  -d '{
        "day": "2025-09-30",
        "books": [ { "bookId": 1, "count": 1 }, { "bookId": 2, "count": 2 } ]
      }' | jq .

# 4) Available time windows for order 42
curl -s http://localhost:8080/orders/time/42 | jq .

# 5) Set a delivery window
curl -s -X POST http://localhost:8080/orders/time/42 \
  -H 'Content-Type: application/json' \
  -d '{ "start": "2025-09-30T10:00:00", "end": "2025-09-30T12:00:00" }' | jq .

# 6) Accept order (courier)
curl -s -X POST http://localhost:8080/orders/acceptance/42 -i

# 7) Decline order (courier)
curl -s -X DELETE http://localhost:8080/orders/acceptance/42 -i

# 8) Complete order
curl -s -X PATCH http://localhost:8080/orders/42 -i

# 9) Cancel order
curl -s -X DELETE http://localhost:8080/orders/42 -i
```