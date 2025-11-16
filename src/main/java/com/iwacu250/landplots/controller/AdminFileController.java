package com.iwacu250.landplots.controller;

import com.iwacu250.landplots.service.FileStorageService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/files")
@CrossOrigin(origins = "*")
public class AdminFileController {

    private final FileStorageService fileStorageService;

    public AdminFileController(FileStorageService fileStorageService) {
        this.fileStorageService = fileStorageService;
    }

    @PostMapping(value = "/upload/image")
    public ResponseEntity<?> uploadImage(@RequestParam("file") MultipartFile file) {
        try {
            String filePath = fileStorageService.storeImage(file);
            return ResponseEntity.ok(createSuccessResponse(filePath));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(createErrorResponse(e));
        }
    }

    @PostMapping(value = "/upload/video")
    public ResponseEntity<?> uploadVideo(@RequestParam("file") MultipartFile file) {
        try {
            String filePath = fileStorageService.storeVideo(file);
            return ResponseEntity.ok(createSuccessResponse(filePath));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(createErrorResponse(e));
        }
    }

    @DeleteMapping(value = "/deleteFile")
    public ResponseEntity<?> deleteFile(@RequestParam String filePath) {
        try {
            boolean deleted = fileStorageService.deleteFile(filePath);
            if (deleted) {
                return ResponseEntity.ok().build();
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(createErrorResponse(e));
        }
    }

    private Map<String, Object> createSuccessResponse(String filePath) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("filePath", filePath);
        response.put("fullUrl", "/uploads/" + filePath); // Adjust this based on your server configuration
        return response;
    }

    private Map<String, Object> createErrorResponse(Exception e) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", false);
        response.put("error", e.getMessage());
        return response;
    }
}
