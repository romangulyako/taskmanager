package io.romangulyako.taskmanager.dto;

public record AuthRequest(
        String username,
        String password
) {}
