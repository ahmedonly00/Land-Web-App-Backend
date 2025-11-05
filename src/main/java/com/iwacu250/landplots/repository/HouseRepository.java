package com.iwacu250.landplots.repository;

import com.iwacu250.landplots.entity.House;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HouseRepository extends JpaRepository<House, Long>, JpaSpecificationExecutor<House> {
    List<House> findByStatus(String status);
    List<House> findByType(String type);
    List<House> findByLocationContainingIgnoreCase(String location);
    List<House> findByPriceBetween(BigDecimal minPrice, BigDecimal maxPrice);
    List<House> findByBedrooms(Integer bedrooms);
}
