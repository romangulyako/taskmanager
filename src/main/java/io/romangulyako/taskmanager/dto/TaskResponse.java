package io.romangulyako.taskmanager.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;

@Schema(description = "Task response")
public record TaskResponse(
        @Schema(description = "ID of the task", example = "1")
        Long id,

        @Schema(description = "Title of the task", example = "Complete documentation")
        String title,

        @Schema(description = "Description of the task", example = "Write OpenAPI documentation")
        String description,

        @Schema(description = "Status of the task", example = "IN_PROGRESS",
                allowableValues = {"OPEN", "IN_PROGRESS", "DONE"})
        String status,

        @Schema(description = "Due date of the task", example = "2026-12-31")
        LocalDate dueDate
) {
}
