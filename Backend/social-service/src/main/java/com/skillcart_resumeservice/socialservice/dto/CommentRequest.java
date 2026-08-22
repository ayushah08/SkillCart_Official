package com.skillcart_resumeservice.socialservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CommentRequest {

    @NotBlank
    @Size(max = 2000)
    private String content;
}