# For Render Production
FROM eclipse-temurin:17-jdk-alpine AS builder 
WORKDIR /app

# Copy Maven files + download dependencies (cache optimization)
COPY pom.xml .
RUN apk add --no-cache maven && \
    mvn dependency:go-offline -B

# Copy source + build JAR
COPY src ./src
RUN mvn clean package -DskipTests

# Production runtime (smaller image)
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# Render H2 in-memory database 
ENV SPRING_DATASOURCE_URL=jdbc:h2:mem:appointments
ENV SPRING_JPA_HIBERNATE_DDL_AUTO=create-drop
ENV SPRING_H2_CONSOLE_ENABLED=true

COPY --from=builder /app/target/*.jar app.jar
EXPOSE ${PORT:-8081}
ENTRYPOINT ["sh", "-c", "java -XX:InitialRAMPercentage=75.0 -Dserver.port=$PORT -jar app.jar"]

