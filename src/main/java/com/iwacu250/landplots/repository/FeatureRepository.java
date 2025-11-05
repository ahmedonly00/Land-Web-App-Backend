package com.iwacu250.landplots.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.iwacu250.landplots.model.Feature;

import java.util.Optional;

@Repository
public interface FeatureRepository extends JpaRepository<Feature, Long> {
    Optional<Feature> findByName(String name);
}
