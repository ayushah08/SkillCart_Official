package com.skillcart.skillcart_auth_service.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@Builder
public class UserResponse {

    private UUID id;

    private String username;

    private String email;
}