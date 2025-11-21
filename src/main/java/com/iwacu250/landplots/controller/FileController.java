package com.iwacu250.landplots.controller;

import com.iwacu250.landplots.service.FileStorageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;
import java.util.HashMap;


@RestController
@RequestMapping("/api/files")
@Tag(name = "File Management", description = "APIs for file uploads and management")
@CrossOrigin(origins = {"https://iwacu250.com/", "https://www.iwacu250.com/"})
public class FileController {

    private final FileStorageService fileStorageService;

    public FileController(FileStorageService fileStorageService) {
        this.fileStorageService = fileStorageService;
    }

    @PostMapping(value = "/upload/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(
        summary = "Upload an image file",
        description = "Uploads an image file to the server"
    )
    @ApiResponse(
        responseCode = "200",
        description = "Image uploaded successfully"
    )
    public ResponseEntity<?> uploadImage(
            @RequestParam("file") MultipartFile file) {
        
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("Please select an image to upload");
        }

        try {
            String filePath = fileStorageService.storeImage(file);
            Map<String, String> response = new HashMap<>();
            response.put("filePath", filePath);
            response.put("fullUrl", "/uploads/" + filePath);
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                .body("Failed to upload image: " + e.getMessage());
        }
    }

    @PostMapping(value = "/upload/video", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(
        summary = "Upload a video file",
        description = "Uploads a video file to the server"
    )
    @ApiResponse(
        responseCode = "200",
        description = "Video uploaded successfully"
    )
    public ResponseEntity<?> uploadVideo(
            @RequestParam("file") MultipartFile file) {
        
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("Please select a video to upload");
        }

        try {
            String filePath = fileStorageService.storeVideo(file);
            Map<String, String> response = new HashMap<>();
            response.put("filePath", filePath);
            response.put("fullUrl", "/uploads/" + filePath);
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                .body("Failed to upload video: " + e.getMessage());
        }
    }

    @DeleteMapping("/delete")
    @Operation(
        summary = "Delete a file",
        description = "Deletes a file using its file path"
    )
    @ApiResponse(responseCode = "200", description = "File deleted successfully")
    public ResponseEntity<?> deleteFile(@RequestParam("filePath") String filePath) {
        if (filePath == null || filePath.isEmpty()) {
            return ResponseEntity.badRequest().body("File path is required");
        }
        
        try {
            fileStorageService.deleteFile(filePath);
            return ResponseEntity.ok().body("File deleted successfully");
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                .body("Failed to delete file: " + e.getMessage());
        }
    }
    
    @GetMapping("/list")
    @Operation(
        summary = "List all uploaded files",
        description = "Returns a list of all uploaded images and videos with their URLs"
    )
    @ApiResponse(responseCode = "200", description = "Successfully retrieved list of files")
    public ResponseEntity<Map<String, List<String>>> listFiles() {
        return ResponseEntity.ok(fileStorageService.listUploadedFiles());
    }
    
    @GetMapping("/{filePath:.+}")
    @Operation(
        summary = "Get file URL",
        description = "Returns the URL to access the file"
    )
    @ApiResponse(responseCode = "200", description = "Successfully retrieved file URL")
    public ResponseEntity<?> getFileUrl(@PathVariable String filePath) {
        try {
            // The file is served directly by WebMvcConfig via /uploads/**
            String fileUrl = "/uploads/" + filePath;
            Map<String, String> response = new HashMap<>();
            response.put("url", fileUrl);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Failed to get file URL: " + e.getMessage());
        }
    }
}
