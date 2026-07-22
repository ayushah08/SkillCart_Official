package com.skillcart.skillcart_auth_service.controller;

import com.skillcart.skillcart_auth_service.dto.AuthResponse;
import com.skillcart.skillcart_auth_service.dto.RegisterRequest;
import com.skillcart.skillcart_auth_service.service.AuthService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@AllArgsConstructor
public class AuthController {

    private AuthService authService;

    @PostMapping("/register")
    private AuthResponse register(@Valid @RequestBody RegisterRequest registerRequest) {

        //   {
//    "username":"ayush",
//            "email":"ayushah@gmail.com",
//            "password":"password123"
//}

        return authService.register(registerRequest);
    }





}
