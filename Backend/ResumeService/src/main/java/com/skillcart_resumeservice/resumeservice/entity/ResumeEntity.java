package com.skillcart_resumeservice.resumeservice.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Entity
@Getter
@Setter

public class ResumeEntity {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private UUID userId;

    private String fileName;
    private String fileType;

    @Lob
    private byte[] fileData;

    @Column(columnDefinition = "TEXT")
    private String parsedJson;
}
