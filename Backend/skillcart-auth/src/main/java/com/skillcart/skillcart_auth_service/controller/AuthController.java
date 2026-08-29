package com.skillcart.skillcart_auth_service.controller;

import com.skillcart.skillcart_auth_service.dto.AuthResponse;
import com.skillcart.skillcart_auth_service.dto.LoginRequest;
import com.skillcart.skillcart_auth_service.dto.RegisterRequest;
import com.skillcart.skillcart_auth_service.service.AuthService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@CrossOrigin(origins = "http://localhost:5173")
@AllArgsConstructor
public class AuthController {

    private AuthService authService;

    @PostMapping("/register")
    public AuthResponse register(@Valid @RequestBody RegisterRequest registerRequest) {

        //   {
//            "username":"ayush",
//            "email":"ayushah@gmail.com",
//            "password":"password123"
//            }

        return authService.register(registerRequest);
    }

    @PostMapping("/login")
    public AuthResponse login(@Valid @RequestBody LoginRequest loginRequest) {


        System.out.println("================================");
        System.out.println("LOGIN CONTROLLER HIT");
        System.out.println("EMAIL: " + loginRequest.getEmail());
        System.out.println("================================");
        return authService.login(loginRequest);
    }





}
