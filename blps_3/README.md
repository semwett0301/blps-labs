# Lab 3 — Async and Distributed Processing with Scheduling

This lab extends Lab 2 by introducing asynchronous, distributed processing and scheduled periodic tasks. The system is split into two Spring Boot services: `order-service` (producer) and `mail-service` (consumer + scheduler). Communication uses Kafka with the publish/subscribe model; scheduled broadcasts use Spring Scheduling.

## Tech stack
- Spring Boot (Web, Data JPA, Security in order-service)
- Java 17, Maven
- PostgreSQL + Hibernate
- Apache Kafka (producer in order-service, consumer in mail-service)
- Spring for Apache Kafka, custom Producer/Consumer configs
- Spring Scheduling (`@Scheduled`) for periodic jobs
- Jackson, MapStruct, Lombok

## Requirements (from assignment photo)
- Asynchronous processing using the publish/subscribe delivery model.
- Use Apache Kafka + ZooKeeper as the messaging service.
- Use Kafka Producer API for sending messages; use a Spring Boot Kafka client for consuming.
- Distributed processing: handle messages on two independent application nodes (order-service and mail-service).
- If the distributed scenario requires transactional operations, include them in a distributed transaction where applicable.
- Implement periodic jobs using Quartz or Spring Scheduling; this project uses Spring Scheduling.

## Services overview

### order-service
- Exposes business REST API (auth, roles, books, orders, time windows).
- On lifecycle changes (`create`, `accept`, `complete`, `cancel`) publishes `OrderDTO` to topic `order-topic`.
- Kafka producer is configured in `order-service/.../config/KafkaConfiguration.java`.
- Example producer call is in `DeliveryServiceLitRes#sendOrder`.

Runtime config (`order-service/src/main/resources/application.properties`):
```text
spring.datasource.url=jdbc:postgresql://localhost:5432/blps_3_db
spring.datasource.username=<username>
spring.datasource.password=<password>
spring.jpa.hibernate.ddl-auto=create
spring.kafka.producer.bootstrap-servers=localhost:9092
spring.kafka.producer.acks=all
spring.kafka.producer.retries=1
```

### mail-service
- Consumes `order-topic` messages and sends email notifications to subscribed users.
- `@KafkaListener(topics = "order-topic")` in `NotificationServiceMail#sendNotification` stores events and sends immediate emails based on user settings.
- Periodic digest: `@Scheduled(fixedRate = 60 * 1000)` in `NotificationServiceMail#broadcast` aggregates pending notifications and emails a batch once per minute.

Runtime config (`mail-service/src/main/resources/application.properties`):
```text
server.port=9000
spring.datasource.url=jdbc:postgresql://localhost:5432/blps_3_db
spring.datasource.username=<username>
spring.datasource.password=<password>
spring.jpa.hibernate.ddl-auto=update
spring.kafka.consumer.bootstrap-servers=localhost:9092
spring.kafka.consumer.group-id=my-group
spring.mail.host=<smtp-host>
spring.mail.port=<smtp-port>
spring.mail.username=<smtp-user>
spring.mail.password=<smtp-pass>
spring.mail.protocol=smtps
mail.topic=Change order status
```

## REST API (order-service)
Base URL: `http://localhost:8080`

- Auth: `POST /auth/register`, `POST /auth/login`, `POST /auth/refresh`, `POST /auth/logout`
- Roles: `GET /roles`
- Books: `GET /books`
- Orders:
  - `GET /orders`, `GET /orders/{id}`
  - `POST /orders` — create and publish event
  - `POST /orders/{id}/accept` — publish event
  - `POST /orders/{id}/reject`
  - `POST /orders/{id}/complete` — publish event
  - `POST /orders/{id}/cancel` — publish event
- Time windows: `GET /time/{orderId}`, `POST /time/{orderId}`

Domain exceptions include: `OrderNotFoundException`, `BookIsNotAvailableException`, `TimeIsNotAvailableException`, `IncorrectTimePeriodException`, `OrderHasBeenAlreadyAcceptedException`, `TimeHasBeenAlreadyChosenException`, `UserNotFoundException`, `UserAlreadyExistException`.

## Kafka topics
- `order-topic` — events about order lifecycle, key is order number, value is `OrderDTO`.