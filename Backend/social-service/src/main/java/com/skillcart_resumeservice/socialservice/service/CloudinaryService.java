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

        System.out.println("================================");
        System.out.println("CLOUDINARY UPLOAD STARTED");
        System.out.println(
                "FILE BYTES: " + fileBytes.length
        );
        System.out.println(
                "OPTIONS: " + options
        );
        System.out.println("================================");

        try {

            Map<?, ?> result =
                    cloudinary
                            .uploader()
                            .upload(
                                    fileBytes,
                                    options
                            );

            System.out.println("================================");
            System.out.println("CLOUDINARY UPLOAD SUCCESS");
            System.out.println(
                    "SECURE URL: " +
                            result.get("secure_url")
            );
            System.out.println("================================");

            return result;

        } catch (Exception e) {

            System.err.println("================================");
            System.err.println("CLOUDINARY UPLOAD FAILED");
            System.err.println(
                    "EXCEPTION TYPE: "
                            + e.getClass().getName()
            );
            System.err.println(
                    "MESSAGE: "
                            + e.getMessage()
            );
            System.err.println("================================");

            e.printStackTrace();

            throw e;
        }
    }
}