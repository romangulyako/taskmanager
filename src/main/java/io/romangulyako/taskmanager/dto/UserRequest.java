package io.romangulyako.taskmanager.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.Set;

@Schema(description = "User creation request")
public record UserRequest(
        @Schema(description = "Username", example = "romangulyako")
        @NotBlank(message = "Username is required")
        String username,

        @Schema(description = "Password", example = "securePassword123")
        @NotBlank(message = "Password is required")
        @Size(min = 6, message = "Password must be at least 6 characters long")
        String password,

        @Schema(description = "User roles", example = "[\"USER\", \"ADMIN\"]")
        Set<String> roles
) {}
