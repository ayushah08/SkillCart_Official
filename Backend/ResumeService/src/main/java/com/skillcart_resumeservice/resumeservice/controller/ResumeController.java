package com.skillcart_resumeservice.resumeservice.controller;

import com.skillcart_resumeservice.resumeservice.service.ResumeService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/resume")
@CrossOrigin("http://localhost:5173")
public class ResumeController {

    private final ResumeService resumeService;


    public ResumeController(ResumeService resumeService) {
        this.resumeService = resumeService;
    }

    @PostMapping(value = "/upload" , consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public String uploadResume(@RequestParam("file") MultipartFile file) {
        try {
            resumeService.storeResume(file);
            return "File Uploaded Succesfully";

        } catch (IOException | InterruptedException e) {
            return "File Upload Failed";

        }


    }

//    @GetMapping("/download/{id}")
//    public ResponseEntity<byte[]> downloadResume(@PathVariable Long id) {
//      return   resumeService.getResume(id)
//                .map(resume -> {
//                    MediaType mediaType;
//                    try {
//
//                        mediaType = MediaType.parseMediaType(resume.getFileType());
//                    }
//                    catch (Exception e) {
//                        mediaType = MediaType.APPLICATION_OCTET_STREAM;
//                    }
//                    return ResponseEntity.ok()
//                            .contentType(mediaType)
//                            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resume.getFileName() + "\"")
//                            .body(resume.getFileData());
//                })
//
//                .orElseGet(()-> ResponseEntity.status(HttpStatus.NOT_FOUND).body(null));
//
//    }

    @PostMapping("/get/{$UserId}")
    public ResponseEntity<String> getUserId(@PathVariable("$UserId") UUID userId) {

        resumeService.setUserId(userId);
        
        return ResponseEntity.ok().body("User Id: " + userId + "received");

    }


}
