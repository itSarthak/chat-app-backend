# Stage 1: Build the Spring Boot application
FROM openjdk:21-jdk-slim AS build
WORKDIR /app
COPY .mvn .mvn
COPY mvnw pom.xml ./
RUN ./mvnw dependency:go-offline -B
COPY src ./src
RUN ./mvnw package -DskipTests

# Stage 2: Create the final image with Nginx
FROM nginx:alpine

# Install OpenJDK JRE to run the Spring Boot application
RUN apk add --no-cache openjdk21-jre-headless

WORKDIR /app

# Copy the Nginx configuration template
COPY nginx.conf /etc/nginx/conf.d/default.conf

# Copy the executable JAR file and the env.properties file
COPY --from=build /app/target/*.jar /app/app.jar
COPY env.properties /app/env.properties

# Expose the ports for Nginx to listen on
EXPOSE 8070

# The command to run the Nginx and Spring Boot services
CMD ["/bin/sh", "-c", "java -jar /app/app.jar & nginx -g 'daemon off;'"]