# Multi-stage build for Render Production
FROM eclipse-temurin:17-jdk-alpine AS builder
WORKDIR /app

# Copy Maven files + download dependencies (cache optimization)
COPY pom.xml .
RUN apk add --no-cache maven && \
    mvn dependency:go-offline -B

# Copy source + build JAR
COPY src ./src
RUN mvn clean package -DskipTests

# Production runtime (smaller image) - RENDER READY
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# Render H2 in-memory database (crash-proof)
ENV SPRING_DATASOURCE_URL=jdbc:h2:mem:notifications
ENV SPRING_JPA_HIBERNATE_DDL_AUTO=create-drop
ENV SPRING_H2_CONSOLE_ENABLED=true

COPY --from=builder /app/target/*.jar app.jar
EXPOSE 8082
ENTRYPOINT ["sh", "-c", "java -XX:InitialRAMPercentage=75.0 -Dserver.port=${PORT:-8082} -jar app.jar"]
