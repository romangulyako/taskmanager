package io.romangulyako.taskmanager.service.impl;

import io.romangulyako.taskmanager.dto.TaskRequest;
import io.romangulyako.taskmanager.dto.TaskResponse;
import io.romangulyako.taskmanager.entity.Task;
import io.romangulyako.taskmanager.mapper.TaskMapper;
import io.romangulyako.taskmanager.repository.TaskRepository;
import io.romangulyako.taskmanager.service.TaskService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class TaskServiceImpl implements TaskService {
    private final TaskRepository taskRepository;
    private final TaskMapper taskMapper;

    @Override
    public TaskResponse create(TaskRequest taskRequest) {
        log.debug("Creating task with title: {}", taskRequest.title());
        Task savedTask = taskRepository.save(taskMapper
                        .toEntity(taskRequest));
        log.info("Task created with id: {}", savedTask.getId());

        return taskMapper.toResponse(savedTask);
    }

    @Override
    public TaskResponse update(Long id, TaskRequest taskRequest) {
        log.debug("Updating task with id: {}", id);
        Task task = this.getById(id);
        task.setTitle(taskRequest.title());
        task.setDescription(taskRequest.description());
        task.setStatus(Task.Status.valueOf(taskRequest.status()));
        task.setDueDate(taskRequest.dueDate());
        Task updatedTask = taskRepository.save(task);
        log.info("Task updated with id: {}", updatedTask.getId());

        return taskMapper.toResponse(updatedTask);
    }

    @Override
    public void delete(Long id) {
        log.debug("Deleting task with id: {}", id);
        taskRepository.deleteById(id);
        log.info("Task deleted with id: {}", id);
    }

    @Override
    @Transactional(readOnly = true)
    public TaskResponse get(Long id) {
        log.debug("Fetching task with id: {}", id);
        return taskMapper.toResponse(this.getById(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<TaskResponse> getAll() {
        log.debug("Fetching all tasks");
        return taskRepository.findAll().stream()
                .map(taskMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<TaskResponse> getAllByStatus(String status) {
        log.debug("Fetching tasks by status: {}", status);
        return taskRepository.findByStatus(Task.Status.valueOf(status)).stream()
                .map(taskMapper::toResponse)
                .toList();
    }

    private Task getById(Long id) {
        return taskRepository.findById(id).orElseThrow(() ->
                new EntityNotFoundException("Task with id " + id + " not found"));
    }
}
