.PHONY: build
build:
	@echo "Building the project..."
	@mvn clean package -DskipTests
	@echo "Build completed successfully!"

.PHONY: run
run:
	@echo "Running the application..."
	@java -jar target/task-manager-*.jar
	@echo "Application is running at http://localhost:8080"

.PHONY: test
test:
	@echo "Running tests..."
	@mvn test
	@echo "Tests completed!"

.PHONY: docker-build
docker-build:
	@echo "Building Docker image..."
	@docker build -t task-manager .
	@echo "Docker image built successfully!"

.PHONY: docker-up
docker-up:
	@echo "Starting Docker containers..."
	@docker-compose up -d
	@echo "Containers are running!"

.PHONY: docker-down
docker-down:
	@echo "Stopping Docker containers..."
	@docker-compose down -v
	@echo "Containers stopped!"

.PHONY: docker-logs
docker-logs:
	@echo "Showing Docker logs..."
	@docker-compose logs -f

.PHONY: clean
clean:
	@echo "Cleaning the project..."
	@mvn clean
	@echo "Project cleaned!"

.PHONY: help
help:
	@echo "Available commands:"
	@echo "  make build          - Build the project"
	@echo "  make run            - Run the application"
	@echo "  make test           - Run tests"
	@echo "  make docker-build   - Build Docker image"
	@echo "  make docker-up      - Start Docker containers"
	@echo "  make docker-down    - Stop Docker containers"
	@echo "  make docker-logs    - Show Docker logs"
	@echo "  make clean          - Clean the project"
	@echo "  make help           - Show this help message"