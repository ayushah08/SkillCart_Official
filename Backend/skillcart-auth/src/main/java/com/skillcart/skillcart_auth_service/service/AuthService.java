package com.skillcart.skillcart_auth_service.service;


import com.skillcart.skillcart_auth_service.dto.AuthResponse;
import com.skillcart.skillcart_auth_service.dto.LoginRequest;
import com.skillcart.skillcart_auth_service.dto.RegisterRequest;
import com.skillcart.skillcart_auth_service.dto.UserProfileResponse;
import com.skillcart.skillcart_auth_service.entity.User;

import java.util.UUID;

public interface AuthService {

    AuthResponse register(RegisterRequest registerRequest);

    AuthResponse login(LoginRequest loginRequest);


//    UserProfileResponse getUserProfile(String username);

    UserProfileResponse getUserProfile(UUID userId);
}
