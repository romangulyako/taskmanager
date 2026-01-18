# Task Manager

Task Manager is a RESTful API for managing tasks and users with role-based access control (RBAC). It provides endpoints
for creating, updating, deleting, and retrieving tasks and users, with JWT-based authentication and authorization.

---

## Features

- **Task Management**: Create, update, delete, and retrieve tasks.
- **User Management**: Register, update, delete, and retrieve users.
- **Authentication & Authorization**: JWT-based authentication with role-based access control (ADMIN, USER).
- **Database Migrations**: Liquibase for managing database schema changes.
- **Docker Support**: Easy deployment with Docker and Docker Compose.
- **OpenAPI/Swagger Documentation**: Interactive API documentation.
- **Spring Boot Actuator**: Endpoints for monitoring and managing the application (health checks, metrics, and more).

---

## Prerequisites

- **Java 17+**
- **Maven 3.9+**
- **Docker** (for containerized deployment)
- **PostgreSQL** (for database)

---

## Getting Started

### 1. Clone the Repository

```bash
git clone https://github.com/romangulyako/taskmanager.git
cd taskmanager
```

### 2. Configure Environment Variables

Copy the example environment files and update them with your settings:

#### PostgreSQL Configuration

```bash
cp postgres.env.example postgres.env
```

Edit `postgres.env` with your PostgreSQL credentials.

#### Application Configuration

```bash
cp .env.example .env
```

Edit `.env` with your application properties.

### 3. Build the Project

#### Using Maven

```bash
mvn clean package -DskipTests
```

#### Using Makefile

```bash
make build
```

### 4. Run Database Migrations

Migrations are automatically applied when running the application in Docker. For local development, you can run them
manually:

#### Using Liquibase CLI

```bash
mvn liquibase:update
```

#### Using Docker

```bash
make docker-up
```

### 5. Run the Application

#### Locally

```bash
java -jar target/task-manager-*.jar
```

#### Using Docker

```bash
make docker-up
```

#### Using Makefile

```bash
make run
```

---

## Project Structure

```text
.
├── Dockerfile                  # Docker configuration
├── Makefile                    # Automation scripts
├── docker-compose.yaml         # Docker Compose configuration
├── migrate.sh                  # Database migration script
├── pom.xml                     # Maven configuration
├── postgres.env.example        # Example PostgreSQL environment variables
├── .env.example                # Example application environment variables
└── src
    ├── main
    │   ├── java                # Java source code
    │   └── resources           # Configuration and changelog files
    └── test                    # Test files
```

---

## API Documentation

The API is documented using OpenAPI/Swagger. After running the application, you can access the interactive documentation
at:

```http request
http://localhost:8080/swagger-ui.html
```

---

## Available Makefile Commands

| Command             | Description                    |
|---------------------|--------------------------------|
| `make build`        | Build the project using Maven. |
| `make clean`        | Clean the project.             |
| `make docker-build` | Build the Docker image.        |
| `make docker-down`  | Stop Docker containers.        |
| `make docker-logs`  | Show Docker logs.              |
| `make docker-up`    | Start Docker containers.       |
| `make help`         | Show available commands.       |
| `make run`          | Run the application locally.   |
| `make test`         | Run tests.                     |

---

## Testing

Run tests using Maven:
```bash
mvn test
```

Or using Makefile:
```bash
make test
```

---

## Deployment

### Using Docker Compose
```bash
docker-compose up -d
```

### Using Makefile
```bash
make docker-up
```

---

## Contributing

1. Fork the repository.
2. Create a new branch:
    ```bash
    git checkout -b feature/your-feature 
    ```
3. Commit your changes:
    ```bash 
    git commit -m "feat: add your feature"
    ```
4. Push to the branch:
    ```bash
    git push origin feature/your-feature 
    ```
5. Open a pull request.



