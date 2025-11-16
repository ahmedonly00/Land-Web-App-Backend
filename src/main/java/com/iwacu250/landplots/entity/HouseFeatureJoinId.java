package com.iwacu250.landplots.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class HouseFeatureJoinId implements Serializable {
    @Column(name = "house_id")
    private Long houseId;

    @Column(name = "feature_id")
    private Long featureId;

    // Default constructor
    public HouseFeatureJoinId() {}

    // Parameterized constructor
    public HouseFeatureJoinId(Long houseId, Long featureId) {
        this.houseId = houseId;
        this.featureId = featureId;
    }

    // Getters and setters
    public Long getHouseId() {
        return houseId;
    }

    public void setHouseId(Long houseId) {
        this.houseId = houseId;
    }

    public Long getFeatureId() {
        return featureId;
    }

    public void setFeatureId(Long featureId) {
        this.featureId = featureId;
    }

    // Override equals and hashCode
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        HouseFeatureJoinId that = (HouseFeatureJoinId) o;
        return Objects.equals(houseId, that.houseId) &&
               Objects.equals(featureId, that.featureId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(houseId, featureId);
    }
}
