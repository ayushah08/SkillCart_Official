package com.skillcart_resumeservice.socialservice.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder
public class PostResponse {

    private UUID id;

    private UUID userId;

    private String userName;

    private String content;

    private String imageUrl;

    private long likeCount;

    private long commentCount;

    private boolean likedByMe;

    private LocalDateTime createdAt;
}