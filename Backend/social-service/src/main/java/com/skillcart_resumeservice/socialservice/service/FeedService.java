package com.skillcart_resumeservice.socialservice.service;

import com.skillcart_resumeservice.socialservice.dto.PostResponse;
import com.skillcart_resumeservice.socialservice.entity.Follow;
import com.skillcart_resumeservice.socialservice.repository.FollowRepository;
import com.skillcart_resumeservice.socialservice.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FeedService {

    private final FollowRepository followRepository;
    private final PostRepository postRepository;
    private final PostService postService;

    public Page<PostResponse> getFeed(
            UUID userId,
            Pageable pageable
    ) {

        List<UUID> followingIds =
                followRepository
                        .findByFollowerId(userId)
                        .stream()
                        .map(Follow::getFollowingId)
                        .toList();

        /*
         * USER FOLLOWS NOBODY
         *
         * Show other users' posts
         */
        if (followingIds.isEmpty()) {

            return postRepository
                    .findByUserIdNotOrderByCreatedAtDesc(
                            userId,
                            pageable
                    )
                    .map(post ->
                            postService.getPost(
                                    post.getId(),
                                    userId
                            )
                    );
        }

        /*
         * USER FOLLOWS PEOPLE
         *
         * Show posts from followed users
         */
        return postRepository
                .findByUserIdInOrderByCreatedAtDesc(
                        followingIds,
                        pageable
                )
                .map(post ->
                        postService.getPost(
                                post.getId(),
                                userId
                        )
                );
    }

    public Page<PostResponse> getRandomPosts(
            UUID currentUserId,
            Pageable pageable
    ) {

        return postRepository
                .findByUserIdNotOrderByCreatedAtDesc(
                        currentUserId,
                        pageable
                )
                .map(post ->
                        postService.getPost(
                                post.getId(),
                                currentUserId
                        )
                );
    }
}