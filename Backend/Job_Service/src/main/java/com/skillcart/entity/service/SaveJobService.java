package com.skillcart.entity.service;


import com.skillcart.entity.SavedJobs;
import com.skillcart.repository.JobRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
public class SaveJobService {

    private final JobRepository jobRepository;

    public String saveJobs(Long jobId , UUID userId){

       SavedJobs savedJobs =  jobRepository.findByUserId(userId);

       savedJobs.setJobId(Collections.singletonList(jobId));

       jobRepository.save(savedJobs);

       return "Job Saved Successfully";

    }

    public List<Long> getJobs (UUID UserId){

        return new ArrayList<>(Collections.singleton(jobRepository.findByUserId(UserId))).get(0).getJobId();
    }
}
