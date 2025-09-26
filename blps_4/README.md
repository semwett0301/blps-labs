# Lab 4 — BPMN Orchestration + Async/Distributed Processing

This module builds on Lab 3 by orchestrating the business process with BPMN (Camunda) while keeping async/distributed messaging and notifications. The project consists of `order-service` (Camunda orchestrator + REST + Kafka producer + email) and `mail-service` (Kafka consumer + scheduler).

### Tech stack
- Spring Boot (Web, Data JPA, Security in order-service)
- Java 17, Maven
- PostgreSQL + Hibernate
- Camunda BPM Platform (BPMN 2.0 orchestration, JavaDelegates)
- Apache Kafka (producer in order-service, consumer in mail-service)
- Spring for Apache Kafka
- Spring Scheduling (`@Scheduled`)
- Jackson, MapStruct, Lombok

---

## Task description

- Model key use cases of the system in BPMN 2.0 and orchestrate them with Camunda.
- Keep asynchronous publish/subscribe communication between services via Kafka.
- Support distributed processing between two independent nodes (`order-service`, `mail-service`).
- Implement a periodic notification job using Spring Scheduling.
- Provide REST API, role model, and deploy on helios.

BPMN models are under `order-service/src/main/resources/processes/`:
- `createOrder.bpmn`, `deliveryOrder.bpmn`, `login.bpmn`, `registerProccess.bpmn`, `broadcast.bpmn`.

---

## Requirements and run

Environment:
- Java 17
- Maven 3.6+
- PostgreSQL 12+
- Kafka + ZooKeeper (for local run)

Build and run locally:
```bash
# order-service
./mvnw -f order-service clean package
java -jar order-service/target/code-0.0.1-SNAPSHOT.jar

# mail-service
./mvnw -f mail-service clean package
java -jar mail-service/target/mail-service-0.0.1-SNAPSHOT.jar
```

Swagger UI (order-service): `/swagger-ui/index.html`.
Camunda web app (default demo user from properties): `demo/demo`.

---

## Database and integrations

`order-service/src/main/resources/application.properties` (highlights):
```text
spring.datasource.url=jdbc:postgresql://localhost:5432/blps_3_db
spring.jpa.hibernate.ddl-auto=create
spring.kafka.producer.bootstrap-servers=localhost:9092
spring.kafka.producer.acks=all
spring.kafka.producer.retries=1

camunda.bpm.admin-user.id=demo
camunda.bpm.admin-user.password=demo

spring.mail.host=<smtp-host>
spring.mail.port=<smtp-port>
spring.mail.username=<smtp-user>
spring.mail.password=<smtp-pass>
spring.mail.protocol=smtps
mail.topic=Change order status
```

`mail-service/src/main/resources/application.properties` (similar to Lab 3 consumer with SMTP + Kafka consumer settings).

---

## Security and roles (same as Lab 2/3)

- Stateless security with JWT cookies.
- Roles: `USER`, `COURIER`.
- Access rules mirror `SecurityConfig` in order-service.

---

## REST API (order-service)

Base URL: `http://localhost:8080`

- Auth: `POST /auth/register`, `POST /auth/login`, `POST /auth/refresh`, `POST /auth/logout`
- Roles: `GET /roles`
- Books: `GET /books`
- Orders:
  - `GET /orders`, `GET /orders/{id}`
  - `POST /orders` — create order; triggers BPMN `createOrder` and emits Kafka event
  - `POST /orders/{id}/accept` — triggers BPMN accept path; emits event
  - `POST /orders/{id}/reject`
  - `POST /orders/{id}/complete` — completes order; emits event
  - `POST /orders/{id}/cancel` — cancels order; emits event
- Time windows: `GET /time/{orderId}`, `POST /time/{orderId}`

Domain exceptions: `OrderNotFoundException`, `BookIsNotAvailableException`, `TimeIsNotAvailableException`, `IncorrectTimePeriodException`, `OrderHasBeenAlreadyAcceptedException`, `TimeHasBeenAlreadyChosenException`, `UserNotFoundException`, `UserAlreadyExistException`.

---

## Kafka and scheduling

- Topic: `order-topic` — order lifecycle events (`OrderDTO`).
- Producer: `order-service` (`config/KafkaConfiguration`, `DeliveryServiceLitRes#sendOrder`).
- Consumer: `mail-service` (`NotificationServiceMail#sendNotification`).
- Periodic digest: `NotificationServiceMail#broadcast` runs on schedule.
