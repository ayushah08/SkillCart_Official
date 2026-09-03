package com.skillcart_resumeservice.socialservice.repository;

import com.skillcart_resumeservice.socialservice.entity.PostLike;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface PostLikeRepository
        extends JpaRepository<PostLike, UUID> {

    boolean existsByPostIdAndUserId(
            UUID postId,
            UUID userId
    );


    void deleteByPostIdAndUserId(
            UUID postId,
            UUID userId
    );

    long countByPostId(UUID postId);
}