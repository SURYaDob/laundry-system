# ===========================================
# Laundry System - Render Deployment (H2 in-memory DB)
# ===========================================
# Build Stage
FROM maven:3.9-eclipse-temurin-17 AS builder

WORKDIR /app

COPY pom.xml .
RUN mvn dependency:go-offline -B -q

COPY src src
RUN mvn package -DskipTests -B -q

# Runtime Stage
FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

RUN addgroup -S appgroup && adduser -S appuser -G appgroup

COPY --from=builder /app/target/*.jar app.jar

USER appuser

EXPOSE 8080

ENTRYPOINT java -jar /app/app.jar --spring.profiles.active=${SPRING_PROFILES_ACTIVE:-dev}
