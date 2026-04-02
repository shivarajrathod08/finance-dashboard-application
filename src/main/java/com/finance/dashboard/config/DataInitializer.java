package com.finance.dashboard.config;

import com.finance.dashboard.entity.Role;
import com.finance.dashboard.entity.User;
import com.finance.dashboard.enums.RoleName;
import com.finance.dashboard.repository.RoleRepository;
import com.finance.dashboard.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;

/**
 * Seeds the database on first startup:
 *   - Creates the three roles (VIEWER, ANALYST, ADMIN) if absent.
 *   - Creates a default ADMIN user if no admin exists.
 *
 * CommandLineRunner runs after the application context is fully loaded,
 * ensuring JPA/Hibernate has already created / updated the schema.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(String... args) {
        seedRoles();
        seedAdminUser();
    }

    private void seedRoles() {
        Arrays.stream(RoleName.values()).forEach(roleName -> {
            if (roleRepository.findByName(roleName).isEmpty()) {
                roleRepository.save(Role.builder().name(roleName).build());
                log.info("Created role: {}", roleName);
            }
        });
    }

    private void seedAdminUser() {
        if (!userRepository.existsByUsername("admin")) {
            Role adminRole = roleRepository.findByName(RoleName.ADMIN)
                    .orElseThrow(() -> new IllegalStateException("ADMIN role not found after seeding"));

            User admin = User.builder()
                    .username("admin")
                    .email("admin@finance.com")
                    .password(passwordEncoder.encode("Admin@123"))
                    .fullName("System Administrator")
                    .role(adminRole)
                    .active(true)
                    .build();

            userRepository.save(admin);
            log.info("Default admin user created (username: admin, password: Admin@123)");
            log.warn("⚠️  Change the default admin password before going to production!");
        }
    }
}