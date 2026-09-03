package com.skillcart_resumeservice.socialservice.service;

import com.skillcart_resumeservice.socialservice.dto.AuthUserResponse;
import com.skillcart_resumeservice.socialservice.dto.UserSummaryResponse;
import com.skillcart_resumeservice.socialservice.entity.Follow;
import com.skillcart_resumeservice.socialservice.repository.FollowRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FollowService {

    private final FollowRepository followRepository;
    private final RestClient restClient;


    // ==========================================
    // FOLLOW
    // ==========================================

    @Transactional
    public void follow(
            UUID followerId,
            UUID followingId
    ) {

        if (followerId.equals(followingId)) {

            throw new RuntimeException(
                    "You cannot follow yourself"
            );
        }

        if (
                followRepository
                        .existsByFollowerIdAndFollowingId(
                                followerId,
                                followingId
                        )
        ) {

            return;
        }

        followRepository.save(
                Follow.builder()
                        .followerId(followerId)
                        .followingId(followingId)
                        .build()
        );
    }


    // ==========================================
    // UNFOLLOW
    // ==========================================

    @Transactional
    public void unfollow(
            UUID followerId,
            UUID followingId
    ) {

        followRepository
                .deleteByFollowerIdAndFollowingId(
                        followerId,
                        followingId
                );
    }


    // ==========================================
    // COUNTS
    // ==========================================

    public long followersCount(
            UUID userId
    ) {

        return followRepository
                .countByFollowingId(userId);
    }


    public long followingCount(
            UUID userId
    ) {

        return followRepository
                .countByFollowerId(userId);
    }


    // ==========================================
    // FOLLOWING STATUS
    // ==========================================

    public boolean isFollowing(
            UUID followerId,
            UUID followingId
    ) {

        return followRepository
                .existsByFollowerIdAndFollowingId(
                        followerId,
                        followingId
                );
    }


    // ==========================================
    // GET FOLLOWERS
    // ==========================================

    public List<UserSummaryResponse> getFollowers(
            UUID userId,
            UUID currentUserId
    ) {

        List<UUID> followerIds =
                followRepository
                        .findByFollowingId(userId)
                        .stream()
                        .map(
                                Follow::getFollowerId
                        )
                        .toList();


        return followerIds
                .stream()
                .map(
                        followerId ->
                                buildUserSummary(
                                        followerId,
                                        currentUserId
                                )
                )
                .toList();
    }


    // ==========================================
    // GET FOLLOWING
    // ==========================================

    public List<UserSummaryResponse> getFollowing(
            UUID userId,
            UUID currentUserId
    ) {

        List<UUID> followingIds =
                followRepository
                        .findByFollowerId(userId)
                        .stream()
                        .map(
                                Follow::getFollowingId
                        )
                        .toList();


        return followingIds
                .stream()
                .map(
                        followingId ->
                                buildUserSummary(
                                        followingId,
                                        currentUserId
                                )
                )
                .toList();
    }


    // ==========================================
    // BUILD USER SUMMARY
    // ==========================================

    private UserSummaryResponse buildUserSummary(
            UUID userId,
            UUID currentUserId
    ) {

        AuthUserResponse authUser =
                restClient
                        .get()
                        .uri(
                                "https://skillcart-auth.onrender.com/api/v1/auth/users/{userId}",
                                userId
                        )
                        .accept(
                                MediaType.APPLICATION_JSON
                        )
                        .retrieve()
                        .body(
                                AuthUserResponse.class
                        );


        if (authUser == null) {

            throw new RuntimeException(
                    "User not found"
            );
        }


        boolean followedByMe =

                currentUserId != null

                        && !currentUserId.equals(
                        userId
                )

                        && followRepository
                        .existsByFollowerIdAndFollowingId(
                                currentUserId,
                                userId
                        );


        return UserSummaryResponse
                .builder()

                .userId(
                        authUser.getId()
                )

                .username(
                        authUser.getUsername()
                )

                .followedByMe(
                        followedByMe
                )

                .build();
    }
}