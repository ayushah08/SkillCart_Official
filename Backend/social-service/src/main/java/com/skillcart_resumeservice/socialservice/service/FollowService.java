package com.skillcart_resumeservice.socialservice.service;

import com.skillcart_resumeservice.socialservice.entity.Follow;
import com.skillcart_resumeservice.socialservice.repository.FollowRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FollowService {

    private final FollowRepository followRepository;


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

        if (followRepository
                .existsByFollowerIdAndFollowingId(
                        followerId,
                        followingId
                )) {
            return;
        }

        followRepository.save(
                Follow.builder()
                        .followerId(followerId)
                        .followingId(followingId)
                        .build()
        );
    }


    // 🔥 IMPORTANT
    @Transactional
    public void unfollow(
            UUID followerId,
            UUID followingId
    ) {

        System.out.println("🔥 UNFOLLOW SERVICE CALLED");
        System.out.println("FOLLOWER: " + followerId);
        System.out.println("FOLLOWING: " + followingId);

        followRepository
                .deleteByFollowerIdAndFollowingId(
                        followerId,
                        followingId
                );

        System.out.println("🔥 UNFOLLOW DELETE COMPLETED");
    }


    public long followersCount(UUID userId) {
        return followRepository
                .countByFollowingId(userId);
    }


    public long followingCount(UUID userId) {
        return followRepository
                .countByFollowerId(userId);
    }


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


    public List<UUID> getFollowers(
            UUID userId
    ) {

        return followRepository
                .findByFollowingId(userId)
                .stream()
                .map(Follow::getFollowerId)
                .toList();
    }


    public List<UUID> getFollowing(
            UUID userId
    ) {

        return followRepository
                .findByFollowerId(userId)
                .stream()
                .map(Follow::getFollowingId)
                .toList();
    }
}