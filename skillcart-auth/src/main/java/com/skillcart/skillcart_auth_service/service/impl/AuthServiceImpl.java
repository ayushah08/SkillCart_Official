package com.skillcart.skillcart_auth_service.service.impl;

import com.skillcart.skillcart_auth_service.dto.AuthResponse;
import com.skillcart.skillcart_auth_service.dto.RegisterRequest;
import com.skillcart.skillcart_auth_service.entity.User;
import com.skillcart.skillcart_auth_service.enums.Role;
import com.skillcart.skillcart_auth_service.exception.EmailAlreadyExistsException;
import com.skillcart.skillcart_auth_service.exception.UsernameAlreadyExistsException;
import com.skillcart.skillcart_auth_service.repository.UserRepository;
import com.skillcart.skillcart_auth_service.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public AuthResponse register(RegisterRequest registerRequest) {

        //emailValidation
        if (userRepository.existsByEmail(registerRequest.getEmail())) {
            throw new EmailAlreadyExistsException("User with email already exists");
        }

        //UserNAme
        if (userRepository.existsByUsernameme(registerRequest.getUsername())){

            throw new UsernameAlreadyExistsException("Username already exists");
        }



        //Mapping User from DTO toENTITY
        User user = User.builder().
                username(registerRequest.getUsername())
                .email(registerRequest.getEmail())
                .password(passwordEncoder.encode(registerRequest.getPassword()))
                .role(Role.USER).verified(false)
                .createdAt(LocalDateTime.now()).updatedAt(LocalDateTime.now())
                .build();


        //saving User
        userRepository.save(user);

        return new AuthResponse("User Registered Successfully");
    }
}
