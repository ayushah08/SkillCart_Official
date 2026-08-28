package com.skillcart_resumeservice.resumeservice.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.skillcart_resumeservice.resumeservice.entity.ResumeEntity;
import com.skillcart_resumeservice.resumeservice.repository.ResumeRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
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
    private final JwtService jwtService;

    Logger logger = LoggerFactory.getLogger(ResumeService.class);

    @Value("${service.download-base-url:http://localhost:8082}")
    private String downloadBaseUrl;

    @Transactional
    public Long storeResume(
            MultipartFile file,
            UUID userId
    )  throws IOException, InterruptedException {
        String fileName = file.getOriginalFilename();
        String fileType = (fileName != null && fileName.contains("."))
                ? fileName.substring(fileName.lastIndexOf("."))
                : "";

        ResumeEntity resumeEntity = new ResumeEntity();
        resumeEntity.setFileName(fileName);
        resumeEntity.setFileType(fileType);
        resumeEntity.setFileData(file.getBytes());
        resumeEntity.setParsedJson(null);
        resumeEntity.setUserId(userId);
        // 1. Save FIRST to generate the primary key ID
        resumeEntity = resumeRepository.save(resumeEntity);

        // 2. Call AI service with valid ID
        String aiParsedData = aiResponse(resumeEntity);

        UIDSetter(userId);
        // 3. Update parsed JSON
        resumeEntity.setParsedJson(aiParsedData);
        return resumeEntity.getId();
    }

  
    public String aiResponse(ResumeEntity resumeEntity) {

        //using this for sending the url to Ai service
//        String downloadUrl = String.format("%s/api/v1/resume/download/%d", downloadBaseUrl, resumeEntity.getId());

//        String jsonPayload = """
//          {
//            "url" : "%s"
//          }""".formatted(downloadUrl);


//        logger.error("An error occured in Ai Side");
        //Directly sending the main file data in bytes
        return restClient.post()
                .uri("https://skillcart-ai.onrender.com/api/v1/resume/parse") // <--- Absolute URI
                .contentType(MediaType.APPLICATION_JSON)
                .body(resumeEntity.getFileData())
                .retrieve()
                .body(String.class);
    }



    public String UIDSetter(UUID id) {

       ResumeEntity resumeEntity =resumeRepository.findByUserId(id);

       if(resumeEntity == null){
           resumeEntity = new ResumeEntity();
           resumeEntity.setUserId(id);
           return "User Id Set For Resume id  " + resumeEntity.getId();
       }else {
           return "User Id Already Exists for this  User with rid  " + resumeEntity.getId() ;
       }



    }


    public ResumeEntity getResumeByUserId(
            UUID userId
    ) {

        return resumeRepository.findByUserId(
                userId
        );
    }
}