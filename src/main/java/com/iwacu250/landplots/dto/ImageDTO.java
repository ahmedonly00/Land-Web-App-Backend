package com.iwacu250.landplots.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ImageDTO {
    private Long id;
    private String imageUrl;
    private Integer displayOrder;
    private Boolean isFeatured;
    private LocalDateTime uploadedAt;
}
