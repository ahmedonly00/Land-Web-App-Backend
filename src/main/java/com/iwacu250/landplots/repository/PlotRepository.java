package com.iwacu250.landplots.repository;

import com.iwacu250.landplots.entity.PropertyStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.iwacu250.landplots.entity.Plot;

import java.util.List;

@Repository
public interface PlotRepository extends JpaRepository<Plot, Long>, JpaSpecificationExecutor<Plot> {
    
    @Query("SELECT p FROM Plot p WHERE p.status = :status")
    Page<Plot> findByStatus(@Param("status") PropertyStatus status, Pageable pageable);
    
    @Query("SELECT COUNT(p) FROM Plot p WHERE p.status = :status")
    Long countByStatus(@Param("status") PropertyStatus status);
    
    @Query("SELECT p FROM Plot p WHERE p.status = 'AVAILABLE' ORDER BY p.createdAt DESC")
    List<Plot> findFeaturedPlots(Pageable pageable);
    
    @Query("SELECT p FROM Plot p WHERE " +
           "(:status IS NULL OR p.status = :status) AND " +
           "(:minPrice IS NULL OR p.price >= :minPrice) AND " +
           "(:maxPrice IS NULL OR p.price <= :maxPrice) AND " +
           "(:minSize IS NULL OR p.size >= :minSize) AND " +
           "(:maxSize IS NULL OR p.size <= :maxSize)")
    Page<Plot> searchPlots(
        @Param("status") PropertyStatus status,
        @Param("location") String location,
        @Param("minPrice") Double minPrice,
        @Param("maxPrice") Double maxPrice,
        @Param("minSize") Double minSize,
        @Param("maxSize") Double maxSize,
        Pageable pageable
    );
    
}
