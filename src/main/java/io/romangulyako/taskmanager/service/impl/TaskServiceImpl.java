package io.romangulyako.taskmanager.service.impl;

import io.romangulyako.taskmanager.dto.TaskRequest;
import io.romangulyako.taskmanager.dto.TaskResponse;
import io.romangulyako.taskmanager.entity.Task;
import io.romangulyako.taskmanager.mapper.TaskMapper;
import io.romangulyako.taskmanager.repository.TaskRepository;
import io.romangulyako.taskmanager.service.TaskService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class TaskServiceImpl implements TaskService {
    private final TaskRepository taskRepository;
    private final TaskMapper taskMapper;

    @Override
    public TaskResponse create(TaskRequest taskRequest) {
        return taskMapper.toResponse(taskRepository
                .save(taskMapper
                        .toEntity(taskRequest)));
    }

    @Override
    public TaskResponse update(Long id, TaskRequest taskRequest) {
        Task task = this.getById(id);
        task.setTitle(taskRequest.title());
        task.setDescription(taskRequest.description());
        task.setStatus(Task.Status.valueOf(taskRequest.status()));
        task.setDueDate(taskRequest.dueDate());

        return taskMapper.toResponse(taskRepository.save(task));
    }

    @Override
    public void delete(Long id) {
        taskRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public TaskResponse get(Long id) {
        return taskMapper.toResponse(this.getById(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<TaskResponse> getAll() {
        return taskRepository.findAll().stream()
                .map(taskMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<TaskResponse> getAllByStatus(String status) {
        return taskRepository.findByStatus(Task.Status.valueOf(status)).stream()
                .map(taskMapper::toResponse)
                .toList();
    }

    private Task getById(Long id) {
        return taskRepository.findById(id).orElseThrow(() ->
                new EntityNotFoundException("Task with id " + id + " not found"));
    }
}
