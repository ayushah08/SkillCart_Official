package com.skillcart_resumeservice.socialservice.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

@Getter
@Builder
public class UserProfileResponse {

    private UUID userId;

    private String username;

    private long postsCount;

    private long followersCount;

    private long followingCount;

    private boolean followingByMe;
}