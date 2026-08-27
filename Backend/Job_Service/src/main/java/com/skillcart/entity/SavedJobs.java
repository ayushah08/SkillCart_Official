package com.skillcart.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Entity
@Setter
@Getter
public class SavedJobs {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private List<Long>  jobId;

    private UUID userId;
}
