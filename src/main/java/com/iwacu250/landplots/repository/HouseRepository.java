package com.iwacu250.landplots.repository;

import com.iwacu250.landplots.entity.House;
import com.iwacu250.landplots.entity.PropertyStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import com.iwacu250.landplots.entity.PropertyType;

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
    
    @Query("SELECT DISTINCT h FROM House h " +
           "WHERE (:location IS NULL OR LOWER(h.location) LIKE LOWER(CONCAT('%', :location, '%'))) " +
           "AND (:minPrice IS NULL OR h.price >= :minPrice) " +
           "AND (:maxPrice IS NULL OR h.price <= :maxPrice) " +
           "AND (:bedrooms IS NULL OR h.bedrooms = :bedrooms) " +
           "AND (:type IS NULL OR h.type = :type) " +
           "AND (:status IS NULL OR h.status = :status)")
    Page<House> searchHouses(
            @Param("location") String location,
            @Param("minPrice") Double minPrice,
            @Param("maxPrice") Double maxPrice,
            @Param("bedrooms") Integer bedrooms,
            @Param("type") PropertyType type,
            @Param("status") PropertyStatus status,
            Pageable pageable
    );
}
