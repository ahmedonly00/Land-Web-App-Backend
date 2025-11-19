package com.iwacu250.landplots.repository;

import com.iwacu250.landplots.entity.House;
import com.iwacu250.landplots.entity.PropertyStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface HouseRepository extends JpaRepository<House, Long> {
    
    @Query("SELECT DISTINCT h FROM House h " +
           "LEFT JOIN FETCH h.houseFeatures hf " +
           "LEFT JOIN FETCH hf.feature f " +
           "LEFT JOIN FETCH f.houseJoins " +
           "WHERE h.id = :id")
    Optional<House> findByIdWithFeatures(@Param("id") Long id);
    List<House> findByStatus(String status);
    List<House> findByType(String type);
    List<House> findByLocationContainingIgnoreCase(String location);
    List<House> findByPriceBetween(Long minPrice, Long maxPrice);
    List<House> findByBedrooms(Integer bedrooms);
    
    // Dashboard statistics methods
    Long countByStatus(PropertyStatus status);
}
