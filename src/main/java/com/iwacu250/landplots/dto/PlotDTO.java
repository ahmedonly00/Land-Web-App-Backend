package com.iwacu250.landplots.dto;

import com.iwacu250.landplots.entity.PropertyStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PlotDTO {
    private Long id;
    private String title;
    private String location;
    private Double size;
    private String sizeUnit;
    private Double price;
    private String currency;
    private String description;
    private PropertyStatus status;
    private String featuredImageUrl;
    private String videoUrl;
    private Double latitude;
    private Double longitude;
    private List<ImageDTO> images = new ArrayList<>();
    private Set<String> features = new HashSet<>();
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
