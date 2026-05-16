# ============================================================
# Attendance Management System - Dockerfile
# ============================================================

# Stage 1 - Build
FROM maven:3.9.6-eclipse-temurin-17 AS builder

WORKDIR /app

# Copy pom.xml
COPY pom.xml .

# Download dependencies
RUN mvn dependency:go-offline -B

# Copy source code
COPY src src

# Build application
RUN mvn clean package -DskipTests -B

# Stage 2 - Runtime
FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

# Create non-root user
RUN addgroup -S attendance && adduser -S attendance -G attendance

# Copy built jar
COPY --from=builder /app/target/*.jar app.jar

# Set ownership
RUN chown attendance:attendance app.jar

USER attendance

# Expose application port
EXPOSE 8080

# Health check
HEALTHCHECK --interval=30s --timeout=5s --start-period=60s --retries=3 \
CMD wget --no-verbose --tries=1 --spider http://localhost:8080/attendance/health || exit 1

# Run application
ENTRYPOINT ["java", "-jar", "app.jar"]