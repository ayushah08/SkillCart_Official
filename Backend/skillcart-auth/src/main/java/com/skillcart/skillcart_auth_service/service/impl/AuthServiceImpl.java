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
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final RestClient restClient;


    // =========================================================
    // REGISTER
    // =========================================================

    @Override
    public AuthResponse register(RegisterRequest registerRequest) {

        if (userRepository.existsByEmail(registerRequest.getEmail())) {
            throw new EmailAlreadyExistsException(
                    "User with email already exists"
            );
        }

        if (userRepository.existsByUsername(registerRequest.getUsername())) {
            throw new UsernameAlreadyExistsException(
                    "Username already exists"
            );
        }

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

        User savedUser = userRepository.save(user);

        String token = jwtService.generateJwtToken(savedUser);

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

        User user = userRepository.findByEmail(
                        loginRequest.getEmail()
                )
                .orElseThrow(() ->
                        new EmailNotFoundException(
                                loginRequest.getEmail()
                                        + " Email Not Found"
                        )
                );


        if (!passwordEncoder.matches(
                loginRequest.getPassword(),
                user.getPassword()
        )) {
            throw new IncorrectPasswordException(
                    "Password is incorrect"
            );
        }


        // Generate JWT
        String token = jwtService.generateJwtToken(
                (UserDetails) user
        );


        // Update login time
        user.setUpdatedAt(LocalDateTime.now());
        userRepository.save(user);


        // Get Resume ID
        // Login must NOT fail if Resume Service is unavailable
        Long rid = getResumeId(user.getId());


        return new AuthResponse(
                token,
                "Welcome Back: "
                        + user.getUsername()
                        + " Login Successful",
                rid
        );
    }


    // =========================================================
    // GET RESUME ID FROM RESUME SERVICE
    // =========================================================

    private Long getResumeId(UUID userId) {

        try {

            ResumeResponse resumeResponse =
                    restClient.get()
                            .uri(
                                    "https://skillcart-resume.onrender.com/api/v1/resume/user/{userId}",
                                    userId
                            )
                            .retrieve()
                            .body(ResumeResponse.class);



            if (resumeResponse == null ||
                    !Boolean.TRUE.equals(
                            resumeResponse.getHasResume()
                    )) {

                System.out.println("Resume Not Found");

                return null;
            }


            System.out.println(
                    "Resume Id: "
                            + resumeResponse.getResumeId()
            );

            return resumeResponse.getResumeId();

        } catch (RestClientException e) {

            System.out.println(
                    "Could not fetch resume: "
                            + e.getMessage()
            );

            return null;
        }
    }
}