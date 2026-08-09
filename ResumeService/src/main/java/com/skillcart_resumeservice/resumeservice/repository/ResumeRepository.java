package com.skillcart_resumeservice.resumeservice.repository;

import com.skillcart_resumeservice.resumeservice.entity.ResumeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ResumeRepository extends JpaRepository<ResumeEntity, Integer> {


}
