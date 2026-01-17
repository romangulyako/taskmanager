package io.romangulyako.taskmanager.service.impl;

import io.romangulyako.taskmanager.dto.AuthRequest;
import io.romangulyako.taskmanager.dto.AuthResponse;
import io.romangulyako.taskmanager.security.JwtTokenProvider;
import io.romangulyako.taskmanager.service.CustomUserDetailsService;
import io.romangulyako.taskmanager.service.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class JwtServiceImpl implements JwtService {
    private final CustomUserDetailsService customUserDetailsService;
    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public AuthResponse login(AuthRequest authRequest) {
        UserDetails userDetails = customUserDetailsService.loadUserByUsername(authRequest.username());

        if (!new BCryptPasswordEncoder().matches(authRequest.password(), userDetails.getPassword())) {
            throw new BadCredentialsException("Invalid password");
        }

        Authentication authentication = new UsernamePasswordAuthenticationToken(
                userDetails,
                null,
                userDetails.getAuthorities()
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String token = jwtTokenProvider.generateToken(authentication);

        return new AuthResponse(token);
    }
}
