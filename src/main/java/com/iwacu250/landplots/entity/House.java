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

    @Column
    private Double latitude;

    @Column
    private Double longitude;

    @OneToMany(mappedBy = "house", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<HouseImage> images = new ArrayList<>();

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
        name = "house_features",
        joinColumns = @JoinColumn(name = "house_id"),
        inverseJoinColumns = @JoinColumn(name = "feature_id")
    )
    private Set<HouseFeature> features = new HashSet<>();

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

    // Helper methods
    public void addImage(HouseImage image) {
        images.add(image);
        image.setHouse(this);
    }

    public void removeImage(HouseImage image) {
        images.remove(image);
        image.setHouse(null);
    }

    public void addFeature(HouseFeature feature) {
        features.add(feature);
        feature.getHouses().add(this);
    }

    public void removeFeature(HouseFeature feature) {
        features.remove(feature);
        feature.getHouses().remove(this);
    }
}
