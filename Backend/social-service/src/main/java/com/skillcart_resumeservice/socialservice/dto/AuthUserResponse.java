package com.skillcart_resumeservice.socialservice.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class AuthUserResponse {

    private UUID id;
    private String username;
}