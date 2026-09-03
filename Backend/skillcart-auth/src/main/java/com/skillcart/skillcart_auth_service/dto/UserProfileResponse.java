package com.skillcart.skillcart_auth_service.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

@Getter
@Builder
public class UserProfileResponse {

    private UUID id;

    private String username;
}