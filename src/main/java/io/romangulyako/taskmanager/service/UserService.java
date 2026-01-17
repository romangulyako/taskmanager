package io.romangulyako.taskmanager.service;

import io.romangulyako.taskmanager.dto.UserRequest;
import io.romangulyako.taskmanager.dto.UserResponse;

import java.util.List;

public interface UserService {
    UserResponse createUser(UserRequest userRequest);
    UserResponse updateUser(Long id, UserRequest userRequest);
    void deleteUser(Long id);
    UserResponse getUserById(Long id);
    List<UserResponse> getAllUsers();
}
