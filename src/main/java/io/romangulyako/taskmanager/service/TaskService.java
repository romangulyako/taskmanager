package io.romangulyako.taskmanager.service;

import io.romangulyako.taskmanager.dto.TaskRequest;
import io.romangulyako.taskmanager.dto.TaskResponse;

import java.util.List;

public interface TaskService {
    TaskResponse create(TaskRequest taskRequest);
    TaskResponse update(Long id, TaskRequest taskRequest);
    void delete(Long id);
    TaskResponse get(Long id);
    List<TaskResponse> getAll();
    List<TaskResponse> getAllByStatus(String status);
}
