FROM maven:3.9.9-eclipse-temurin-17 AS builder

WORKDIR /app

COPY pom.xml .
COPY src ./src

RUN mvn clean package -DskipTests

FROM eclipse-temurin:17-jre-alpine

RUN apk add --no-cache netcat-openbsd curl unzip bash && \
    curl -L https://jdbc.postgresql.org/download/postgresql-42.7.3.jar -o /opt/postgresql-driver.jar && \
    curl -L https://github.com/liquibase/liquibase/releases/download/v5.0.1/liquibase-5.0.1.zip -o liquibase.zip && \
    unzip liquibase.zip -d /opt && \
    rm liquibase.zip && \
    ln -s /opt/liquibase /usr/local/bin/liquibase && \
    chmod +x /usr/local/bin/liquibase
RUN adduser -D appuser

WORKDIR /app

COPY --from=builder /app/target/task-manager*.jar app.jar
COPY migrate.sh /app/migrate.sh
COPY src/main/resources/db/changelog /app/db/changelog

RUN chmod +x /app/migrate.sh && \
    chown appuser:appuser /app/migrate.sh /app/app.jar && \
    chown -R appuser:appuser /app/db && \
    chmod -R 755 /app/db

EXPOSE 8080

USER appuser

ENTRYPOINT ["sh", "-c", "/app/migrate.sh && exec java -XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0 -jar /app/app.jar"]