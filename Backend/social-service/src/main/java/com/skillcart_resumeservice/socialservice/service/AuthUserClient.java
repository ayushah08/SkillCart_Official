package com.skillcart_resumeservice.socialservice.service;

import com.skillcart_resumeservice.socialservice.dto.AuthUserResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthUserClient {

    private final RestClient restClient;

    public AuthUserResponse getUserById(UUID userId) {

        AuthUserResponse user =
                restClient.get()
                        .uri(
                                "https://skillcart-auth.onrender.com/api/v1/auth/users/{userId}",
                                userId
                        )
                        .accept(MediaType.APPLICATION_JSON)
                        .retrieve()
                        .body(AuthUserResponse.class);

        if (user == null) {
            throw new RuntimeException("User not found");
        }

        return user;
    }
}