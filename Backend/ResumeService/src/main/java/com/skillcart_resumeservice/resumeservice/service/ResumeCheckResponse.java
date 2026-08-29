package com.skillcart_resumeservice.resumeservice.service;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResumeCheckResponse {

    private Boolean hasResume;
    private Long resumeId;
}