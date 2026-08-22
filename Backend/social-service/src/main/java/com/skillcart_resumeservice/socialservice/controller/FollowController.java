package com.skillcart_resumeservice.socialservice.controller;

import com.skillcart_resumeservice.socialservice.service.FollowService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/social/users")
@RequiredArgsConstructor
public class FollowController {

    private final FollowService followService;

    @PostMapping("/{userId}/follow")
    public void follow(
            Authentication authentication,
            @PathVariable UUID userId
    ) {

        UUID currentUser =
                (UUID) authentication.getPrincipal();

        followService.follow(
                currentUser,
                userId
        );
    }

    @DeleteMapping("/{userId}/follow")
    public void unfollow(
            Authentication authentication,
            @PathVariable UUID userId
    ) {

        UUID currentUser =
                (UUID) authentication.getPrincipal();

        followService.unfollow(
                currentUser,
                userId
        );
    }

    @GetMapping("/{userId}/followers/count")
    public long followersCount(
            @PathVariable UUID userId
    ) {

        return followService.followersCount(
                userId
        );
    }

    @GetMapping("/{userId}/following/count")
    public long followingCount(
            @PathVariable UUID userId
    ) {

        return followService.followingCount(
                userId
        );
    }
}