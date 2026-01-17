FROM maven:3.9.9-eclipse-temurin-17 AS builder
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

FROM eclipse-temurin:17-jre-alpine
RUN adduser -D appuser
WORKDIR /app
COPY --from=builder /app/target/task-manager*.jar app.jar
RUN chown appuser:appuser app.jar
EXPOSE 8080
USER appuser
ENTRYPOINT ["java", "-XX:+UseContainerSupport", "-XX:MaxRAMPercentage=75.0", "-jar", "app.jar"]