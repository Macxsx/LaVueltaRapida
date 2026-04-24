# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

**La Vuelta Rápida** is an F1-themed pizza delivery application. It is a Spring Boot REST API backend (port 8090) designed to be consumed by a separate frontend (expected at `http://localhost:5000`).

## Commands

All Maven commands must be run from the `demo/` directory (where `pom.xml` lives).

```bash
# Run the application
cd demo && ./mvnw spring-boot:run

# Build a JAR (skipping tests)
cd demo && ./mvnw package -DskipTests

# Run tests
cd demo && ./mvnw test

# Clean and rebuild
cd demo && ./mvnw clean package
```

The app runs on **port 8090**. The H2 console is at `http://localhost:8090/h2` (JDBC URL: `jdbc:h2:mem:lavueltarapida`, user: `sa`, password: blank).

## Architecture

Standard Spring Boot layered architecture: **Entity → Repository → Service → Controller**

```
demo/src/main/java/com/example/demo/
├── DemoApplication.java       # Entry point
├── DataInitializer.java       # Seeds DB on startup (categories, food items, users)
├── entitys/                   # JPA entities
├── repository/                # Spring Data JPA repositories
├── service/                   # Business logic interfaces + implementations
└── controller/                # REST controllers (@RestController) + Thymeleaf controllers
```

### Domain Model

- **Cliente** — customer with a one-to-one **Carrito** (cart) and one-to-many **Pedido** (orders)
- **Pedido** — order composed of **LineaPedido** lines, each referencing a **Comida** (food item) and optional **LineaPedidoAdicional** extras; assigned to a **Domiciliario** (delivery driver)
- **Comida** — menu item belonging to a **Categoria**; each Categoria has many-to-many **Adicional** (toppings/extras)
- **User roles** are separate entities: `Cliente`, `Administrador`, `Operador`, `Domiciliario` — there is no shared User superclass or Spring Security integration

### Authentication

Authentication is custom (no Spring Security). `POST /auth/login` checks credentials against all role entities and returns `{ username, role, clienteId, carritoId }`. Passwords are stored in plaintext and hidden from API responses via `@JsonProperty(access = WRITE_ONLY)`.

### Key REST Endpoints

| Resource | Base path |
|---|---|
| Auth | `POST /auth/login` |
| Clients | `/clientes` |
| Food menu | `/comidas` |
| Categories | `/categorias` |
| Extras | `/adicionales` |
| Cart | `/carrito` |
| Orders | `/pedido` — includes `GET /pedido/activos` (active orders, ASC for kitchen FIFO) |
| Delivery drivers | `/domiciliarios` |
| Admins / Operators | `/administradores`, `/operadores` |

All controllers use `@CrossOrigin` permitting `http://localhost:5000` and `http://127.0.0.1:5000`.

### Database

H2 in-memory with `ddl-auto=create-drop` — the schema is recreated fresh on every startup and seeded by `DataInitializer`. There is no migration tool (Flyway/Liquibase). Any schema change requires updating both entity classes and `DataInitializer`.

### Technology Stack

- Java 17, Spring Boot 3.4.3
- Spring Data JPA / Hibernate (H2 dialect)
- Lombok (compile-time only)
- Thymeleaf (server-side templates for a few pages: login, menu)
- No Spring Security, no JWT, no password hashing
