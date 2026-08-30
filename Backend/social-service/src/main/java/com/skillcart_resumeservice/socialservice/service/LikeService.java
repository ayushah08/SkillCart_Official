package com.skillcart_resumeservice.socialservice.service;

import com.skillcart_resumeservice.socialservice.entity.PostLike;
import com.skillcart_resumeservice.socialservice.repository.PostLikeRepository;
import com.skillcart_resumeservice.socialservice.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class LikeService {

    private final PostRepository postRepository;
    private final PostLikeRepository likeRepository;

    public void like(
            UUID postId,
            UUID userId,
            String userName
    ) {

        if (!postRepository.existsById(postId)) {

            throw new RuntimeException(
                    "Post not found"
            );
        }

        if (
                likeRepository
                        .existsByPostIdAndUserId(
                                postId,
                                userId
                        )
        ) {

            return;
        }

        likeRepository.save(
                PostLike.builder()
                        .postId(postId)
                        .userId(userId)
                        .userName(userName)
                        .build()
        );
    }

    public void unlike(
            UUID postId,
            UUID userId
    ) {

        likeRepository
                .deleteByPostIdAndUserId(
                        postId,
                        userId
                );
    }
}