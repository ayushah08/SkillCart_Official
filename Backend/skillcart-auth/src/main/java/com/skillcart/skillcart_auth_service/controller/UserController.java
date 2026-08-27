package com.skillcart.skillcart_auth_service.controller;

import com.skillcart.skillcart_auth_service.dto.UserResponse;
import com.skillcart.skillcart_auth_service.entity.User;
import com.skillcart.skillcart_auth_service.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserRepository userRepository;


    // GET ALL USERS EXCEPT CURRENT USER
    @GetMapping
    public List<UserResponse> getAllUsers(
            Authentication authentication
    ) {

        String currentUsername =
                authentication.getName();

        return userRepository.findAll()
                .stream()

                .filter(user ->
                        !user.getUsername()
                                .equals(currentUsername)
                )

                .map(this::mapToResponse)

                .toList();
    }


    // GET USER BY ID
    @GetMapping("/{userId}")
    public UserResponse getUserById(
            @PathVariable UUID userId
    ) {

        User user =
                userRepository
                        .findById(userId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "User not found"
                                )
                        );

        return mapToResponse(user);
    }


    private UserResponse mapToResponse(
            User user
    ) {

        return UserResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .build();
    }
}