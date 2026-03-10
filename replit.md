# Demo - Spring Boot Restaurant Web App

## Overview
A Spring Boot web application for a restaurant (themed around Formula 1). Uses Thymeleaf for server-side rendering with static assets (CSS, JS, images).

## Tech Stack
- **Language**: Java 19 (GraalVM 22.3)
- **Framework**: Spring Boot 4.0.3
- **Templating**: Thymeleaf
- **Build Tool**: Maven (via Maven Wrapper `./mvnw`)
- **Port**: 5000

## Project Structure
```
demo/
  src/
    main/
      java/com/example/demo/
        controller/   - Spring MVC controllers (Login, Menu, Restaurante)
        entitys/      - Domain entities (Categoria, Cliente, Comida)
        repository/   - Data repositories
        service/      - Business services
      resources/
        templates/    - Thymeleaf HTML templates
        static/       - CSS, JS, Images
    test/             - Unit tests
  pom.xml             - Maven build config
  mvnw                - Maven wrapper
```

## Running the App
The app runs via the "Start application" workflow:
```
cd demo && ./mvnw spring-boot:run
```

## Key Notes
- The app uses in-memory repositories (no external database configured)
- Spring Boot DevTools is included for hot-reload during development
- Thymeleaf templates use some deprecated fragment syntax (warnings, not errors)
- Port is set to 5000 in `application.properties`
