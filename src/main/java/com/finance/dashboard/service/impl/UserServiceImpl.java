package com.finance.dashboard.service.impl;

import com.finance.dashboard.dto.request.AssignRoleRequest;
import com.finance.dashboard.dto.request.CreateUserRequest;
import com.finance.dashboard.dto.response.PagedResponse;
import com.finance.dashboard.dto.response.UserResponse;
import com.finance.dashboard.entity.Role;
import com.finance.dashboard.entity.User;
import com.finance.dashboard.enums.RoleName;
import com.finance.dashboard.exception.BadRequestException;
import com.finance.dashboard.exception.DuplicateResourceException;
import com.finance.dashboard.exception.ResourceNotFoundException;
import com.finance.dashboard.repository.RoleRepository;
import com.finance.dashboard.repository.UserRepository;
import com.finance.dashboard.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserResponse createUser(CreateUserRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new DuplicateResourceException(
                    "Username '" + request.getUsername() + "' is already taken");
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException(
                    "Email '" + request.getEmail() + "' is already registered");
        }

        RoleName roleName = parseRole(request.getRole(), RoleName.VIEWER);
        Role role = findRole(roleName);

        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .fullName(request.getFullName())
                .role(role)
                .active(true)
                .build();

        User saved = userRepository.save(user);
        log.info("Created user '{}' with role '{}'", saved.getUsername(), roleName);
        return toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponse getUserById(Long id) {
        return toResponse(findUserById(id));
    }

    @Override
    @Transactional(readOnly = true)
    public PagedResponse<UserResponse> getAllUsers(Pageable pageable) {
        Page<User> page = userRepository.findAll(pageable);
        return toPagedResponse(page);
    }

    @Override
    @Transactional(readOnly = true)
    public PagedResponse<UserResponse> searchUsers(String query, Pageable pageable) {
        Page<User> page = userRepository.searchUsers(query, pageable);
        return toPagedResponse(page);
    }

    @Override
    public UserResponse assignRole(Long userId, AssignRoleRequest request) {
        User user = findUserById(userId);
        RoleName roleName = parseRole(request.getRoleName(), null);
        if (roleName == null) {
            throw new BadRequestException("Invalid role: " + request.getRoleName());
        }
        Role role = findRole(roleName);
        user.setRole(role);
        log.info("Assigned role '{}' to user '{}'", roleName, user.getUsername());
        return toResponse(userRepository.save(user));
    }

    @Override
    public UserResponse activateUser(Long userId) {
        User user = findUserById(userId);
        user.setActive(true);
        log.info("Activated user '{}'", user.getUsername());
        return toResponse(userRepository.save(user));
    }

    @Override
    public UserResponse deactivateUser(Long userId) {
        User user = findUserById(userId);
        user.setActive(false);
        log.info("Deactivated user '{}'", user.getUsername());
        return toResponse(userRepository.save(user));
    }

    @Override
    public void deleteUser(Long userId) {
        User user = findUserById(userId);
        userRepository.delete(user);
        log.info("Deleted user '{}'", user.getUsername());
    }

    // ── Helpers ──────────────────────────────────────────────────────────────

    private User findUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));
    }

    private Role findRole(RoleName roleName) {
        return roleRepository.findByName(roleName)
                .orElseThrow(() -> new ResourceNotFoundException("Role", "name", roleName));
    }

    private RoleName parseRole(String roleStr, RoleName defaultRole) {
        if (roleStr == null || roleStr.isBlank()) return defaultRole;
        try {
            return RoleName.valueOf(roleStr.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("Invalid role: '" + roleStr +
                    "'. Valid values: VIEWER, ANALYST, ADMIN");
        }
    }

    private UserResponse toResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .role(user.getRole().getName().name())
                .active(user.isActive())
                .createdAt(user.getCreatedAt())
                .build();
    }

    private PagedResponse<UserResponse> toPagedResponse(Page<User> page) {
        return PagedResponse.<UserResponse>builder()
                .content(page.getContent().stream().map(this::toResponse).toList())
                .page(page.getNumber())
                .size(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .last(page.isLast())
                .build();
    }
}