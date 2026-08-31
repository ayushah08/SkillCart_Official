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

        ResumeEntity resumeEntity =
                new ResumeEntity();

        resumeEntity.setUserId(userId);
        resumeEntity.setFileName(fileName);
        resumeEntity.setFileType(fileType);
        resumeEntity.setFileData(file.getBytes());
        resumeEntity.setParsedJson(null);


        // ==============================
        // SAVE RESUME
        // ==============================

        resumeEntity =
                resumeRepository.save(resumeEntity);

        logger.info(
                "Resume saved. RID: {}, UID: {}",
                resumeEntity.getId(),
                userId
        );


        // ==============================
        // CALL AI SERVICE
        // ==============================

        try {

            logger.info(
                    "Calling AI service for RID: {}",
                    resumeEntity.getId()
            );

            String aiParsedData =
                    aiResponse(resumeEntity);

            if (aiParsedData != null) {

                resumeEntity.setParsedJson(
                        aiParsedData
                );

                resumeRepository.save(
                        resumeEntity
                );

                logger.info(
                        "AI parsed data saved for RID: {}",
                        resumeEntity.getId()
                );

            }

        } catch (Exception e) {

            logger.error(
                    "AI service failed for RID: {}. Resume was still uploaded.",
                    resumeEntity.getId(),
                    e
            );

        }


        return resumeEntity.getId();
    }


    public String aiResponse(
            ResumeEntity resumeEntity
    ) {

        return restClient.post()
                .uri(
                        "https://skillcart-ai.fastapicloud.dev/api/v1/resume/parse"
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


    @Transactional(readOnly = true)
    public Long getResumeByUserId(
            UUID userId
    ) {

        Long resumeId =
                resumeRepository
                        .findResumeIdByUserId(userId);

        return resumeId;
    }
}