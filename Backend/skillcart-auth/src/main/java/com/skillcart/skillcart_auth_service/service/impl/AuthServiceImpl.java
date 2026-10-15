
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
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final RestClient restClient;
    private final GmailServices gmailServices;

    // =========================================================
    // REGISTER
    // Account creation is allowed only after email verification.
    // =========================================================

    @Override
    public AuthResponse register(RegisterRequest registerRequest) {

        String email = registerRequest.getEmail()
                .trim()
                .toLowerCase(java.util.Locale.ROOT);

        // Check whether the email is already registered.
        if (userRepository.existsByEmail(email)) {
            throw new EmailAlreadyExistsException(
                    "User with email already exists"
            );
        }

        // Check whether the username is already registered.
        if (userRepository.existsByUsername(
                registerRequest.getUsername()
        )) {
            throw new UsernameAlreadyExistsException(
                    "Username already exists"
            );
        }

        // Require a successful OTP verification before creating
        // the account. Verification expires after 10 minutes.
        if (!gmailServices.consumeEmailVerification(email)) {
            throw new IllegalStateException(
                    "Please verify your email before creating an account"
            );
        }

        LocalDateTime now = LocalDateTime.now();

        User user = User.builder()
                .username(registerRequest.getUsername())
                .email(email)
                .password(
                        passwordEncoder.encode(
                                registerRequest.getPassword()
                        )
                )
                .role(Role.USER)
                .verified(true)
                .createdAt(now)
                .updatedAt(now)
                .build();

        User savedUser = userRepository.save(user);

        // Do not issue a JWT during registration.
        // The user can log in after the account is created.

        return new AuthResponse(
                null,
                "Account created successfully. Please log in.",
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

        // Never issue a JWT to an unverified user.
        if (!user.isVerified()) {
            throw new IllegalStateException(
                    "Please verify your email before logging in"
            );
        }

        String token = jwtService.generateJwtToken(
                (UserDetails) user
        );

        user.setUpdatedAt(LocalDateTime.now());
        userRepository.save(user);

        // Resume service failure must not prevent login.
        Long resumeId = getResumeId(user.getId());

        return new AuthResponse(
                token,
                "Welcome Back: "
                        + user.getUsername()
                        + " Login Successful",
                resumeId
        );
    }

    // =========================================================
    // GET RESUME ID FROM RESUME SERVICE
    // =========================================================

    private Long getResumeId(UUID userId) {

        try {

            ResumeResponse resumeResponse = restClient.get()
                    .uri(
                            "https://skillcart-resume.onrender.com/api/v1/resume/user/{userId}",
                            userId
                    )
                    .retrieve()
                    .body(ResumeResponse.class);

            if (resumeResponse == null
                    || !Boolean.TRUE.equals(
                    resumeResponse.getHasResume()
            )) {
                return null;
            }

            return resumeResponse.getResumeId();

        } catch (RestClientException e) {

            System.err.println(
                    "Could not fetch resume: " + e.getMessage()
            );

            return null;
        }
    }

    // =========================================================
    // GET USER PROFILE
    // =========================================================

    @Override
    public UserProfileResponse getUserProfile(UUID userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() ->
                        new RuntimeException("User not found")
                );

        return UserProfileResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .build();
    }

    // =========================================================
    // SEND EMAIL OTP
    // Does not create a user.
    // =========================================================

    @Override
    public void sendEmailOtp(String email) {

        String normalizedEmail = email.trim()
                .toLowerCase(java.util.Locale.ROOT);

        if (userRepository.existsByEmail(normalizedEmail)) {
            throw new EmailAlreadyExistsException(
                    "An account with this email already exists"
            );
        }

        gmailServices.sendOtpToAdmin(normalizedEmail);
    }

    // =========================================================
    // VERIFY EMAIL OTP
    // Does not create a user.
    // =========================================================

    @Override
    public boolean verifyEmailOtp(String email, String otp) {

        String normalizedEmail = email.trim()
                .toLowerCase(java.util.Locale.ROOT);

        if (userRepository.existsByEmail(normalizedEmail)) {
            return false;
        }

        return gmailServices.validateOtp(normalizedEmail, otp);
    }
}
