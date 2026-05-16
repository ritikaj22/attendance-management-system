# ============================================================
# Attendance Management System - Production Dockerfile
# Multi-stage build for optimized image size
# ============================================================

# Stage 1: Build Stage
FROM eclipse-temurin:17-jdk-alpine AS builder

WORKDIR /app

# Copy Maven wrapper and pom.xml first for layer caching
COPY pom.xml .
COPY mvnw .
COPY .mvn .mvn

# Download dependencies (cached layer)
RUN ./mvnw dependency:go-offline -B

# Copy source code
COPY src src

# Build the application
RUN ./mvnw clean package -DskipTests -B &&     mkdir -p target/dependency &&     cd target/dependency &&     jar -xf ../*.jar

# Stage 2: Runtime Stage
FROM eclipse-temurin:17-jre-alpine

# Security: Create non-root user
RUN addgroup -S attendance && adduser -S attendance -G attendance

WORKDIR /app

# Copy extracted JAR layers from builder
COPY --from=builder /app/target/dependency/BOOT-INF/lib /app/lib
COPY --from=builder /app/target/dependency/META-INF /app/META-INF
COPY --from=builder /app/target/dependency/BOOT-INF/classes /app

# Set ownership
RUN chown -R attendance:attendance /app

USER attendance

# Expose application port
EXPOSE 8080

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=60s --retries=3     CMD wget --no-verbose --tries=1 --spider http://localhost:8080/attendance/health || exit 1

# Run the application
ENTRYPOINT ["java", "-cp", "app:app/lib/*", "com.attendance.AttendanceApplication"]
