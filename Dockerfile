# -------------------------------
# Stage 1: Build with Maven
# -------------------------------
FROM maven:3.9.4-eclipse-temurin-21 AS builder

WORKDIR /app

# Copy pom.xml and download dependencies
COPY pom.xml ./
RUN mvn dependency:go-offline

# Copy the rest of the project
COPY src ./src

# Build Spring Boot jar (skip tests for faster build)
RUN mvn clean package -DskipTests


# -------------------------------
# Stage 2: Run Spring Boot App
# -------------------------------
FROM eclipse-temurin:21-jdk-jammy

WORKDIR /app

# Copy built jar from builder stage
COPY --from=builder /app/target/*.jar app.jar

# Expose backend ports
EXPOSE 8090 8092

# Run Spring Boot application
ENTRYPOINT ["java", "-jar", "app.jar"]
