package com.iwacu250.landplots.repository;

import com.iwacu250.landplots.entity.House;
import com.iwacu250.landplots.entity.PropertyStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HouseRepository extends JpaRepository<House, Long> {
    List<House> findByStatus(String status);
    List<House> findByType(String type);
    List<House> findByLocationContainingIgnoreCase(String location);
    List<House> findByPriceBetween(Long minPrice, Long maxPrice);
    List<House> findByBedrooms(Integer bedrooms);
    
    // Dashboard statistics methods
    Long countByStatus(PropertyStatus status);
}
