package com.skillcart.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(
        name = "saved_jobs",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_saved_job_user",
                        columnNames = {
                                "user_id",
                                "job_id"
                        }
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SavedJobs {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(
            name = "user_id",
            nullable = false
    )
    private UUID userId;

    @Column(
            name = "job_id",
            nullable = false
    )
    private Long jobId;

    @Column(
            name = "saved_at",
            nullable = false
    )
    private LocalDateTime savedAt;

    @PrePersist
    public void prePersist() {

        savedAt = LocalDateTime.now();
    }
}