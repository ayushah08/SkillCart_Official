package skillcart_Resume_Service.service;

import org.springframework.web.multipart.MultipartFile;
import skillcart_Resume_Service.dto.UploadResumeResponse;

public interface ResumeService {

    UploadResumeResponse uploadResume(String userId, MultipartFile file);

    void saveAnalysis(Long resumeId, String extractedJson);

}