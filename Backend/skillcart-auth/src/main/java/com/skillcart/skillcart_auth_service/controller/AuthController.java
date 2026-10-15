package com.skillcart.skillcart_auth_service.controller;

import com.skillcart.skillcart_auth_service.dto.AuthResponse;
import com.skillcart.skillcart_auth_service.dto.EmailOtpRequest;
import com.skillcart.skillcart_auth_service.dto.LoginRequest;
import com.skillcart.skillcart_auth_service.dto.RegisterRequest;
import com.skillcart.skillcart_auth_service.dto.UserProfileResponse;
import com.skillcart.skillcart_auth_service.dto.VerifyOtpRequest;
import com.skillcart.skillcart_auth_service.service.AuthService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/send/otp")
    public Map<String, Object> sendOtp(
            @Valid @RequestBody EmailOtpRequest request
    ) {
        authService.sendEmailOtp(request.email());

        return Map.of(
                "success", true,
                "message", "OTP sent successfully. Please check your email."
        );
    }

    @PostMapping("/verify-otp")
    public boolean verifyOtp(
            @Valid @RequestBody VerifyOtpRequest request
    ) {
        return authService.verifyEmailOtp(
                request.email(),
                request.otp()
        );
    }

    @PostMapping("/register")
    public AuthResponse register(
            @Valid @RequestBody RegisterRequest registerRequest
    ) {
        return authService.register(registerRequest);
    }

    @PostMapping("/login")
    public AuthResponse login(
            @Valid @RequestBody LoginRequest loginRequest
    ) {
        return authService.login(loginRequest);
    }

    @GetMapping("/users/{userId}")
    public UserProfileResponse getUserProfile(
            @PathVariable UUID userId
    ) {
        return authService.getUserProfile(userId);
    }
}
