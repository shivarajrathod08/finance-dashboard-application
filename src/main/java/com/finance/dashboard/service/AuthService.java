package com.finance.dashboard.service;

import com.finance.dashboard.dto.request.CreateUserRequest;
import com.finance.dashboard.dto.request.LoginRequest;
import com.finance.dashboard.dto.response.AuthResponse;
import com.finance.dashboard.dto.response.UserResponse;

public interface AuthService {
    AuthResponse login(LoginRequest request);
    UserResponse register(CreateUserRequest request);
}