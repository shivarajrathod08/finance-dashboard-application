package com.finance.dashboard.service.impl;

import com.finance.dashboard.dto.request.CreateUserRequest;
import com.finance.dashboard.dto.request.LoginRequest;
import com.finance.dashboard.dto.response.AuthResponse;
import com.finance.dashboard.dto.response.UserResponse;
import com.finance.dashboard.entity.User;
import com.finance.dashboard.repository.UserRepository;
import com.finance.dashboard.security.jwt.JwtTokenProvider;
import com.finance.dashboard.service.AuthService;
import com.finance.dashboard.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;
    private final UserService userService;
    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public AuthResponse login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(), request.getPassword()));

        String token = tokenProvider.generateToken(authentication);

        User user = userRepository.findByUsername(request.getUsername()).orElseThrow();

        log.info("User '{}' logged in successfully", request.getUsername());

        return AuthResponse.builder()
                .token(token)
                .tokenType("Bearer")
                .username(user.getUsername())
                .role(user.getRole().getName().name())
                .expiresAt(tokenProvider.getExpiryFromToken(token))
                .build();
    }

    @Override
    @Transactional
    public UserResponse register(CreateUserRequest request) {
        // Self-registration always gets VIEWER role; role assignment requires ADMIN
        request.setRole("VIEWER");
        return userService.createUser(request);
    }
}