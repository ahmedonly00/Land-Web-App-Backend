package com.iwacu250.landplots.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PlotRequestDTO {

    @NotBlank(message = "Title is required")
    private String title;

    @NotBlank(message = "Location is required")
    private String location;

    @NotNull(message = "Size is required")
    @Positive(message = "Size must be positive")
    private Double size;

    @NotBlank(message = "Size unit is required")
    private String sizeUnit;

    @NotNull(message = "Price is required")
    @Positive(message = "Price must be positive")
    private Double price;

    private String currency = "RWF";

    private String description;

    private String status = "AVAILABLE";

    private String videoUrl;

    private Double latitude;

    private Double longitude;

    private Set<String> features = new HashSet<>();
}
