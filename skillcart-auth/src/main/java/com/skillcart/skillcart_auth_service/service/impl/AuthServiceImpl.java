package com.skillcart.skillcart_auth_service.service.impl;

import com.skillcart.skillcart_auth_service.dto.AuthResponse;
import com.skillcart.skillcart_auth_service.dto.LoginRequest;
import com.skillcart.skillcart_auth_service.dto.RegisterRequest;
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

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private JwtService jwtService;

    @Override
    public AuthResponse register(RegisterRequest registerRequest) {

        //emailValidation
        if (userRepository.existsByEmail(registerRequest.getEmail())) {
            throw new EmailAlreadyExistsException("User with email already exists");
        }

        //UserNAme
        if (userRepository.existsByUsername(registerRequest.getUsername())) {

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
       User savedUser =  userRepository.save(user);

        String token = jwtService.generateJwtToken(savedUser);

        return new AuthResponse(token, "User Registered Successfully");
    }

    @Override
    public AuthResponse login(LoginRequest loginRequest) {

        Optional<User> user = userRepository.findByEmail(loginRequest.getEmail());

        if (user.isEmpty()) {
            throw new EmailNotFoundException(loginRequest.getEmail() + "Email Not found");
        }
        User user1 = user.get();
        if (!passwordEncoder.matches(loginRequest.getPassword(), user1.getPassword())) {
            throw new IncorrectPasswordException("Password is incorrect");
        }

        String token = jwtService.generateJwtToken((UserDetails) user1);

        return new AuthResponse(token, "Welcome Back :" + user1.getUsername() + "Login Succesful");
    }
}
