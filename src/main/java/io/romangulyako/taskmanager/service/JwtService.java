package io.romangulyako.taskmanager.service;

import io.romangulyako.taskmanager.dto.AuthRequest;
import io.romangulyako.taskmanager.dto.AuthResponse;

public interface JwtService {
    AuthResponse login(AuthRequest authRequest);
}
