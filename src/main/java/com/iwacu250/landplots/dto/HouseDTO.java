package com.iwacu250.landplots.dto;

import com.iwacu250.landplots.validation.ValidPropertyType;
import com.iwacu250.landplots.model.PropertyStatus;
import com.iwacu250.landplots.model.PropertyType;
import com.iwacu250.landplots.validation.ValidPropertyStatus;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Data
public class HouseDTO {
    private Long id;

    @NotBlank(message = "Title is required")
    @Size(max = 100, message = "Title must be less than 100 characters")
    private String title;

    @NotBlank(message = "Location is required")
    @Size(max = 200, message = "Location must be less than 200 characters")
    private String location;

    @NotNull(message = "Size is required")
    @DecimalMin(value = "0.01", message = "Size must be greater than 0")
    @Digits(integer = 10, fraction = 2, message = "Size must have up to 10 digits before and 2 after decimal")
    private BigDecimal size;

    @NotBlank(message = "Size unit is required")
    @Size(max = 20, message = "Size unit must be less than 20 characters")
    private String sizeUnit = "sqm";

    @NotNull(message = "Price is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Price must be greater than 0")
    @Digits(integer = 15, fraction = 2, message = "Price must have up to 15 digits before and 2 after decimal")
    private BigDecimal price;

    @NotBlank(message = "Currency is required")
    @Size(min = 3, max = 3, message = "Currency must be a 3-letter code")
    private String currency = "RWF";

    @Size(max = 2000, message = "Description must be less than 2000 characters")
    private String description;

    @ValidPropertyType
    private PropertyType type;

    @Min(value = 0, message = "Number of bedrooms must be 0 or more")
    @Max(value = 100, message = "Number of bedrooms must be less than 100")
    private Integer bedrooms;

    @DecimalMin(value = "0.0", message = "Number of bathrooms must be 0 or more")
    @Digits(integer = 2, fraction = 1, message = "Bathrooms must have up to 2 digits before and 1 after decimal")
    private BigDecimal bathrooms;

    @Min(value = 1800, message = "Year built must be 1800 or later")
    @Max(value = 2100, message = "Year built must be before 2100")
    private Integer yearBuilt;

    @Min(value = 1, message = "Number of floors must be at least 1")
    @Max(value = 200, message = "Number of floors must be less than 200")
    private Integer floors;

    @ValidPropertyStatus
    private PropertyStatus status = PropertyStatus.AVAILABLE;

    @Size(max = 500, message = "Featured image URL must be less than 500 characters")
    private String featuredImageUrl;

    @Size(max = 500, message = "Video URL must be less than 500 characters")
    private String videoUrl;

    @DecimalMin(value = "-90.0", message = "Latitude must be between -90 and 90")
    @DecimalMax(value = "90.0", message = "Latitude must be between -90 and 90")
    private BigDecimal latitude;

    @DecimalMin(value = "-180.0", message = "Longitude must be between -180 and 180")
    @DecimalMax(value = "180.0", message = "Longitude must be between -180 and 180")
    private BigDecimal longitude;

    @Valid
    private Set<HouseFeatureDTO> features = new HashSet<>();
    
    private Set<@Size(max = 500, message = "Image URL must be less than 500 characters") String> imageUrls = new HashSet<>();
    
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
