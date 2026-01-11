package io.romangulyako.taskmanager.controller;

import io.romangulyako.taskmanager.dto.ErrorResponse;
import io.romangulyako.taskmanager.dto.TaskRequest;
import io.romangulyako.taskmanager.dto.TaskResponse;
import io.romangulyako.taskmanager.entity.Task;
import io.romangulyako.taskmanager.repository.TaskRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class TaskControllerIntegrationTest {
    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:17.6");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private TaskRepository taskRepository;

    private final LocalDate FIXED_DATE = LocalDate.of(2026, 1, 1);
    
    @Test
    void createTask_shouldReturnCreatedTask() {
        // Arrange
        TaskRequest taskRequest = new TaskRequest(
                "Integration Test Task",
                "Description",
                "OPEN",
                FIXED_DATE
        );

        // Act
        ResponseEntity<TaskResponse> response = restTemplate.postForEntity(
                "/api/v1/tasks",
                taskRequest,
                TaskResponse.class
        );

        // Assert
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(taskRequest.title(), response.getBody().title());
    }

    @Test
    void updateTask_shouldReturnUpdatedTaskResponse() {
        // Arrange
        Task task = Task.builder()
                .title("Old Task")
                .description("Old Description")
                .status(Task.Status.OPEN)
                .dueDate(FIXED_DATE)
                .build();

        Task savedTask = taskRepository.save(task);

        TaskRequest taskRequest = new TaskRequest(
                "Updated Task",
                "Updated Description",
                "DONE",
                FIXED_DATE
        );

        // Act
        ResponseEntity<TaskResponse> response = restTemplate.exchange(
                "/api/v1/tasks/" + savedTask.getId(),
                HttpMethod.PUT,
                new HttpEntity<>(taskRequest),
                TaskResponse.class
        );

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(taskRequest.title(), response.getBody().title());
    }

    @Test
    void deleteTask_shouldReturnNoContent() {
        // Arrange
        Task task = Task.builder()
                .title("Task to Delete")
                .description("Description")
                .status(Task.Status.OPEN)
                .dueDate(FIXED_DATE)
                .build();

        Task savedTask = taskRepository.save(task);

        // Act
        ResponseEntity<Void> response = restTemplate.exchange(
                "/api/v1/tasks/" + savedTask.getId(),
                HttpMethod.DELETE,
                null,
                Void.class
        );

        // Assert
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }

    @Test
    void getTask_shouldReturnTask_whenTaskExists() {
        // Arrange
        Task task = Task.builder()
                .title("Existing Task")
                .description("Description")
                .status(Task.Status.OPEN)
                .dueDate(FIXED_DATE)
                .build();

        Task savedTask = taskRepository.save(task);

        // Act
        ResponseEntity<TaskResponse> response = restTemplate.getForEntity(
                "/api/v1/tasks/" + savedTask.getId(),
                TaskResponse.class
        );

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(savedTask.getTitle(), response.getBody().title());
    }

    @Test
    void getTask__shouldReturnErrorResponse_whenTaskNotFound() {
        // Arrange
        long taskId = 1L;
        taskRepository.deleteById(taskId);

        // Act
        ResponseEntity<ErrorResponse> response = restTemplate.getForEntity(
                "/api/v1/tasks/" + taskId,
                ErrorResponse.class
        );

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    void getAllTasks_shouldReturnListOfTasks() {
        // Arrange
        Task task1 = Task.builder()
                .title("Task 1")
                .description("Description 1")
                .status(Task.Status.OPEN)
                .dueDate(FIXED_DATE)
                .build();

        Task task2 = Task.builder()
                .title("Task 2")
                .description("Description 2")
                .status(Task.Status.DONE)
                .dueDate(FIXED_DATE)
                .build();

        taskRepository.saveAll(List.of(task1, task2));

        // Act
        ResponseEntity<List<TaskResponse>> response = restTemplate.exchange(
                "/api/v1/tasks",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {}
        );

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(2, response.getBody().size());
    }

    @Test
    void getAllByStatus_shouldReturnFilteredTasks() {
        // Arrange
        Task task1 = Task.builder()
                .title("Task 1")
                .description("Description 1")
                .status(Task.Status.OPEN)
                .dueDate(FIXED_DATE)
                .build();

        Task task2 = Task.builder()
                .title("Task 2")
                .description("Description 2")
                .status(Task.Status.OPEN)
                .dueDate(FIXED_DATE)
                .build();

        taskRepository.deleteAll();
        taskRepository.saveAll(List.of(task1, task2));

        // Act
        ResponseEntity<List<TaskResponse>> response = restTemplate.exchange(
                "/api/v1/tasks/status?status=OPEN",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {}
        );

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(2, response.getBody().size());
    }
}
