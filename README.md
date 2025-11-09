<div align="center">

# Quick Commerce Backend

Microservices-based backend platform for an e-commerce (quick commerce) system built with Spring Boot 3 (Java 17) and Spring Cloud (Eureka Service Discovery, OpenFeign). Provides modular services for authentication & user management, product catalogue, ordering workflow, and payments – all sharing a common domain model & utilities module.

</div>

---

## 1. High-Level Overview

| Concern | Implementation |
|---------|----------------|
| Language | Java 17 |
| Framework | Spring Boot 3.1.5 |
| Microservice Discovery | Spring Cloud Netflix Eureka |
| Inter-service Communication | REST + OpenFeign clients |
| API Documentation | springdoc-openapi (Swagger UI) |
| Persistence | JPA / Hibernate + PostgreSQL |
| AuthN/AuthZ | Spring Security + JWT (jjwt) + Refresh Tokens |
| Validation | Jakarta Bean Validation |
| Build | Gradle (multi-module) |
| Packaging | Boot JAR per service |
| External Payment Gateway | Razorpay Java SDK |
| Cross-cutting Module | `shared` (entities, DTOs, base abstractions) |

### Services

1. `discovery-service` – Eureka server (service registry)
2. `auth-service` – User registration, login, JWT issuance, roles, addresses
3. `catalogue-service` – Products, categories, wishlist, cart items & search/filter
4. `orders-service` – Checkout flows (single item & cart), order + order items, integrations to payment & catalogue
5. `payment-service` – Payment initiation, capture, refund, webhook handling (Razorpay abstraction layer)
6. `shared` – Reusable domain entities, DTOs, base JPA abstractions, pagination models

All service modules (except discovery) depend on `shared` for consistent domain representations & utility classes.

---

## 2. Architecture & Interaction Flow

```
							 +------------------+
							 |  discovery-service| (Eureka)
							 +---------+--------+
												 ^  service registration
	 (Feign)               |                         (Feign)
 +------------+    +-----+------+        +----------------+
 | orders     |<-->| catalogue  |<------>| auth-service   |
 |  service   |    |  service   |        | (JWT issuance) |
 +-----+------+    +-----+------+        +----------------+
			 |  \
			 |   \ Feign (payment + product pricing lookups)
			 v    \
 +------------+      Razorpay SDK / Webhooks
 | payment    |<------------------------------>
 | service    |
 +------------+
```

Typical Checkout (cart):
1. Client authenticates via `auth-service` (JWT + refresh token)
2. Product & pricing retrieved from `catalogue-service`
3. Client triggers checkout via `orders-service`
4. `orders-service` validates product availability & computes totals
5. Payment initiated via `payment-service` (delegates to Razorpay gateway)
6. On success, order persisted; payment status updated; webhook can reconcile

---

## 3. Modules & Responsibilities

### 3.1 discovery-service
Eureka server – central registry enabling service-to-service resolution by logical name.

Key dependency: `spring-cloud-starter-netflix-eureka-server`.

### 3.2 auth-service
Responsibilities:
- User registration & login
- JWT access & refresh token issuance
- Role-based authorization (e.g. ADMIN endpoint)
- Address management

Security Stack:
- Spring Security
- JJWT (`io.jsonwebtoken`)
- RefreshToken entity & repository

Representative Controller Endpoints (`/api/auth`):
- `POST /register`
- `POST /login`
- `POST /refresh?refreshToken=...`
- `GET /me`
- `GET /admin/secure` (ROLE_ADMIN only)

### 3.3 catalogue-service
Responsibilities:
- CRUD for Products & Categories
- Wishlist management
- Cart item management
- Paged product listing with search, category filters, price range, sort

Representative Endpoints (`/api/v1/products`):
- `POST /` create product
- `PUT /{id}` update
- `DELETE /{id}` delete
- `GET /{id}` fetch
- `GET /` list (params: page, size, search, categories, minPrice, maxPrice, sort)

Entities:
- `Product`, `Category`, `WishListItem`, `CartItem`

### 3.4 orders-service
Responsibilities:
- Checkout single product or entire cart
- Compose product + pricing data from catalogue-service
- Initiate & track payments
- Persist `Order` and `OrderItem` entities

Representative Endpoints (`/api/v1/orders`):
- `POST /checkout/single`
- `POST /checkout/cart`

Integrations (Feign):
- `CatalogueClient` (name = `catalogue-service`)
- `PaymentClient` (name = `payment-service`)

### 3.5 payment-service
Responsibilities:
- Initialize payments via optional multi-gateway abstraction
- Capture & refund operations
- Webhook ingestion & signature verification

Representative Endpoints (`/api/payments`):
- `POST /` initiate payment (`gateway` param optional)
- `POST /{paymentId}/capture`
- `POST /{paymentId}/refund`
- `POST /webhook` (gateway webhook listener)
- `GET /{paymentId}` fetch status

External Integration:
- Razorpay (SDK: `com.razorpay:razorpay-java`)

### 3.6 shared module
Provides:
- Base JPA entity abstractions (e.g. `BaseEntity` with auditing)
- Common DTOs (e.g. `PageResponse`)
- Shared entities reused across services (strategic unification)
- Re-exported dependencies (Web, Data JPA, Validation, PostgreSQL driver)

---

## 4. Domain Model Summary

| Module | Entities |
|--------|----------|
| auth-service | User, Role, RefreshToken, Address |
| catalogue-service | Product, Category, WishListItem, CartItem |
| orders-service | Order, OrderItem |
| payment-service | Payment |
| shared | BaseEntity (auditing support) |

All JPA repositories extend `JpaRepository<..., UUID>` for strongly typed persistence.

---

## 5. Inter-Service Communication

- Service discovery via Eureka (logical names registered at startup).
- OpenFeign used in `orders-service` for type-safe HTTP clients:
	- `@FeignClient(name = "payment-service")` -> Payment orchestration
	- `@FeignClient(name = "catalogue-service")` -> Product data enrichment
- Synchronous REST with JSON payloads.
- Potential extension: Circuit breakers / retries (Resilience4j) – not yet implemented.

---

## 6. Security & Authentication

- Stateless JWT auth (access + refresh). Access token returned on login; refresh token endpoint issues new pair.
- Spring Security method-level authorization using `@PreAuthorize`.
- Role-based access (e.g. ADMIN protected route).
- Validation layer ensures request DTO integrity via `jakarta.validation` annotations.

---

## 7. API Documentation

- Swagger / OpenAPI served via `springdoc-openapi-starter-webmvc-ui`.
- For each service (except discovery), Swagger UI typically available at:
	- `http://localhost:<port>/swagger-ui/index.html`

---

## 8. Build & Run

### 8.1 Prerequisites
- JDK 17+
- Gradle Wrapper (included)
- PostgreSQL instance (ensure URL/user/pass configured in each service `application.yml`)

### 8.2 Typical Local Startup Order
1. Discovery server
2. Auth service
3. Catalogue service
4. Payment service
5. Orders service

### 8.3 Commands

Build all:
```
./gradlew clean build
```

Run individual service (example – catalogue):
```
./gradlew :catalogue-service:bootRun
```

Run all (separate terminals):
```
./gradlew :discovery-service:bootRun
./gradlew :auth-service:bootRun
./gradlew :catalogue-service:bootRun
./gradlew :payment-service:bootRun
./gradlew :orders-service:bootRun
```

Eureka Dashboard: `http://localhost:8761`

### 8.4 Configuration
Key settings typically found in each service's `application.yml`:
- Server port
- Spring datasource properties (PostgreSQL)
- Eureka client configuration
- JWT secret / token lifetimes (auth-service)
- Razorpay credentials (payment-service) – supply via env vars or external config

---

## 9. Testing

- JUnit 5 + Spring Boot test starter
- Mockito for service layer mocking (where applied)
- Recommendation: add integration tests for Feign boundaries & payment gateway stubs.

---

## 10. Extension & Improvement Ideas

| Area | Opportunity |
|------|-------------|
| Resilience | Add Resilience4j (retries, circuit breakers) around Feign clients |
| Observability | Add centralized logging + tracing (Zipkin / OpenTelemetry) |
| API Gateway | Introduce edge gateway with rate limiting & unified auth |
| Caching | Introduce Redis for product catalogue & token blacklisting |
| Async Events | Use Kafka for order/payment state propagation |
| Tests | Add contract tests for Feign + end-to-end checkout flow |
| CI/CD | Automate build, test, containerization, deployment |

---

## 11. Dependency Summary (Selected)

| Purpose | Library |
|---------|---------|
| Web | spring-boot-starter-web |
| Persistence | spring-boot-starter-data-jpa, PostgreSQL Driver |
| Validation | spring-boot-starter-validation |
| Security | spring-boot-starter-security, jjwt-* |
| Discovery | spring-cloud-starter-netflix-eureka-client / server |
| OpenAPI | springdoc-openapi-starter-webmvc-ui |
| Feign | spring-cloud-starter-openfeign |
| Payments | razorpay-java |
| Lombok | lombok |

---

## 12. Example Request Flow (Checkout Single Product)

1. Client obtains JWT from `auth-service` (`/api/auth/login`).
2. Client calls `orders-service /api/v1/orders/checkout/single` with product + quantity.
3. `orders-service` fetches product details via `catalogue-service` Feign client.
4. `orders-service` calls `payment-service` to initialize payment (Razorpay order creation behind the scenes).
5. `payment-service` returns payment reference; `orders-service` persists `Order` + `OrderItem`.
6. Client proceeds to payment capture (if required) or awaits webhook.

---

## 13. Project Structure (Excerpt)

```
quick-commerce-backend/
	build.gradle (root aggregator)
	discovery-service/
	auth-service/
	catalogue-service/
	orders-service/
	payment-service/
	shared/
```

---

## 14. Licensing / Academic Use

This project is prepared for academic evaluation. Ensure any API keys (e.g., Razorpay) are not committed. Use environment variables or a secrets manager in production.

---

## 15. Maintainer

Author: (Hrishikesh Shelke)
Contact: (dev.hrishi@outlook.com)

---

Feel free to extend this README with deployment, containerization, or architectural decision records (ADRs) as the project evolves.

