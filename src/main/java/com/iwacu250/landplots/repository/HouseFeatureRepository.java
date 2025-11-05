package com.iwacu250.landplots.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.iwacu250.landplots.entity.HouseFeature;

import java.util.List;

@Repository
public interface HouseFeatureRepository extends JpaRepository<HouseFeature, Long> {
    // For non-paginated search (if needed)
    List<HouseFeature> findByNameContainingIgnoreCase(String name);
    
    // For paginated search
    Page<HouseFeature> findByNameContainingIgnoreCase(String name, Pageable pageable);
    
    boolean existsByName(String name);
}
