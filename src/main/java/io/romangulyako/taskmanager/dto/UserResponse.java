package io.romangulyako.taskmanager.dto;

import java.util.Set;

public record UserResponse(
        Long id,
        String username,
        Set<String> roles
) {}
