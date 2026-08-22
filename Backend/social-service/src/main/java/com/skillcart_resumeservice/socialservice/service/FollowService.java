package com.skillcart_resumeservice.socialservice.service;


import com.skillcart_resumeservice.socialservice.entity.Follow;
import com.skillcart_resumeservice.socialservice.repository.FollowRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FollowService {

    private final FollowRepository followRepository;

    public void follow(
            UUID followerId,
            UUID followingId
    ) {

        if (
                followerId.equals(followingId)
        ) {

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
}