package skillcart_Resume_Service.service;

import org.springframework.web.multipart.MultipartFile;

public interface CloudinaryService {

    String uploadResume(MultipartFile file);

}