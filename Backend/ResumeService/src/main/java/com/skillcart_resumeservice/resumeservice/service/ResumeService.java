package com.skillcart_resumeservice.resumeservice.service;

import com.skillcart_resumeservice.resumeservice.entity.ResumeEntity;
import com.skillcart_resumeservice.resumeservice.repository.ResumeRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClient;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ResumeService {

    private final RestClient restClient;
    private final ResumeRepository resumeRepository;

    private final Logger logger =
            LoggerFactory.getLogger(ResumeService.class);


    @Transactional
    public Long storeResume(
            MultipartFile file,
            UUID userId
    ) throws IOException {

        String fileName =
                file.getOriginalFilename();

        String fileType =
                (fileName != null && fileName.contains("."))
                        ? fileName.substring(
                        fileName.lastIndexOf(".")
                )
                        : "";

        // ==============================
        // CREATE RESUME OBJECT
        // ==============================

        ResumeEntity resumeEntity =
                new ResumeEntity();

        resumeEntity.setUserId(userId);
        resumeEntity.setFileName(fileName);
        resumeEntity.setFileType(fileType);
        resumeEntity.setFileData(file.getBytes());


        // ==============================
        // CALL AI SERVICE FIRST
        // ==============================

        logger.info(
                "Calling AI service for UID: {}",
                userId
        );

        String aiParsedData =
                aiResponse(resumeEntity);


        // ==============================
        // VALIDATE AI RESPONSE
        // ==============================

        if (aiParsedData == null ||
                aiParsedData.isBlank()) {

            throw new RuntimeException(
                    "AI service did not return parsed resume data"
            );
        }


        // ==============================
        // SET PARSED JSON
        // ==============================

        resumeEntity.setParsedJson(
                aiParsedData
        );


        // ==============================
        // SAVE ONLY AFTER AI SUCCESS
        // ==============================

        resumeEntity =
                resumeRepository.save(
                        resumeEntity
                );


        logger.info(
                "Resume and AI data saved. RID: {}, UID: {}",
                resumeEntity.getId(),
                userId
        );


        return resumeEntity.getId();
    }


    // ==============================
    // AI SERVICE
    // ==============================

    public String aiResponse(
            ResumeEntity resumeEntity
    ) {

        return restClient.post()
                .uri(
                        "https://skillcart-ai.onrender.com/api/v1/resume/parse"
                )
                .contentType(
                        MediaType.APPLICATION_JSON
                )
                .body(
                        resumeEntity.getFileData()
                )
                .retrieve()
                .body(String.class);
    }


    // ==============================
    // GET RESUME ID BY USER
    // ==============================

    @Transactional(readOnly = true)
    public Long getResumeByUserId(
            UUID userId
    ) {

        return resumeRepository
                .findResumeIdByUserId(userId);
    }
}