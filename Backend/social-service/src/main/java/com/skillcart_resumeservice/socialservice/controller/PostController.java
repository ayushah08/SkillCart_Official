package com.skillcart_resumeservice.socialservice.controller;

import com.skillcart_resumeservice.socialservice.dto.PostResponse;
import com.skillcart_resumeservice.socialservice.service.PostService;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@RestController
@RequestMapping("/api/social/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    @PostMapping(
            consumes = "multipart/form-data"
    )
    public PostResponse createPost(
            Authentication authentication,

            @RequestPart(
                    required = false
            )
            @Size(max = 5000)
            String content,

            @RequestPart(
                    required = false
            )
            MultipartFile image
    ) {

        UUID userId =
                (UUID) authentication.getPrincipal();

        return postService.createPost(
                userId,
                content,
                image
        );
    }

    @GetMapping("/{postId}")
    public PostResponse getPost(
            Authentication authentication,
            @PathVariable UUID postId
    ) {

        UUID userId =
                (UUID) authentication.getPrincipal();

        return postService.getPost(
                postId,
                userId
        );
    }

    @GetMapping("/user/{userId}")
    public Page<PostResponse> getUserPosts(
            Authentication authentication,

            @PathVariable UUID userId,

            @RequestParam(
                    defaultValue = "0"
            )
            int page,

            @RequestParam(
                    defaultValue = "10"
            )
            int size
    ) {

        UUID currentUserId =
                (UUID) authentication.getPrincipal();

        Pageable pageable =
                PageRequest.of(
                        page,
                        size
                );

        return postService.getUserPosts(
                userId,
                currentUserId,
                pageable
        );
    }

    @DeleteMapping("/{postId}")
    public void deletePost(
            Authentication authentication,
            @PathVariable UUID postId
    ) {

        UUID userId =
                (UUID) authentication.getPrincipal();

        postService.deletePost(
                postId,
                userId
        );
    }
}