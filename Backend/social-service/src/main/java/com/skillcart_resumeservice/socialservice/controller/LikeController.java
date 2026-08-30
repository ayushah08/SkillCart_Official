package com.skillcart_resumeservice.socialservice.controller;

import com.skillcart_resumeservice.socialservice.service.LikeService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/social/posts")
@RequiredArgsConstructor
public class LikeController {

    private final LikeService likeService;

    @PostMapping("/{postId}/like")
    public void like(
            Authentication authentication,
            @PathVariable UUID postId
    ) {

        UUID userId =
                (UUID) authentication.getPrincipal();

        String userName = (String) authentication.getDetails();

        likeService.like(
                postId,
                userId,
                userName
        );
    }

    @DeleteMapping("/{postId}/like")
    public void unlike(
            Authentication authentication,
            @PathVariable UUID postId
    ) {

        UUID userId =
                (UUID) authentication.getPrincipal();

        likeService.unlike(
                postId,
                userId
        );
    }
}