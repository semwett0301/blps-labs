# Business Logic of Software Systems (BLPS)

This repository contains a sequence of labs implementing and evolving an **online book ordering and delivery system**.  
Each lab builds upon the previous one, gradually introducing advanced business logic, transactions, security, distributed processing, and BPMN orchestration.

---

## 🚀 Tech Stack

- **Java 11 / 17**, **Maven 3.6+**
- **Spring Boot** (Web, Data JPA, Security, Scheduling)
- **PostgreSQL + Hibernate**
- **MapStruct, Lombok**
- **Swagger / Springdoc OpenAPI** for REST documentation
- **JWT (java-jwt)** with stateless authentication
- **Bitronix (JTA)** for distributed transactions
- **Apache Kafka** for async and distributed communication
- **Camunda BPM** for BPMN 2.0 orchestration
- **Docker + helios** deployment (where required)

---

## 📂 Labs Overview

### [Lab 1 — Business Logic of Software Systems](./blps_1)
Implements the core **book order and delivery process**:
- Browsing books, placing orders, selecting delivery times.
- Courier acceptance/rejection and order completion/cancellation.
- Data persistence in PostgreSQL.
- REST API with curl examples and BPMN 2.0 model.

---

### [Lab 2 — Transactions and Access Control](./blps_2)
Extends Lab 1 with:
- **Declarative transaction management** using Spring JTA + Bitronix.
- **Role-based access control** with Spring Security and JWT.
- Roles: `USER`, `COURIER`.
- Privilege model mapped to endpoints and stored in DB.
- Updated BPMN/UML diagrams.

---

### [Lab 3 — Async and Distributed Processing](./blps_3)
Splits the system into **two services**:
- `order-service` — REST API + Kafka producer.
- `mail-service` — Kafka consumer + scheduled digest emails.
- Asynchronous **publish/subscribe** communication with Kafka.
- **Spring Scheduling** for periodic notifications.

---

### [Lab 4 — BPMN Orchestration + Async/Distributed](./blps_4)
Adds **BPMN orchestration** with Camunda:
- Business processes (`createOrder`, `deliveryOrder`, `login`, `register`, `broadcast`) modeled in BPMN 2.0.
- Orchestration integrated into `order-service`.
- Maintains async Kafka communication and scheduled notifications.
- Camunda web app for monitoring and process execution.
