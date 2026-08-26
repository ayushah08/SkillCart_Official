package com.skillcart.skillcart_auth_service.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

@Getter
@AllArgsConstructor
public class AuthResponse {

    private String token;
    private  String message;

    private UUID Rid;
}
