package com.iwacu250.landplots.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class HouseFeatureDTO {
    private Long id;

    @NotBlank(message = "Feature name is required")
    @Size(min = 2, max = 50, message = "Name must be between 2 and 50 characters")
    private String name;

    @Size(max = 500, message = "Description must be less than 500 characters")
    private String description;

    @Size(max = 50, message = "Icon class must be less than 50 characters")
    private String icon;
}
