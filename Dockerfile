# Build stage
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

# Run stage
FROM eclipse-temurin:21-jre-jammy
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar

# Environment variables
ENV SERVER_PORT=8080
ENV DB_URL=jdbc:postgresql://postgres:5432/familytree
ENV DB_USERNAME=postgres
ENV DB_PASSWORD=postgres
ENV JWT_SECRET=your-256-bit-secret
ENV JWT_EXPIRATION=86400000

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"] 