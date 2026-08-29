package com.skillcart_resumeservice.resumeservice.controller;

import com.skillcart_resumeservice.resumeservice.entity.ResumeEntity;
import com.skillcart_resumeservice.resumeservice.service.ResumeCheckResponse;
import com.skillcart_resumeservice.resumeservice.service.ResumeService;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/resume")
@CrossOrigin("http://localhost:5173")
public class ResumeController {

    private final ResumeService resumeService;


    public ResumeController(ResumeService resumeService) {
        this.resumeService = resumeService;
    }

    @PostMapping(
            value = "/upload",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<?> uploadResume(
            @RequestParam("file") MultipartFile file,
            Authentication authentication
    ) throws IOException, InterruptedException {


        UUID userId = (UUID) authentication.getPrincipal();
        try {
            Long resumeId = resumeService.storeResume(
                    file,
                    userId

            );


        return ResponseEntity.ok(
                    Map.of(
                            "success", true,
                            "resumeId", resumeId
                    )
            );


        } catch (Exception e) {

            return ResponseEntity
                    .internalServerError()
                    .body(
                            Map.of(
                                    "success", false,
                                    "message",
                                    "Resume upload failed"
                            )
                    );
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

//
//    @GetMapping("/me")
//    public ResponseEntity<?> getMyResume(
//            Authentication authentication
//    ) {
//
//        UUID userId =
//                (UUID) authentication.getPrincipal();
//
//
//        Long resume =
//                resumeService.getResumeByUserId(userId);
//
//
//        if (resume == null) {
//
//            return ResponseEntity.ok(
//                    Map.of(
//                            "hasResume", false,
//                            "resumeId", (Object) null
//                    )
//            );
//        }
//
//
//        return ResponseEntity.ok(
//                Map.of(
//                        "hasResume", true,
//                        "resumeId", resume.getId()
//                )
//        );
//    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<ResumeCheckResponse> getResumeByUserId(
            @PathVariable UUID userId
    ) {

        System.out.println(
                "🔥 USER RESUME CONTROLLER REACHED: " + userId
        );
        Long resumeId =
                resumeService.getResumeByUserId(userId);

        if (resumeId == null) {

            return ResponseEntity.ok(
                    new ResumeCheckResponse(
                            false,
                            null
                    )
            );
        }

        return ResponseEntity.ok(
                new ResumeCheckResponse(
                        true,
                        resumeId
                )
        );
    }




    @GetMapping("/test-public")
    public ResponseEntity<String> testPublic() {

        return ResponseEntity.ok(
                "RESUME SERVICE IS WORKING"
        );
    }

}

