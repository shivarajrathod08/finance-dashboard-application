package com.finance.dashboard.service;

import com.finance.dashboard.dto.request.AssignRoleRequest;
import com.finance.dashboard.dto.request.CreateUserRequest;
import com.finance.dashboard.dto.response.PagedResponse;
import com.finance.dashboard.dto.response.UserResponse;
import org.springframework.data.domain.Pageable;

public interface UserService {

    UserResponse createUser(CreateUserRequest request);

    UserResponse getUserById(Long id);

    PagedResponse<UserResponse> getAllUsers(Pageable pageable);

    PagedResponse<UserResponse> searchUsers(String query, Pageable pageable);

    UserResponse assignRole(Long userId, AssignRoleRequest request);

    UserResponse activateUser(Long userId);

    UserResponse deactivateUser(Long userId);

    void deleteUser(Long userId);
}