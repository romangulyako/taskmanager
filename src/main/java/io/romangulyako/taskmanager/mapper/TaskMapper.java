package io.romangulyako.taskmanager.mapper;

import io.romangulyako.taskmanager.dto.TaskRequest;
import io.romangulyako.taskmanager.dto.TaskResponse;
import io.romangulyako.taskmanager.entity.Task;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface TaskMapper {
    @Mapping(target = "id", ignore = true)
    Task toEntity(TaskRequest taskRequest);
    TaskResponse toResponse(Task task);
}
