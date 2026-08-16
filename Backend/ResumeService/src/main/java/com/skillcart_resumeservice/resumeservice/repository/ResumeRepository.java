package com.skillcart_resumeservice.resumeservice.repository;

import com.skillcart_resumeservice.resumeservice.entity.ResumeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ResumeRepository extends JpaRepository<ResumeEntity, Integer> {


    Optional<Object> findByUserId(UUID userId);
}
