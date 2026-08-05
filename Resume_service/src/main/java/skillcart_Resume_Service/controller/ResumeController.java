package skillcart_Resume_Service.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import skillcart_Resume_Service.dto.AnalysisRequest;
import skillcart_Resume_Service.dto.UploadResumeResponse;
import skillcart_Resume_Service.service.ResumeService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/resume")
public class ResumeController {

    private final ResumeService resumeService;

    @PostMapping(value = "/upload",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public UploadResumeResponse upload(

            @RequestParam("userId") String userId,
            @RequestParam("file") MultipartFile file){

        return resumeService.uploadResume(userId,file);

    }

    @PutMapping("/{resumeId}/analysis")
    public ResponseEntity<String> saveAnalysis(
            @PathVariable Long resumeId,
            @RequestBody AnalysisRequest request){

        resumeService.saveAnalysis(
                resumeId,
                request.getExtractedJson()
        );

        return ResponseEntity.ok("Analysis Saved Successfully");
    }

}