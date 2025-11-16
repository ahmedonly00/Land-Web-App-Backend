package com.iwacu250.landplots.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class FileUploadResponse {
    private String url;
    private String publicId;
    private String format;
    private long fileSize;
    private String resourceType;

   
    public static FileUploadResponse fromCloudinaryResult(Map<String, Object> uploadResult) {
        return new FileUploadResponse(
            (String) uploadResult.get("secure_url"),
            (String) uploadResult.get("public_id"),
            (String) uploadResult.get("format"),
            ((Number) uploadResult.get("bytes")).longValue(),
            (String) uploadResult.get("resource_type")
        );
    }
}
