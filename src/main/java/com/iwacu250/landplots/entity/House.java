package com.iwacu250.landplots.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "houses")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class House {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String location;

    @Column(nullable = false)
    private Double size;

    @Column(nullable = false, name = "size_unit", length = 20)
    private String sizeUnit = "sqm";

    @Column(nullable = false)
    private Double price;

    @Column(length = 10)
    private String currency = "RWF";

    @Column(columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private PropertyType type = PropertyType.HOUSE;

    @Column(name = "bedrooms")
    private Integer bedrooms;

    @Column(name = "bathrooms")
    private Integer bathrooms;

    @Column(name = "year_built")
    private Integer yearBuilt;

    @Column(name = "floors")
    private Integer floors;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 20)
    private PropertyStatus status = PropertyStatus.AVAILABLE;

    @Column(name = "featured_image_url", length = 500)
    private String featuredImageUrl;

    @Column(name = "video_url", length = 500)
    private String videoUrl;

    @OneToMany(mappedBy = "house", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<HouseImage> images = new ArrayList<>();

    @OneToMany(mappedBy = "house", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<HouseFeatureJoin> houseFeatures = new HashSet<>();
    
    // Helper methods for images
    public void addImage(HouseImage image) {
        if (!images.contains(image)) {
            images.add(image);
            image.setHouse(this);
        }
    }

    public void removeImage(HouseImage image) {
        if (images.remove(image)) {
            image.setHouse(null);
        }
    }
    
    // Helper methods for features
    public void addFeature(HouseFeature feature) {
        if (houseFeatures == null) {
            houseFeatures = new HashSet<>();
        }
        
        // Check if this feature is already associated
        boolean exists = houseFeatures.stream()
            .anyMatch(j -> j.getFeature() != null && j.getFeature().getId() != null && 
                         j.getFeature().getId().equals(feature.getId()));
        
        if (!exists) {
            HouseFeatureJoin join = new HouseFeatureJoin();
            join.setHouse(this);
            join.setFeature(feature);
            join.setName(feature.getName());
            join.setDescription(feature.getDescription());
            join.setIcon(feature.getIcon());
            houseFeatures.add(join);
        }
    }
    
    public void removeFeature(HouseFeature feature) {
        if (houseFeatures != null) {
            houseFeatures.removeIf(j -> j.getFeature() != null && 
                                     j.getFeature().getId() != null && 
                                     j.getFeature().getId().equals(feature.getId()));
        }
    }

    public void addFeatureJoin(HouseFeatureJoin join) {
        if (this.houseFeatures == null) {
            this.houseFeatures = new HashSet<>();
        }
        if (join != null) {
            join.setHouse(this);
            this.houseFeatures.add(join);
        }
    }
    
    @Transient
    public Set<HouseFeature> getFeatures() {
        Set<HouseFeature> features = new HashSet<>();
        if (houseFeatures != null) {
            for (HouseFeatureJoin join : houseFeatures) {
                if (join.getFeature() != null) {
                    features.add(join.getFeature());
                }
            }
        }
        return features;
    }

    @Column(nullable = false, updatable = false, name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // No duplicate methods here - they've been moved up

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        House house = (House) o;
        return id != null && id.equals(house.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
