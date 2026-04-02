package com.finance.dashboard.repository;

import com.finance.dashboard.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String email);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    /**
     * Case-insensitive full-text search across username, email, and fullName.
     */
    @Query("""
        SELECT u FROM User u
        WHERE LOWER(u.username) LIKE LOWER(CONCAT('%', :query, '%'))
           OR LOWER(u.email)    LIKE LOWER(CONCAT('%', :query, '%'))
           OR LOWER(u.fullName) LIKE LOWER(CONCAT('%', :query, '%'))
        """)
    Page<User> searchUsers(@Param("query") String query, Pageable pageable);
}