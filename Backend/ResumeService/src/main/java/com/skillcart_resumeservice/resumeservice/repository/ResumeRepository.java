package com.skillcart_resumeservice.resumeservice.repository;

import com.skillcart_resumeservice.resumeservice.entity.ResumeEntity;

import feign.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ResumeRepository
        extends JpaRepository<ResumeEntity, Long> {

  @Query("""
        SELECT r.id
        FROM ResumeEntity r
        WHERE r.userId = :userId
    """)
  Long findResumeIdByUserId(
          @Param("userId") UUID userId
  );}