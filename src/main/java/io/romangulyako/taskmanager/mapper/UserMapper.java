package io.romangulyako.taskmanager.mapper;

import io.romangulyako.taskmanager.dto.UserResponse;
import io.romangulyako.taskmanager.entity.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserResponse toResponse(User user);
}
