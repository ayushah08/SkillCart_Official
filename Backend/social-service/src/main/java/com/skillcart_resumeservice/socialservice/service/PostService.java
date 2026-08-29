package com.skillcart_resumeservice.socialservice.service;


import com.cloudinary.utils.ObjectUtils;
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

import java.io.IOException;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final PostLikeRepository likeRepository;
    private final CommentRepository commentRepository;
    private final CloudinaryService cloudinaryService;


    public String uploadImage(
            MultipartFile file
    ) {

        if (
                file == null ||
                        file.isEmpty()
        ) {

            throw new IllegalArgumentException(
                    "Image cannot be empty"
            );
        }

        String contentType =
                file.getContentType();

        if (
                contentType == null ||
                        !contentType.startsWith("image/")
        ) {

            throw new IllegalArgumentException(
                    "Only image files are allowed"
            );
        }

        try {

            Map<?, ?> result =
                    cloudinaryService.uploadFile( file.getBytes(),
                            ObjectUtils.asMap(
                                    "folder",
                                    "skillcart/posts",
                                    "resource_type",
                                    "image"
                            ));



            return result
                    .get("secure_url")
                    .toString();

        } catch (IOException e) {

            throw new RuntimeException(
                    "Image upload failed",
                    e
            );
        }
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

                .userName(post.getUserName())

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

    public Post createPost(
            UUID userId,
            String userName ,
            String content,
            MultipartFile image
    ) {

        if (
                (content == null || content.trim().isEmpty())
                        && (image == null || image.isEmpty())
        ) {

            throw new IllegalArgumentException(
                    "Post content or image is required"
            );
        }

        String imageUrl = null;

        if (
                image != null &&
                        !image.isEmpty()
        ) {

            imageUrl = uploadImage(image);
        }

        Post post = new Post();

        post.setUserId(userId);
        post.setUserName(userName);

        post.setContent(
                content != null
                        ? content.trim()
                        : null
        );

        post.setImageUrl(imageUrl);

        return postRepository.save(post);
    }
}