package com.skillcart.controller;

import com.skillcart.service.SaveJobService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/jobs")
@RequiredArgsConstructor
public class JobController {

    private final SaveJobService saveJobService;


    // ===============================
    // SAVE JOB
    // ===============================

    @PostMapping("/save/{jobId}")
    public String saveJob(
            @PathVariable Long jobId,
            Authentication authentication
    ) {

        UUID userId =
                (UUID) authentication.getPrincipal();

        return saveJobService.saveJob(
                jobId,
                userId
        );
    }


    // ===============================
    // UNSAVE JOB
    // ===============================

    @DeleteMapping("/save/{jobId}")
    public String unsaveJob(
            @PathVariable Long jobId,
            Authentication authentication
    ) {

        UUID userId =
                (UUID) authentication.getPrincipal();

        return saveJobService.unsaveJob(
                jobId,
                userId
        );
    }


    // ===============================
    // GET SAVED JOB IDS
    // ===============================

    @GetMapping("/saved")
    public List<Long> getSavedJobs(
            Authentication authentication
    ) {

        UUID userId =
                (UUID) authentication.getPrincipal();

        return saveJobService.getSavedJobIds(
                userId
        );
    }


    // ===============================
    // CHECK SAVED STATUS
    // ===============================

    @GetMapping("/saved/{jobId}")
    public boolean isJobSaved(
            @PathVariable Long jobId,
            Authentication authentication
    ) {

        UUID userId =
                (UUID) authentication.getPrincipal();

        return saveJobService.isJobSaved(
                jobId,
                userId
        );
    }
}