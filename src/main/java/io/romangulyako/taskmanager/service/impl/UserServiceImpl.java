package io.romangulyako.taskmanager.service.impl;

import io.romangulyako.taskmanager.dto.UserRequest;
import io.romangulyako.taskmanager.dto.UserResponse;
import io.romangulyako.taskmanager.entity.User;
import io.romangulyako.taskmanager.mapper.UserMapper;
import io.romangulyako.taskmanager.repository.UserRepository;
import io.romangulyako.taskmanager.service.UserService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;

    @Override
    public UserResponse createUser(UserRequest userRequest) {
        log.debug("Creating user with username: {}", userRequest.username());
        Set<String> roles = userRequest.roles() != null && !userRequest.roles().isEmpty()
                ? userRequest.roles()
                : Set.of("USER");

        User user = User.builder()
                .username(userRequest.username())
                .password(passwordEncoder.encode(userRequest.password()))
                .roles(roles)
                .build();
        User savedUser = userRepository.save(user);
        log.info("User created with id: {}", savedUser.getId());

        return userMapper.toResponse(savedUser);
    }

    @Override
    public UserResponse updateUser(Long id, UserRequest userRequest) {
        log.debug("Updating user with id: {}", id);
        User user = this.getById(id);
        user.setUsername(userRequest.username());
        user.setPassword(passwordEncoder.encode(userRequest.password()));
        user.setRoles(userRequest.roles());
        User updatedUser = userRepository.save(user);
        log.info("User updated with id: {}", updatedUser.getId());

        return userMapper.toResponse(updatedUser);
    }

    @Override
    public void deleteUser(Long id) {
        log.debug("Deleting user with id: {}", id);
        userRepository.deleteById(id);
        log.info("User deleted with id: {}", id);
    }

    @Transactional(readOnly = true)
    @Override
    public UserResponse getUserById(Long id) {
        log.debug("Fetching user by id: {}", id);
        return userMapper.toResponse(this.getById(id));
    }

    @Transactional(readOnly = true)
    @Override
    public List<UserResponse> getAllUsers() {
        log.debug("Fetching all users");
        return userRepository.findAll().stream()
                .map(userMapper::toResponse)
                .toList();
    }

    private User getById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + id));
    }
}
