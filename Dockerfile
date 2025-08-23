# Stage 1: Build the application
FROM maven:3.9.6-eclipse-temurin-21 AS build

WORKDIR /app

# Copy pom.xml and download dependencies first (caching)
COPY pom.xml .
RUN mvn dependency:go-offline

# Copy source code and build
COPY src ./src
RUN mvn clean package -DskipTests

# Stage 2: Run the application
FROM eclipse-temurin:21-jdk AS runtime

WORKDIR /app

# Copy the built JAR from the build stage
COPY --from=build /app/target/*.jar app.jar

# Copy env.properties file
COPY env.properties ./env.properties

# Set environment variable so Spring Boot picks env.properties
ENV SPRING_CONFIG_IMPORT=file:env.properties

# Only expose Netty socket.io port (8081)
EXPOSE 8081

# Run the app
ENTRYPOINT ["java", "-jar", "app.jar"]
