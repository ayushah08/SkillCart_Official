package com.skillcart_resumeservice.socialservice.controller;

import com.skillcart_resumeservice.socialservice.dto.PostResponse;
import com.skillcart_resumeservice.socialservice.service.FeedService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/social/feed")
@RequiredArgsConstructor
public class FeedController {

    private final FeedService feedService;

    @GetMapping
    public Page<PostResponse> getFeed(
            Authentication authentication,

            @RequestParam(
                    defaultValue = "0"
            )
            int page,

            @RequestParam(
                    defaultValue = "20"
            )
            int size
    ) {

        UUID userId =
                (UUID) authentication.getPrincipal();

        return feedService.getFeed(
                userId,
                PageRequest.of(page, size)
        );
    }


    @GetMapping("/random")
    public Page<PostResponse> getRandomPosts(
            Authentication authentication,

            @RequestParam(
                    defaultValue = "0"
            )
            int page,

            @RequestParam(
                    defaultValue = "20"
            )
            int size
    ) {

        UUID userId =
                (UUID) authentication.getPrincipal();

        return feedService.getRandomPosts(
                userId,
                PageRequest.of(page, size)
        );
    }
}