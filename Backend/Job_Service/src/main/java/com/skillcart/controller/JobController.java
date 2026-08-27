package com.skillcart.controller;

import com.skillcart.entity.service.SaveJobService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/jobs")
public class JobController {

    private final SaveJobService  saveJobService;

    public JobController(SaveJobService saveJobService) {
        this.saveJobService = saveJobService;
    }

    @PostMapping("/save/job")
    public String saveJob(Long jobId , Authentication authentication){

        return saveJobService.saveJobs(jobId , (UUID) authentication.getPrincipal());

    }
}
