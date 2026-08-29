package com.skillcart_resumeservice.socialservice.controller;

import com.skillcart_resumeservice.socialservice.service.FollowService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
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

        System.out.println("================================");
        System.out.println("🔥 UNFOLLOW CONTROLLER REACHED");
        System.out.println("AUTHENTICATION: " + authentication);
        System.out.println("TARGET USER ID: " + userId);
        System.out.println("================================");

        UUID currentUser =
                (UUID) authentication.getPrincipal();

        System.out.println(
                "CURRENT USER ID: " + currentUser
        );

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


    @GetMapping("/{userId}/followers")
    public List<UUID> getFollowers(
            @PathVariable UUID userId
    ) {

        return followService.getFollowers(
                userId
        );
    }


    @GetMapping("/{userId}/following")
    public List<UUID> getFollowing(
            @PathVariable UUID userId
    ) {

        return followService.getFollowing(
                userId
        );
    }


    @GetMapping("/{userId}/following-status")
    public boolean followingStatus(
            Authentication authentication,
            @PathVariable UUID userId
    ) {

        UUID currentUser =
                (UUID) authentication.getPrincipal();

        return followService.isFollowing(
                currentUser,
                userId
        );
    }
}