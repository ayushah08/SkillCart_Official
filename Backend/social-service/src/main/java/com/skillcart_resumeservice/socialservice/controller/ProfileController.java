package com.skillcart_resumeservice.socialservice.controller;

import com.skillcart_resumeservice.socialservice.dto.UserProfileResponse;
import com.skillcart_resumeservice.socialservice.service.ProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/social/profiles")
@RequiredArgsConstructor
public class ProfileController {

    private final ProfileService profileService;


    @GetMapping("/{userId}")
    public UserProfileResponse getProfile(
            Authentication authentication,
            @PathVariable UUID userId
    ) {

        UUID currentUserId =
                (UUID) authentication.getPrincipal();

        return profileService.getUserProfile(
                userId,
                currentUserId
        );
    }
}