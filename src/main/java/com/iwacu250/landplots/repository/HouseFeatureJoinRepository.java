package com.iwacu250.landplots.repository;

import com.iwacu250.landplots.entity.HouseFeatureJoin;
import com.iwacu250.landplots.entity.HouseFeatureJoinId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface HouseFeatureJoinRepository extends JpaRepository<HouseFeatureJoin, HouseFeatureJoinId> {
    
    @Query("SELECT COUNT(j) > 0 FROM HouseFeatureJoin j WHERE j.house.id = :houseId AND j.feature.id = :featureId")
    boolean existsByHouseIdAndFeatureId(@Param("houseId") Long houseId, @Param("featureId") Long featureId);
}
