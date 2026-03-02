# F1 Pizza Restaurant - Spring Boot App

## Overview
An F1-themed pizza restaurant web application built with Spring Boot 4, Thymeleaf templates, and in-memory data storage. No external database required.

## Architecture
- **Framework**: Spring Boot 4.0.3
- **Language**: Java 19 (GraalVM 22.3)
- **Build tool**: Maven (via `mvnw` wrapper or system `mvn`)
- **Templates**: Thymeleaf
- **Data**: In-memory (HashMap-based repositories, no database)
- **Port**: 5000

## Project Layout
```
demo/
  src/main/java/com/example/demo/
    DemoApplication.java         - Main entry point
    controller/                  - Spring MVC controllers
    entitys/                     - Domain objects (Comida, Categoria, Cliente)
    repository/                  - In-memory data repositories
    service/                     - Service layer
  src/main/resources/
    application.properties       - App config (port 5000)
    templates/                   - Thymeleaf HTML templates
    static/                      - CSS, images, JS
```

## Key Routes
- `/` - Home / index page
- `/producto/` - Product home
- `/producto/menu` - Menu page (all pizzas by category)
- `/producto/{id}` - Product detail page
- `/producto/menutabla` - Menu admin table view
- `/f1-standings` - F1 standings page
- `/login` - Login page

## Running
The workflow runs: `cd demo && mvn spring-boot:run`

The app starts on port 5000 with Spring DevTools for hot reload.

## Deployment
- Target: autoscale
- Build: `cd demo && mvn package -DskipTests -q`
- Run: `cd demo && java -jar target/demo-0.0.1-SNAPSHOT.jar`
