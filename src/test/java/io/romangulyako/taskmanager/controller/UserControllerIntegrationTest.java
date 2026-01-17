package io.romangulyako.taskmanager.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.romangulyako.taskmanager.dto.UserRequest;
import io.romangulyako.taskmanager.entity.User;
import io.romangulyako.taskmanager.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;
import java.util.Set;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.hamcrest.Matchers.hasSize;

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class UserControllerIntegrationTest {
    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:17.6");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @Test
    void createUser_shouldReturnCreatedUser() throws Exception {
        // Arrange
        UserRequest userRequest = new UserRequest(
                "testuser",
                "password123",
                Set.of("USER"));

        // Act & Assert
        mockMvc.perform(post("/api/v1/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.username").value(userRequest.username()));
    }

    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @Test
    void updateUser_shouldReturnUpdatedUser() throws Exception {
        // Arrange
        User user = User.builder()
                .username("olduser")
                .password("password123")
                .roles(Set.of("USER"))
                .build();

        User savedUser = userRepository.save(user);

        UserRequest userRequest = new UserRequest(
                "newuser",
                "newpassword123",
                Set.of("ADMIN"));

        // Act & Assert
        mockMvc.perform(put("/api/v1/users/{id}", savedUser.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value(userRequest.username()));
    }

    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @Test
    void deleteUser_shouldReturnNoContent() throws Exception {
        // Arrange
        User user = User.builder()
                .username("userToDelete")
                .password("password123")
                .roles(Set.of("USER"))
                .build();

        User savedUser = userRepository.save(user);

        // Act & Assert
        mockMvc.perform(delete("/api/v1/users/{id}", savedUser.getId()))
                .andExpect(status().isNoContent());
    }

    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @Test
    void getUserById_shouldReturnUser_whenUserExists() throws Exception {
        // Arrange
        User user = User.builder()
                .username("testuser")
                .password("password123")
                .roles(Set.of("USER"))
                .build();

        User savedUser = userRepository.save(user);

        // Act & Assert
        mockMvc.perform(get("/api/v1/users/{id}", savedUser.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value(savedUser.getUsername()));
    }

    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @Test
    void getAllUsers_shouldReturnListOfUsers() throws Exception {
        // Arrange
        User user1 = User.builder()
                .username("user1")
                .password("password123")
                .roles(Set.of("USER"))
                .build();

        User user2 = User.builder()
                .username("user2")
                .password("password123")
                .roles(Set.of("USER"))
                .build();

        userRepository.saveAll(List.of(user1, user2));

        // Act & Assert
        mockMvc.perform(get("/api/v1/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));
    }

    @AfterEach
    public void deleteAllUsers() {
        userRepository.deleteAll();
    }
}
