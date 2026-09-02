package com.skillcart.service;

import com.skillcart.entity.SavedJobs;
import com.skillcart.repository.JobRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SaveJobService {

    private final JobRepository jobRepository;


    public String saveJob(
            Long jobId,
            UUID userId
    ) {

        if (
                jobRepository
                        .existsByUserIdAndJobId(
                                userId,
                                jobId
                        )
        ) {

            return "Job already saved";
        }

        SavedJobs savedJob =
                SavedJobs.builder()
                        .userId(userId)
                        .jobId(jobId)
                        .build();

        jobRepository.save(savedJob);

        return "Job saved successfully";
    }


    @Transactional
    public String unsaveJob(
            Long jobId,
            UUID userId
    ) {

        if (
                !jobRepository
                        .existsByUserIdAndJobId(
                                userId,
                                jobId
                        )
        ) {

            return "Job is not saved";
        }

        jobRepository.deleteByUserIdAndJobId(
                userId,
                jobId
        );

        return "Job removed from saved jobs";
    }


    public List<Long> getSavedJobIds(
            UUID userId
    ) {

        return jobRepository
                .findByUserIdOrderBySavedAtDesc(userId)
                .stream()
                .map(SavedJobs::getJobId)
                .toList();
    }


    public boolean isJobSaved(
            Long jobId,
            UUID userId
    ) {

        return jobRepository
                .existsByUserIdAndJobId(
                        userId,
                        jobId
                );
    }
}