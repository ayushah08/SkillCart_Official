package com.skillcart_resumeservice.socialservice.repository;

import com.skillcart_resumeservice.socialservice.entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface PostRepository
        extends JpaRepository<Post, UUID> {

    Page<Post> findByUserIdOrderByCreatedAtDesc(
            UUID userId,
            Pageable pageable
    );

    Page<Post> findByUserIdInOrderByCreatedAtDesc(
            Iterable<UUID> userIds,
            Pageable pageable
    );

    Page<Post> findByUserIdNotOrderByCreatedAtDesc(
            UUID userId,
            Pageable pageable
    );
}