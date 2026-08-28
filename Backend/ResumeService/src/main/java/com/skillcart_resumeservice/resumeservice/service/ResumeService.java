package com.skillcart_resumeservice.resumeservice.service;

import com.fasterxml.jackson.databind.ObjectMapper;
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
    private final ObjectMapper objectMapper;

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

        // IMPORTANT: Connect resume to logged-in user
        resumeEntity.setUserId(userId);

        resumeEntity.setFileName(fileName);
        resumeEntity.setFileType(fileType);
        resumeEntity.setFileData(file.getBytes());
        resumeEntity.setParsedJson(null);

        // Save resume and generate RID
        resumeEntity =
                resumeRepository.save(resumeEntity);

        logger.info(
                "Resume saved. RID: {}, UID: {}",
                resumeEntity.getId(),
                userId
        );

        // Call AI service
        String aiParsedData =
                aiResponse(resumeEntity);

        // Update parsed JSON
        resumeEntity.setParsedJson(aiParsedData);

        return resumeEntity.getId();
    }


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


    public ResumeEntity getResumeByUserId(
            UUID userId
    ) {

        logger.info(
                "Searching resume for UID: {}",
                userId
        );

        ResumeEntity resume =
                resumeRepository.findByUserId(userId);

        logger.info(
                "Resume search result: {}",
                resume != null
                        ? "RID = " + resume.getId()
                        : "NOT FOUND"
        );

        return resume;
    }
}