package com.skillcart.skillcart_auth_service.service.impl;

import com.skillcart.skillcart_auth_service.dto.*;
import com.skillcart.skillcart_auth_service.entity.User;
import com.skillcart.skillcart_auth_service.enums.Role;
import com.skillcart.skillcart_auth_service.exception.EmailAlreadyExistsException;
import com.skillcart.skillcart_auth_service.exception.EmailNotFoundException;
import com.skillcart.skillcart_auth_service.exception.IncorrectPasswordException;
import com.skillcart.skillcart_auth_service.exception.UsernameAlreadyExistsException;
import com.skillcart.skillcart_auth_service.repository.UserRepository;
import com.skillcart.skillcart_auth_service.security.JwtService;
import com.skillcart.skillcart_auth_service.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtService jwtService;

    private final RestClient restClient;


    // =========================================================
    // REGISTER
    // =========================================================

    @Override
    public AuthResponse register(RegisterRequest registerRequest) {

        // Check email
        if (userRepository.existsByEmail(registerRequest.getEmail())) {
            throw new EmailAlreadyExistsException(
                    "User with email already exists"
            );
        }

        // Check username
        if (userRepository.existsByUsername(registerRequest.getUsername())) {
            throw new UsernameAlreadyExistsException(
                    "Username already exists"
            );
        }

        // Create User
        User user = User.builder()
                .username(registerRequest.getUsername())
                .email(registerRequest.getEmail())
                .password(
                        passwordEncoder.encode(
                                registerRequest.getPassword()
                        )
                )
                .role(Role.USER)
                .verified(false)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        // Save User
        User savedUser = userRepository.save(user);

        // Generate JWT
        String token = jwtService.generateJwtToken(savedUser);

        /*
         * Resume is NOT created during registration.
         *
         * Therefore rid = null.
         *
         * Frontend can use this to open the resume upload page.
         */

        return new AuthResponse(
                token,
                "User Registered Successfully",
                null
        );
    }


    // =========================================================
    // LOGIN
    // =========================================================

    @Override
    public AuthResponse login(LoginRequest loginRequest) {

        // Find user by email
        Optional<User> user = userRepository.findByEmail(
                loginRequest.getEmail()
        );

        // Email doesn't exist
        if (user.isEmpty()) {
            throw new EmailNotFoundException(
                    loginRequest.getEmail() + " Email Not Found"
            );
        }

        User user1 = user.get();

        // Check password
        if (!passwordEncoder.matches(
                loginRequest.getPassword(),
                user1.getPassword()
        )) {
            throw new IncorrectPasswordException(
                    "Password is incorrect"
            );
        }

        // Generate JWT
        String token = jwtService.generateJwtToken(
                (UserDetails) user1
        );
        user1.setUpdatedAt(LocalDateTime.now());
        Long rid = getResumeId(user1.getId());

        userRepository.save(user1);

        /*
         * Ask Resume Service whether this user
         * already has a resume.
         *
         * If resume exists:
         *      rid = resume UUID
         *
         * If resume doesn't exist:
         *      rid = null
         */




        return new AuthResponse(
                token,
                "Welcome Back: "
                        + user1.getUsername()
                        + " Login Successful" + "Resume Id is",
                        rid

        );
    }


    // =========================================================
    // GET RESUME ID FROM RESUME SERVICE
    // =========================================================

    private Long getResumeId(UUID userId) {

        ResumeResponse resumeResponse =
                restClient.get()
                        .uri(
                                "https://skillcart-resume.onrender.com/api/v1/resume/user/{userId}",
                                userId
                        )
                        .retrieve()
                        .body(ResumeResponse.class);

        if (resumeResponse == null ||
                !Boolean.TRUE.equals(resumeResponse.getHasResume())) {

            return null;
        }

        return resumeResponse.getResumeId();
    }
}