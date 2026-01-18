#!/bin/sh

echo "Waiting for database to be ready..."
while ! nc -z db 5432; do
  sleep 1
done
echo "Database is ready!"

echo "Running Liquibase migrations..."
cd /app/db/changelog || exit 1
liquibase \
  --classpath=/opt/postgresql-driver.jar \
  --changeLogFile=db.changelog-master.yaml \
  --url=${DB_URL} \
  --username=${DB_USERNAME} \
  --password=${DB_PASSWORD} \
  --driver=org.postgresql.Driver \
  update
echo "Migrations completed!"