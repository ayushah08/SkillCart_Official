package com.skillcart.skillcart_auth_service.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginRequest {

    @Email(message = "Enter a Valid Email")
    @NotBlank(message = "Email cannot be blank")
    private String email;

    @NotBlank(message = "Please enter a password")
    private String password;
}
