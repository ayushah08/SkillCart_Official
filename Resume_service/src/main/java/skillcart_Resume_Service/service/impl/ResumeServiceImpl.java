package skillcart_Resume_Service.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import skillcart_Resume_Service.dto.UploadResumeResponse;
import skillcart_Resume_Service.entity.Resume;
import skillcart_Resume_Service.entity.ResumeStatus;
import skillcart_Resume_Service.repository.ResumeRepository;
import skillcart_Resume_Service.service.CloudinaryService;
import skillcart_Resume_Service.service.ResumeService;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ResumeServiceImpl implements ResumeService {

    private final ResumeRepository repository;
    private final CloudinaryService cloudinaryService;

    @Override
    public UploadResumeResponse uploadResume(String userId,
                                             MultipartFile file) {

        String url = cloudinaryService.uploadResume(file);

        Resume resume = Resume.builder()
                .userId(userId)
                .resumeName(file.getOriginalFilename())
                .fileType(file.getContentType())   // <-- ADD THIS
                .resumeUrl(url)
                .status(ResumeStatus.UPLOADED)
                .uploadedAt(LocalDateTime.now())
                .build();

        Resume savedResume = repository.save(resume);

        return UploadResumeResponse.builder()
                .resumeId(savedResume.getResumeId())
                .resumeUrl(savedResume.getResumeUrl())
                .status(savedResume.getStatus().name())
                .build();

    }

    @Override
    public void saveAnalysis(Long resumeId, String extractedJson) {

        Resume resume = repository.findById(resumeId)
                .orElseThrow(() -> new RuntimeException("Resume Not Found"));

        resume.setStatus(ResumeStatus.COMPLETED);
        resume.setAiResponseJson(extractedJson);

        repository.save(resume);
    }

}