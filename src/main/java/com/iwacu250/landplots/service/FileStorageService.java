package com.iwacu250.landplots.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.Optional;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.Data;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class FileStorageService {

    @Value("${file.upload-dir:uploads/}")
    private String uploadDir;
    
    @Value("${file.allowed-image-types:jpg,jpeg,png,gif,webp}")
    private String allowedImageTypes;
    
    @Value("${file.allowed-video-types:mp4,webm,ogg}")
    private String allowedVideoTypes;
    
    private Set<String> allowedImageExtensions;
    private Set<String> allowedVideoExtensions;
    
    public String storeImage(MultipartFile file) {
        return storeFile(file, "image");
    }

    public String storeVideo(MultipartFile file) {
        return storeFile(file, "video");
    }
    
    public FileUploadResponse uploadFile(MultipartFile file, String folder) throws IOException {
        String fileType = getFileType(file.getContentType());
        String filePath = storeFile(file, fileType);
        
        FileUploadResponse response = new FileUploadResponse();
        response.setUrl(filePath);
        response.setPublicId(filePath); // In a real implementation, this would be the Cloudinary public ID
        response.setContentType(file.getContentType());
        response.setFileSize(file.getSize());
        response.setUploadedAt(LocalDateTime.now());
        
        return response;
    }
    
    @PostConstruct
    public void init() {
        // Initialize allowed file extensions
        allowedImageExtensions = new HashSet<>(Arrays.asList(allowedImageTypes.toLowerCase().split(",")));
        allowedVideoExtensions = new HashSet<>(Arrays.asList(allowedVideoTypes.toLowerCase().split(",")));
        
        // Create upload directory if it doesn't exist
        try {
            Files.createDirectories(Paths.get(uploadDir).toAbsolutePath().normalize());
        } catch (IOException ex) {
            throw new RuntimeException("Could not create upload directory", ex);
        }
    }

    private String storeFile(MultipartFile file, String fileType) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("File cannot be empty");
        }

        try {
            // Validate file type
            String originalFileName = StringUtils.cleanPath(file.getOriginalFilename());
            String fileExtension = getFileExtension(originalFileName)
                .orElseThrow(() -> new IllegalArgumentException("File must have an extension"));

            if (fileType.equalsIgnoreCase("image") && !isValidImage(fileExtension)) {
                throw new IllegalArgumentException("Invalid image type. Allowed types: " + allowedImageTypes);
            } else if (fileType.equalsIgnoreCase("video") && !isValidVideo(fileExtension)) {
                throw new IllegalArgumentException("Invalid video type. Allowed types: " + allowedVideoTypes);
            }

            // Create target directory
            Path targetDir = Paths.get(uploadDir, fileType + "s").toAbsolutePath().normalize();
            Files.createDirectories(targetDir);

            // Generate unique filename
            String uniqueFileName = UUID.randomUUID().toString() + "." + fileExtension;
            Path targetLocation = targetDir.resolve(uniqueFileName);

            // Save file
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

            return fileType + "s/" + uniqueFileName;
        } catch (IOException ex) {
            throw new RuntimeException("Failed to store file " + file.getOriginalFilename(), ex);
        }
    }

    public boolean deleteFile(String filePath) {
        try {
            Path fileToDelete = Paths.get(uploadDir).resolve(filePath).normalize();
            return Files.deleteIfExists(fileToDelete);
        } catch (IOException ex) {
            throw new RuntimeException("Failed to delete file: " + filePath, ex);
        }
    }

    private String getFileType(String contentType) {
        if (contentType == null) {
            return "other";
        }
        if (contentType.startsWith("image/")) {
            return "image";
        } else if (contentType.startsWith("video/")) {
            return "video";
        }
        return "other";
    }
    
    private Optional<String> getFileExtension(String fileName) {
        if (fileName == null) {
            return Optional.empty();
        }
        int lastDot = fileName.lastIndexOf('.');
        if (lastDot == -1) {
            return Optional.empty();
        }
        return Optional.of(fileName.substring(lastDot + 1).toLowerCase());
    }
    
    @Data
    public static class FileUploadResponse {
        private String url;
        private String publicId;
        private String contentType;
        private long fileSize;
        private LocalDateTime uploadedAt;
    }

    private boolean isValidImage(String extension) {
        return allowedImageExtensions.contains(extension.toLowerCase());
    }

    private boolean isValidVideo(String extension) {
        return allowedVideoExtensions.contains(extension.toLowerCase());
    }
    
    /**
     * List all uploaded files with their URLs
     * @return Map containing lists of image and video URLs
     */
    public Map<String, List<String>> listUploadedFiles() {
        Map<String, List<String>> files = new HashMap<>();
        
        // List image files
        Path imageDir = Paths.get(uploadDir, "images").toAbsolutePath().normalize();
        files.put("images", listFilesInDirectory(imageDir));
        
        // List video files
        Path videoDir = Paths.get(uploadDir, "videos").toAbsolutePath().normalize();
        files.put("videos", listFilesInDirectory(videoDir));
        
        return files;
    }
    
    private List<String> listFilesInDirectory(Path directory) {
        try {
            if (Files.notExists(directory)) {
                Files.createDirectories(directory);
                return new ArrayList<>();
            }
            
            return Files.walk(directory, 1)
                .filter(Files::isRegularFile)
                .map(directory::relativize)
                .map(Path::toString)
                .filter(path -> !path.isEmpty())
                .map(path -> "/uploads/" + directory.getFileName() + "/" + path)
                .collect(Collectors.toList());
        } catch (IOException e) {
            throw new RuntimeException("Failed to list files in directory: " + directory, e);
        }
    }
}
