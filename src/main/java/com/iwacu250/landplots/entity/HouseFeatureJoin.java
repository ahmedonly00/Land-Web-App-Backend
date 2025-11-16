package com.iwacu250.landplots.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

@Entity
@Table(name = "house_features")
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class HouseFeatureJoin implements Serializable {
    @EmbeddedId
    @EqualsAndHashCode.Include
    private HouseFeatureJoinId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("houseId")
    private House house;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("featureId")
    private HouseFeature feature;
    
    @Column(name = "name", nullable = false)
    private String name;
    
    @Column(name = "description")
    private String description;
    
    @Column(name = "icon")
    private String icon;

    public HouseFeatureJoin() {}
    
    public HouseFeatureJoin(House house, HouseFeature feature) {
        this.house = house;
        this.feature = feature;
        this.id = new HouseFeatureJoinId(house.getId(), feature.getId());
        this.name = feature.getName();
        this.description = feature.getDescription();
        this.icon = feature.getIcon();
    }
    
    public void setHouse(House house) {
        this.house = house;
        if (this.id == null) {
            this.id = new HouseFeatureJoinId();
        }
        if (house != null && house.getId() != null) {
            this.id.setHouseId(house.getId());
        }
    }
    
    public void setFeature(HouseFeature feature) {
        this.feature = feature;
        if (this.id == null) {
            this.id = new HouseFeatureJoinId();
        }
        if (feature != null) {
            if (feature.getId() != null) {
                this.id.setFeatureId(feature.getId());
            }
            // Also update the name, description, and icon from the feature
            if (feature.getName() != null) this.name = feature.getName();
            if (feature.getDescription() != null) this.description = feature.getDescription();
            if (feature.getIcon() != null) this.icon = feature.getIcon();
        }
    }
    
    public void setId(HouseFeatureJoinId id) {
        this.id = id;
    }
}
