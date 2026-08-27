package com.skillcart.repository;

import com.skillcart.entity.SavedJobs;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface JobRepository extends JpaRepository<SavedJobs,Long> {

    SavedJobs findByUserId(UUID userId);
}
