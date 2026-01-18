package io.romangulyako.taskmanager.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Authentication request")
public record AuthRequest(
        @Schema(description = "Username", example = "romangulyako")
        String username,

        @Schema(description = "Password", example = "securePassword123")
        String password
) {}
