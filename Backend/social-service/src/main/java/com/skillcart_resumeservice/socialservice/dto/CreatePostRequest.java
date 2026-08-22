package com.skillcart_resumeservice.socialservice.dto;

import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreatePostRequest {

    @Size(max = 5000)
    private String content;
}