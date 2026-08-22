package com.skillcart_resumeservice.socialservice.controller;

import com.skillcart_resumeservice.socialservice.dto.CommentRequest;
import com.skillcart_resumeservice.socialservice.entity.Comment;
import com.skillcart_resumeservice.socialservice.service.CommentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/social/comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @PostMapping("/post/{postId}")
    public Comment addComment(
            Authentication authentication,

            @PathVariable UUID postId,

            @Valid @RequestBody
            CommentRequest request
    ) {

        UUID userId =
                (UUID) authentication.getPrincipal();

        return commentService.addComment(
                postId,
                userId,
                request.getContent()
        );
    }

    @GetMapping("/post/{postId}")
    public Page<Comment> getComments(
            @PathVariable UUID postId,

            @RequestParam(
                    defaultValue = "0"
            )
            int page,

            @RequestParam(
                    defaultValue = "20"
            )
            int size
    ) {

        return commentService.getComments(
                postId,
                PageRequest.of(page, size)
        );
    }

    @DeleteMapping("/{commentId}")
    public void deleteComment(
            Authentication authentication,

            @PathVariable UUID commentId
    ) {

        UUID userId =
                (UUID) authentication.getPrincipal();

        commentService.deleteComment(
                commentId,
                userId
        );
    }
}