package com.iwacu250.landplots.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.Collections;
import java.util.List;


@Service
public class FileStorageService {

    private static final Logger logger = LoggerFactory.getLogger(FileStorageService.class);

    private final Cloudinary cloudinary;

    public FileStorageService(
            @Value("${cloudinary.cloud-name}") String cloudName,
            @Value("${cloudinary.api-key}") String apiKey,
            @Value("${cloudinary.api-secret}") String apiSecret) {
        
        this.cloudinary = new Cloudinary(ObjectUtils.asMap(
                "cloud_name", cloudName,
                "api_key", apiKey,
                "api_secret", apiSecret,
                "secure", true
        ));
    }

    public String uploadFile(MultipartFile file) throws IOException {
        try {
            // Generate unique filename
            String originalFilename = file.getOriginalFilename();
            String fileExtension = originalFilename != null && originalFilename.contains(".") 
                    ? originalFilename.substring(originalFilename.lastIndexOf("."))
                    : "";
            String uniqueFilename = "iwacu250_" + UUID.randomUUID().toString() + fileExtension;

            // Upload to Cloudinary
            @SuppressWarnings("unchecked")
            Map<String, Object> uploadResult = cloudinary.uploader().upload(file.getBytes(),
                    ObjectUtils.asMap(
                            "folder", "iwacu250/plots",
                            "public_id", uniqueFilename,
                            "resource_type", "auto",
                            "transformation", ObjectUtils.asMap(
                                    "width", 1200,
                                    "height", 800,
                                    "crop", "limit"
                            )
                    ));

            String imageUrl = (String) uploadResult.get("secure_url");
            logger.info("File uploaded successfully: {}", imageUrl);
            return imageUrl;

        } catch (IOException e) {
            logger.error("Failed to upload file to Cloudinary", e);
            throw new IOException("Failed to upload file: " + e.getMessage());
        }
    }

    public void deleteFile(String imageUrl) throws IOException {
        String publicId = extractPublicIdFromUrl(imageUrl);
        if (publicId != null) {
            try {
                @SuppressWarnings("unchecked")
                Map<String, Object> result = cloudinary.uploader().destroy(publicId, Collections.emptyMap());
                logger.info("File deleted successfully: {}", result);
            } catch (IOException e) {
                logger.error("Failed to delete file from Cloudinary", e);
                throw new IOException("Failed to delete file: " + e.getMessage(), e);
            } catch (Exception e) {
                logger.error("Unexpected error deleting file from Cloudinary", e);
                throw new IOException("Unexpected error deleting file: " + e.getMessage(), e);
            }
        }
    }

    private String extractPublicIdFromUrl(String imageUrl) {
        try {
            // Extract public_id from Cloudinary URL
            // Example: https://res.cloudinary.com/cloud_name/image/upload/v123456/iwacu250/plots/filename.jpg
            String[] parts = imageUrl.split("/upload/");
            if (parts.length > 1) {
                String pathAfterUpload = parts[1];
                // Remove version number if present
                String[] pathParts = pathAfterUpload.split("/");
                if (pathParts.length > 1) {
                    // Reconstruct path without version
                    StringBuilder publicId = new StringBuilder();
                    for (int i = 1; i < pathParts.length; i++) {
                        if (i > 1) publicId.append("/");
                        publicId.append(pathParts[i]);
                    }
                    // Remove file extension
                    String result = publicId.toString();
                    int lastDot = result.lastIndexOf(".");
                    if (lastDot > 0) {
                        result = result.substring(0, lastDot);
                    }
                    return result;
                }
            }
        } catch (Exception e) {
            logger.error("Failed to extract public_id from URL: {}", imageUrl, e);
        }
        return null;
    }
}
