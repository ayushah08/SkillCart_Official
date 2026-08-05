package skillcart_Resume_Service.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import skillcart_Resume_Service.service.CloudinaryService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/test")
public class UploadController {

    private final CloudinaryService cloudinaryService;

    @PostMapping("/upload")
    public String upload(@RequestParam MultipartFile file){

        return cloudinaryService.uploadResume(file);

    }

}