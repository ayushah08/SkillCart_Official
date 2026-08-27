package com.skillcart_resumeservice.socialservice.service;

import com.cloudinary.Cloudinary;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CloudinaryService {

    private final Cloudinary cloudinary;

    public Map<?, ?> uploadFile(
            byte[] fileBytes,
            Map<?, ?> options
    ) throws IOException {

        return cloudinary
                .uploader()
                .upload(
                        fileBytes,
                        options
                );
    }
}