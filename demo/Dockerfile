# ── STAGE 1: Build ────────────────────────────────────────────────────────────
FROM eclipse-temurin:17-jdk-alpine AS builder

WORKDIR /app

# Copy wrapper and pom first → Docker cache layer for dependencies
COPY .mvn/ .mvn/
COPY mvnw pom.xml ./

# Make wrapper executable and download dependencies (cached unless pom.xml changes)
RUN chmod +x mvnw && ./mvnw dependency:go-offline -q

# Copy source and build the fat JAR (skip tests; they run in CI)
COPY src/ src/
RUN ./mvnw package -DskipTests -q

# ── STAGE 2: Runtime ──────────────────────────────────────────────────────────
FROM eclipse-temurin:17-jre-alpine AS runtime

# Non-root user for security
RUN addgroup -S appgroup && adduser -S appuser -G appgroup
USER appuser

WORKDIR /app

COPY --from=builder /app/target/demo-0.0.1-SNAPSHOT.jar app.jar

# Cloud platforms (Render, Fly.io, Railway) inject $PORT at runtime
EXPOSE 8090

ENTRYPOINT ["java", \
  "-XX:+UseContainerSupport", \
  "-XX:MaxRAMPercentage=75.0", \
  "-Djava.security.egd=file:/dev/./urandom", \
  "-jar", "app.jar"]
