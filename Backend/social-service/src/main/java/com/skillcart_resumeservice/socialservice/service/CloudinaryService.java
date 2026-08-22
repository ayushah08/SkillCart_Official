package com.skillcart_resumeservice.socialservice.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CloudinaryService {

    private final Cloudinary cloudinary;

    public String uploadImage(
            MultipartFile file
    ) {

        if (
                file == null ||
                        file.isEmpty()
        ) {

            throw new IllegalArgumentException(
                    "Image cannot be empty"
            );
        }

        try {

            Map<?, ?> result =
                    cloudinary.uploader().upload(
                            file.getBytes(),
                            ObjectUtils.asMap(
                                    "folder",
                                    "skillcart/posts",
                                    "resource_type",
                                    "image"
                            )
                    );

            return result
                    .get("secure_url")
                    .toString();

        } catch (IOException e) {

            throw new RuntimeException(
                    "Image upload failed",
                    e
            );
        }
    }
}