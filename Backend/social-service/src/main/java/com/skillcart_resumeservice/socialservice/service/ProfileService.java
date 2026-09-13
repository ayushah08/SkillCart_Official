package com.skillcart_resumeservice.socialservice.service;

import com.skillcart_resumeservice.socialservice.dto.AuthUserResponse;
import com.skillcart_resumeservice.socialservice.dto.UserProfileResponse;
import com.skillcart_resumeservice.socialservice.repository.FollowRepository;
import com.skillcart_resumeservice.socialservice.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProfileService {

    private final FollowRepository followRepository;
    private final PostRepository postRepository;
    private final AuthUserClient authUserClient;


    public UserProfileResponse getUserProfile(
            UUID profileUserId,
            UUID currentUserId
    ) {

        // ==============================
        // GET USER FROM AUTH SERVICE
        // ==============================

        AuthUserResponse authUser =
                authUserClient.getUserById(
                        profileUserId
                );


        // ==============================
        // SOCIAL COUNTS
        // ==============================

        long postsCount =
                postRepository.countByUserId(
                        profileUserId
                );

        long followersCount =
                followRepository.countByFollowingId(
                        profileUserId
                );

        long followingCount =
                followRepository.countByFollowerId(
                        profileUserId
                );


        // ==============================
        // FOLLOW STATUS
        // ==============================

        boolean followingByMe =
                !profileUserId.equals(currentUserId)
                        && followRepository
                        .existsByFollowerIdAndFollowingId(
                                currentUserId,
                                profileUserId
                        );


        // ==============================
        // RESPONSE
        // ==============================

        return UserProfileResponse.builder()
                .userId(authUser.getId())
                .username(authUser.getUsername())
                .postsCount(postsCount)
                .followersCount(followersCount)
                .followingCount(followingCount)
                .followingByMe(followingByMe)
                .build();
    }
    public UserProfileResponse searchUserProfile(
            String username,
            UUID currentUserId
    ) {

        // ==============================
        // GET USER FROM AUTH SERVICE
        // ==============================

        AuthUserResponse authUser =
                authUserClient.getUserByName(
                        username
                );


        // ==============================
        // SOCIAL COUNTS
        // ==============================

        long postsCount =
                postRepository.countByUserId(
                        authUser.getId()
                );

        long followersCount =
                followRepository.countByFollowingId(
                        authUser.getId()
                );

        long followingCount =
                followRepository.countByFollowerId(
                        authUser.getId()
                );


        // ==============================
        // FOLLOW STATUS
        // ==============================

        boolean followingByMe =
                !authUser.getId().equals(currentUserId)
                        && followRepository
                        .existsByFollowerIdAndFollowingId(
                                currentUserId,
                                authUser.getId()
                        );


        // ==============================
        // RESPONSE
        // ==============================

        return UserProfileResponse.builder()
                .userId(authUser.getId())
                .username(authUser.getUsername())
                .postsCount(postsCount)
                .followersCount(followersCount)
                .followingCount(followingCount)
                .followingByMe(followingByMe)
                .build();
    }
}

