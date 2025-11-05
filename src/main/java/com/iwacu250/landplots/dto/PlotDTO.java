package com.iwacu250.landplots.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
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
    private BigDecimal size;
    private String sizeUnit;
    private BigDecimal price;
    private String currency;
    private String description;
    private String status;
    private String featuredImageUrl;
    private String videoUrl;
    private BigDecimal latitude;
    private BigDecimal longitude;
    private List<ImageDTO> images = new ArrayList<>();
    private Set<String> features = new HashSet<>();
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
