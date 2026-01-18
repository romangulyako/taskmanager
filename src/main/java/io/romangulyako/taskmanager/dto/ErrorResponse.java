package io.romangulyako.taskmanager.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Schema(description = "Error response")
public record ErrorResponse(
        @Schema(description = "Timestamp of the error", example = "2024-01-01T12:00:00")
        LocalDateTime timestamp,

        @Schema(description = "HTTP status code", example = "404")
        int status,

        @Schema(description = "HTTP status description", example = "Not Found")
        String error,

        @Schema(description = "Error message", example = "Task not found")
        String message,

        @Schema(description = "Path where the error occurred", example = "/api/v1/tasks/1")
        String path
) {}
