package com.skillcart.skillcart_auth_service.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter
@Getter
@Builder
public class DetailResponse {

    private String username;

    private String email;

    private LocalDateTime created_at;
    private LocalDateTime updated_at;

}
