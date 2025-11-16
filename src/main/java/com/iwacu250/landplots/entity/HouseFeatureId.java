package com.iwacu250.landplots.entity;

import jakarta.persistence.Embeddable;
import lombok.Data;
import java.io.Serializable;

@Data
@Embeddable
public class HouseFeatureId implements Serializable {
    private Long houseId;
    private Long featureId;

    public HouseFeatureId() {}
    
    public HouseFeatureId(Long houseId, Long featureId) {
        this.houseId = houseId;
        this.featureId = featureId;
    }
}
