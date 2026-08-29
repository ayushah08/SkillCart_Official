package com.skillcart_resumeservice.socialservice.service;


import com.skillcart_resumeservice.socialservice.entity.Comment;
import com.skillcart_resumeservice.socialservice.repository.CommentRepository;
import com.skillcart_resumeservice.socialservice.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;

    public Comment addComment(
            UUID postId,
            UUID userId,
            String userName, String content
    ) {

        if (!postRepository.existsById(postId)) {

            throw new RuntimeException(
                    "Post not found"
            );
        }

        Comment comment =
                Comment.builder()
                        .postId(postId)
                        .userId(userId)
                        .userName(userName)
                        .content(content)
                        .build();

        return commentRepository.save(comment);
    }

    public Page<Comment> getComments(
            UUID postId,
            Pageable pageable
    ) {

        return commentRepository
                .findByPostIdOrderByCreatedAtAsc(
                        postId,
                        pageable
                );
    }

    public void deleteComment(
            UUID commentId,
            UUID userId
    ) {

        Comment comment =
                commentRepository.findById(
                        commentId
                ).orElseThrow(() ->
                        new RuntimeException(
                                "Comment not found"
                        )
                );

        if (
                !comment.getUserId()
                        .equals(userId)
        ) {

            throw new RuntimeException(
                    "You can only delete your own comment"
            );
        }

        commentRepository.delete(comment);
    }
}