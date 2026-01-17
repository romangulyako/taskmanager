package io.romangulyako.taskmanager.service.impl;

import io.romangulyako.taskmanager.dto.AuthRequest;
import io.romangulyako.taskmanager.dto.AuthResponse;
import io.romangulyako.taskmanager.security.JwtTokenProvider;
import io.romangulyako.taskmanager.service.CustomUserDetailsService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JwtServiceImplTest {

    @Mock
    private CustomUserDetailsService customUserDetailsService;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @InjectMocks
    private JwtServiceImpl jwtService;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Test
    void login_shouldReturnAuthResponse_whenCredentialsAreValid() {
        // Arrange
        AuthRequest authRequest = new AuthRequest("user", "password");
        String hashedPassword = passwordEncoder.encode("password");

        UserDetails userDetails = new org.springframework.security.core.userdetails.User(
                "user",
                hashedPassword,
                Collections.singleton(new SimpleGrantedAuthority("ROLE_USER"))
        );

        when(customUserDetailsService.loadUserByUsername("user")).thenReturn(userDetails);
        when(jwtTokenProvider.generateToken(any(Authentication.class))).thenReturn("test-token");

        // Act
        AuthResponse authResponse = jwtService.login(authRequest);

        // Assert
        assertNotNull(authResponse);
        assertEquals("test-token", authResponse.token());
        verify(customUserDetailsService, times(1)).loadUserByUsername("user");
        verify(jwtTokenProvider, times(1)).generateToken(any(Authentication.class));
    }

    @Test
    void login_shouldThrowBadCredentialsException_whenPasswordIsInvalid() {
        // Arrange
        AuthRequest authRequest = new AuthRequest("user", "wrong-password");
        String hashedPassword = passwordEncoder.encode("password");

        UserDetails userDetails = new org.springframework.security.core.userdetails.User(
                "user",
                hashedPassword,
                Collections.emptyList()
        );

        when(customUserDetailsService.loadUserByUsername("user")).thenReturn(userDetails);

        // Act & Assert
        assertThrows(BadCredentialsException.class, () -> jwtService.login(authRequest));
    }

    @Test
    void login_shouldThrowUsernameNotFoundException_whenUserDoesNotExist() {
        // Arrange
        AuthRequest authRequest = new AuthRequest("nonexistent", "password");

        when(customUserDetailsService.loadUserByUsername("nonexistent"))
                .thenThrow(new UsernameNotFoundException("User not found"));

        // Act & Assert
        assertThrows(UsernameNotFoundException.class, () -> jwtService.login(authRequest));
    }
}