package com.skillcart_resumeservice.socialservice.service;


import com.skillcart_resumeservice.socialservice.dto.PostResponse;
import com.skillcart_resumeservice.socialservice.entity.Post;
import com.skillcart_resumeservice.socialservice.repository.CommentRepository;
import com.skillcart_resumeservice.socialservice.repository.PostLikeRepository;
import com.skillcart_resumeservice.socialservice.repository.PostRepository;
import com.skillcart_resumeservice.socialservice.service.CloudinaryService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final PostLikeRepository likeRepository;
    private final CommentRepository commentRepository;
    private final CloudinaryService cloudinaryService;

    public PostResponse createPost(
            UUID userId,
            String content,
            MultipartFile image
    ) {

        String imageUrl = null;

        if (
                image != null &&
                        !image.isEmpty()
        ) {

            imageUrl =
                    cloudinaryService.uploadImage(image);
        }

        Post post = Post.builder()
                .userId(userId)
                .content(content)
                .imageUrl(imageUrl)
                .build();

        post = postRepository.save(post);

        return toResponse(
                post,
                userId
        );
    }

    public PostResponse getPost(
            UUID postId,
            UUID currentUserId
    ) {

        Post post =
                postRepository.findById(postId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Post not found"
                                )
                        );

        return toResponse(
                post,
                currentUserId
        );
    }

    public Page<PostResponse> getUserPosts(
            UUID userId,
            UUID currentUserId,
            Pageable pageable
    ) {

        return postRepository
                .findByUserIdOrderByCreatedAtDesc(
                        userId,
                        pageable
                )
                .map(post ->
                        toResponse(
                                post,
                                currentUserId
                        )
                );
    }

    public void deletePost(
            UUID postId,
            UUID userId
    ) {

        Post post =
                postRepository.findById(postId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Post not found"
                                )
                        );

        if (
                !post.getUserId()
                        .equals(userId)
        ) {

            throw new RuntimeException(
                    "You can only delete your own post"
            );
        }

        postRepository.delete(post);
    }

    private PostResponse toResponse(
            Post post,
            UUID currentUserId
    ) {

        return PostResponse.builder()

                .id(post.getId())

                .userId(post.getUserId())

                .content(post.getContent())

                .imageUrl(post.getImageUrl())

                .likeCount(
                        likeRepository
                                .countByPostId(
                                        post.getId()
                                )
                )

                .commentCount(
                        commentRepository
                                .countByPostId(
                                        post.getId()
                                )
                )

                .likedByMe(
                        likeRepository
                                .existsByPostIdAndUserId(
                                        post.getId(),
                                        currentUserId
                                )
                )

                .createdAt(
                        post.getCreatedAt()
                )

                .build();
    }
}