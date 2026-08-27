package com.skillcart.repository;

import com.skillcart.entity.SavedJobs;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface JobRepository
        extends JpaRepository<SavedJobs, UUID> {

    boolean existsByUserIdAndJobId(
            UUID userId,
            Long jobId
    );

    Optional<SavedJobs> findByUserIdAndJobId(
            UUID userId,
            Long jobId
    );

    List<SavedJobs> findByUserIdOrderBySavedAtDesc(
            UUID userId
    );

    void deleteByUserIdAndJobId(
            UUID userId,
            Long jobId
    );
}