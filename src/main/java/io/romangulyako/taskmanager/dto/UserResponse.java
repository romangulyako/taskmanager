package io.romangulyako.taskmanager.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Set;

@Schema(description = "User response")
public record UserResponse(
        @Schema(description = "ID of the user", example = "1")
        Long id,

        @Schema(description = "Username", example = "romangulyako")
        String username,

        @Schema(description = "User roles", example = "[\"USER\", \"ADMIN\"]")
        Set<String> roles
) {}
