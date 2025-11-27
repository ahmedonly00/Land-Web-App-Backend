package com.iwacu250.landplots.repository;

import java.util.List;

import com.iwacu250.landplots.entity.HouseImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HouseImageRepository extends JpaRepository<HouseImage, Long> {
    List<HouseImage> findByHouseIdOrderByDisplayOrder(Long houseId);
}
