package com.iwacu250.landplots.repository;

import com.iwacu250.landplots.entity.Image;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ImageRepository extends JpaRepository<Image, Long> {
    List<Image> findByPlotIdOrderByDisplayOrderAsc(Long plotId);
    void deleteByPlotId(Long plotId);
}
