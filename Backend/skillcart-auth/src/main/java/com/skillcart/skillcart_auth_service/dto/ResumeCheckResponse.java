package com.skillcart.skillcart_auth_service.dto;

import lombok.Data;

@Data
public class ResumeCheckResponse {

    private Boolean hasResume;
    private Long resumeId;
}