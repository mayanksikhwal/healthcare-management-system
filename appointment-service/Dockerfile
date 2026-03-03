/* for local system
FROM eclipse-temurin:17-jdk-alpine
WORKDIR /app
COPY target/appointment-service-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8081
ENTRYPOINT ["java", "-jar", "app.jar"]
*/

// For Render Production

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
COPY --from=builder /app/target/*.jar app.jar
EXPOSE 8081
ENTRYPOINT ["java", "-XX:InitialRAMPercentage=75.0", "-jar", "app.jar"]
