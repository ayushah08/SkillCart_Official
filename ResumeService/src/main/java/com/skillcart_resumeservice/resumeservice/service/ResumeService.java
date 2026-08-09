package com.skillcart_resumeservice.resumeservice.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.skillcart_resumeservice.resumeservice.entity.ResumeEntity;
import com.skillcart_resumeservice.resumeservice.repository.ResumeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClient;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ResumeService {

    private final RestClient restClient;
    private final ResumeRepository resumeRepository;
    private final ObjectMapper objectMapper;

    @Value("${service.download-base-url:http://localhost:8082}")
    private String downloadBaseUrl;

    @Transactional
    public ResumeEntity storeResume(MultipartFile file) throws IOException, InterruptedException {
        String fileName = file.getOriginalFilename();
        String fileType = (fileName != null && fileName.contains("."))
                ? fileName.substring(fileName.lastIndexOf("."))
                : "";

        ResumeEntity resumeEntity = new ResumeEntity();
        resumeEntity.setFileName(fileName);
        resumeEntity.setFileType(fileType);
        resumeEntity.setFileData(file.getBytes());
        resumeEntity.setParsedJson(null);

        // 1. Save FIRST to generate the primary key ID
        resumeEntity = resumeRepository.save(resumeEntity);

        // 2. Call AI service with valid ID
        String aiParsedData = aiResponse(resumeEntity);

        // 3. Update parsed JSON
        resumeEntity.setParsedJson(aiParsedData);
        return resumeRepository.save(resumeEntity);
    }

    @Transactional(readOnly = true)
    public Optional<ResumeEntity> getResume(Long id) {
        return resumeRepository.findById(Math.toIntExact(id));
    }
    public String aiResponse(ResumeEntity resumeEntity) {
        String downloadUrl = String.format("%s/api/v1/resume/download/%d", downloadBaseUrl, resumeEntity.getId());

        String jsonPayload = """
          {
            "url" : "%s"
          }""".formatted(downloadUrl);

        return restClient.post()
                .uri("http://10.62.224.115:8001/api/v1/resume/parse") // <--- Absolute URI
                .contentType(MediaType.APPLICATION_JSON)
                .body(jsonPayload)
                .retrieve()
                .body(String.class);
    }
}