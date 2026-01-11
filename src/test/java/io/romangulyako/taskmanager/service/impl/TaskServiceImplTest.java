package io.romangulyako.taskmanager.service.impl;

import io.romangulyako.taskmanager.dto.TaskRequest;
import io.romangulyako.taskmanager.dto.TaskResponse;
import io.romangulyako.taskmanager.entity.Task;
import io.romangulyako.taskmanager.mapper.TaskMapper;
import io.romangulyako.taskmanager.repository.TaskRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TaskServiceImplTest {
    @Mock
    private TaskRepository taskRepository;

    @Mock
    private TaskMapper taskMapper;

    @InjectMocks
    private TaskServiceImpl taskService;

    private final LocalDate FIXED_DATE = LocalDate.of(2026, 1, 1);

    @Test
    void createTask_shouldReturnTaskResponse() {
        // Arrange
        TaskRequest taskRequest = new TaskRequest(
                "Test Task",
                "Description",
                "OPEN",
                FIXED_DATE
        );

        Task preSavedTask = Task.builder()
                .title("Test Task")
                .description("Description")
                .status(Task.Status.OPEN)
                .dueDate(FIXED_DATE)
                .build();

        Task savedTask = Task.builder()
                .id(1L)
                .title("Test Task")
                .description("Description")
                .status(Task.Status.OPEN)
                .dueDate(FIXED_DATE)
                .build();

        TaskResponse taskResponse = new TaskResponse(
                1L,
                "Test Task",
                "Description",
                "OPEN",
                FIXED_DATE
        );

        when(taskMapper.toEntity(taskRequest)).thenReturn(preSavedTask);
        when(taskRepository.save(preSavedTask)).thenReturn(savedTask);
        when(taskMapper.toResponse(savedTask)).thenReturn(taskResponse);

        // Act
        TaskResponse result = taskService.create(taskRequest);

        // Assert
        assertNotNull(result);
        assertEquals(taskResponse, result);
        verify(taskRepository, times(1)).save(preSavedTask);
    }

    @Test
    void updateTask_shouldReturnUpdatedTaskResponse() {
        // Arrange
        Long taskId = 1L;
        TaskRequest taskRequest = new TaskRequest(
                "Updated Task",
                "Updated Description",
                "DONE",
                FIXED_DATE
        );

        Task existingTask = Task.builder()
                .id(taskId)
                .title("Old Task")
                .description("Old Description")
                .status(Task.Status.OPEN)
                .dueDate(FIXED_DATE)
                .build();

        Task updatedTask = Task.builder()
                .id(taskId)
                .title("Updated Task")
                .description("Updated Description")
                .status(Task.Status.DONE)
                .dueDate(FIXED_DATE)
                .build();

        TaskResponse taskResponse = new TaskResponse(
                taskId,
                "Updated Task",
                "Updated Description",
                "DONE",
                FIXED_DATE
        );

        when(taskRepository.findById(taskId)).thenReturn(Optional.of(existingTask));
        when(taskRepository.save(existingTask)).thenReturn(updatedTask);
        when(taskMapper.toResponse(updatedTask)).thenReturn(taskResponse);

        // Act
        TaskResponse result = taskService.update(taskId, taskRequest);

        // Assert
        assertNotNull(result);
        assertEquals(taskResponse, result);
        verify(taskRepository, times(1)).findById(taskId);
        verify(taskRepository, times(1)).save(existingTask);
    }

    @Test
    void getTask_shouldReturnTaskResponse() {
        // Arrange
        Long taskId = 1L;
        Task foundTask = Task.builder()
                .id(1L)
                .title("Test Task")
                .description("Description")
                .status(Task.Status.OPEN)
                .dueDate(FIXED_DATE)
                .build();

        TaskResponse taskResponse = new TaskResponse(
                1L,
                "Test Task",
                "Description",
                "OPEN",
                FIXED_DATE
        );
        when(taskRepository.findById(taskId)).thenReturn(Optional.of(foundTask));
        when(taskMapper.toResponse(foundTask)).thenReturn(taskResponse);

        // Act
        TaskResponse result = taskService.get(taskId);

        // Assert
        assertEquals(taskResponse, result);
    }

    @Test
    void getTask_shouldThrowException_whenTaskNotFound() {
        // Arrange
        Long taskId = 1L;
        when(taskRepository.findById(taskId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () -> taskService.get(taskId));
    }

    @Test
    void deleteTask_shouldDeleteTask() {
        // Arrange
        Long taskId = 1L;

        // Act
        taskService.delete(taskId);

        // Assert
        verify(taskRepository, times(1)).deleteById(taskId);
    }

    @Test
    void getAllTasks_shouldReturnListOfTaskResponses() {
        // Arrange
        Task task1 = Task.builder()
                .id(1L)
                .title("Task 1")
                .description("Description 1")
                .status(Task.Status.OPEN)
                .dueDate(FIXED_DATE)
                .build();

        Task task2 = Task.builder()
                .id(2L)
                .title("Task 2")
                .description("Description 2")
                .status(Task.Status.DONE)
                .dueDate(FIXED_DATE)
                .build();

        TaskResponse taskResponse1 = new TaskResponse(
                1L,
                "Task 1",
                "Description 1",
                "OPEN",
                FIXED_DATE
        );

        TaskResponse taskResponse2 = new TaskResponse(
                2L,
                "Task 2",
                "Description 2",
                "DONE",
                FIXED_DATE
        );

        when(taskRepository.findAll()).thenReturn(List.of(task1, task2));
        when(taskMapper.toResponse(task1)).thenReturn(taskResponse1);
        when(taskMapper.toResponse(task2)).thenReturn(taskResponse2);

        // Act
        List<TaskResponse> result = taskService.getAll();

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(taskRepository, times(1)).findAll();
    }
}