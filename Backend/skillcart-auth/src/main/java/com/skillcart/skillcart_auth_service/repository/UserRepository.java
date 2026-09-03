package com.skillcart.skillcart_auth_service.repository;

import com.skillcart.skillcart_auth_service.dto.UserProfileResponse;
import com.skillcart.skillcart_auth_service.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    boolean existsByUsername(String username);

    Optional<User> findByUsername(String username);

    UserProfileResponse getUserProfile(UUID userId);
}

