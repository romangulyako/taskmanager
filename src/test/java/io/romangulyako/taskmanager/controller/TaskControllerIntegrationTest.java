package io.romangulyako.taskmanager.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.romangulyako.taskmanager.dto.TaskRequest;
import io.romangulyako.taskmanager.entity.Task;
import io.romangulyako.taskmanager.repository.TaskRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.LocalDate;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@AutoConfigureMockMvc
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
    private TaskRepository taskRepository;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private final LocalDate FIXED_DATE = LocalDate.of(2026, 1, 1);

    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @Test
    void createTask_shouldReturnCreatedTask() throws Exception {
        // Arrange
        TaskRequest taskRequest = new TaskRequest(
                "Integration Test Task",
                "Description",
                "OPEN",
                FIXED_DATE
        );

        // Act & Assert
        mockMvc.perform(post("/api/v1/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(taskRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value(taskRequest.title()));
    }

    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @Test
    void updateTask_shouldReturnUpdatedTaskResponse() throws Exception {
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

        // Act & Assert
        mockMvc.perform(put("/api/v1/tasks/" + savedTask.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(taskRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value(taskRequest.title()));
    }

    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @Test
    void deleteTask_shouldReturnNoContent() throws Exception {
        // Arrange
        Task task = Task.builder()
                .title("Task to Delete")
                .description("Description")
                .status(Task.Status.OPEN)
                .dueDate(FIXED_DATE)
                .build();

        Task savedTask = taskRepository.save(task);

        // Act & Assert
        mockMvc.perform(delete("/api/v1/tasks/" + savedTask.getId()))
                .andExpect(status().isNoContent());
    }

    @WithMockUser(username = "user", roles = {"USER"})
    @Test
    void getTask_shouldReturnTask_whenTaskExists() throws Exception {
        // Arrange
        Task task = Task.builder()
                .title("Existing Task")
                .description("Description")
                .status(Task.Status.OPEN)
                .dueDate(FIXED_DATE)
                .build();

        Task savedTask = taskRepository.save(task);

        // Act & Assert
        mockMvc.perform(get("/api/v1/tasks/" + savedTask.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value(savedTask.getTitle()));
    }

    @WithMockUser(username = "user", roles = {"USER"})
    @Test
    void getTask__shouldReturnErrorResponse_whenTaskNotFound() throws Exception {
        // Arrange
        long taskId = 1L;
        taskRepository.deleteById(taskId);

        // Act & Assert
        mockMvc.perform(get("/api/v1/tasks/" + taskId))
                .andExpect(status().isNotFound());
    }

    @WithMockUser(username = "user", roles = {"USER"})
    @Test
    void getAllTasks_shouldReturnListOfTasks() throws Exception {
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

        taskRepository.deleteAll();
        taskRepository.saveAll(List.of(task1, task2));

        // Act & Assert
        mockMvc.perform(get("/api/v1/tasks"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].title").value(task1.getTitle()))
                .andExpect(jsonPath("$[1].title").value(task2.getTitle()));
    }

    @WithMockUser(username = "user", roles = {"USER"})
    @Test
    void getAllByStatus_shouldReturnFilteredTasks() throws Exception {
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

        // Act & Assert
        mockMvc.perform(get("/api/v1/tasks?status=OPEN"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));
    }
}
