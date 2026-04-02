package com.finance.dashboard.security;

import com.finance.dashboard.entity.User;
import com.finance.dashboard.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Bridges Spring Security's UserDetailsService contract with our JPA User entity.
 *
 * The role is mapped to a GrantedAuthority prefixed with "ROLE_" so that
 * Spring Security's @PreAuthorize("hasRole('ADMIN')") works out of the box.
 *
 * The `active` flag is mapped to UserDetails#isEnabled — Spring Security
 * will throw DisabledException automatically before authentication proceeds.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException(
                        "User not found with username: " + username));

        log.debug("Loaded user '{}' with role '{}'", username, user.getRole().getName());

        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getUsername())
                .password(user.getPassword())
                .authorities(List.of(new SimpleGrantedAuthority(
                        "ROLE_" + user.getRole().getName().name())))
                .disabled(!user.isActive())
                .build();
    }
}