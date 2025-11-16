package com.iwacu250.landplots.mapper;

import com.iwacu250.landplots.dto.HouseDTO;
import com.iwacu250.landplots.entity.House;
import com.iwacu250.landplots.entity.HouseImage;
import com.iwacu250.landplots.entity.PropertyType;
import com.iwacu250.landplots.entity.PropertyStatus;

import java.util.stream.Collectors;

public class HouseMapper {

    public static HouseDTO toDto(House entity) {
        if (entity == null) {
            return null;
        }
        
        HouseDTO dto = new HouseDTO();
        dto.setId(entity.getId());
        dto.setTitle(entity.getTitle());
        dto.setDescription(entity.getDescription());
        dto.setPrice(entity.getPrice());
        dto.setLocation(entity.getLocation());
        dto.setBedrooms(entity.getBedrooms());
        dto.setBathrooms(entity.getBathrooms() != null ? entity.getBathrooms().doubleValue() : null);
        dto.setSize(entity.getSize());
        dto.setSizeUnit(entity.getSizeUnit());
        dto.setCurrency(entity.getCurrency());
        dto.setType(entity.getType());
        dto.setStatus(entity.getStatus());
        dto.setYearBuilt(entity.getYearBuilt());
        dto.setFloors(entity.getFloors());
        dto.setFeaturedImageUrl(entity.getFeaturedImageUrl());
        dto.setVideoUrl(entity.getVideoUrl());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedAt(entity.getUpdatedAt());
        
        // Map features
        if (entity.getFeatures() != null) {
            dto.setFeatures(entity.getFeatures().stream()
                .map(HouseFeatureMapper::toDto)
                .collect(Collectors.toSet()));
        }
        
        // Map image URLs
        if (entity.getImages() != null) {
            dto.setImageUrls(entity.getImages().stream()
                .map(HouseImage::getImageUrl)
                .collect(Collectors.toSet()));
        }
        
        return dto;
    }
    
    public static House toEntity(HouseDTO dto) {
        if (dto == null) {
            return null;
        }
        
        House entity = new House();
        // Note: We don't set ID when creating a new entity as it's auto-generated
        if (dto.getId() != null) {
            entity.setId(dto.getId());
        }
        
        entity.setTitle(dto.getTitle());
        entity.setDescription(dto.getDescription());
        entity.setPrice(dto.getPrice());
        entity.setLocation(dto.getLocation());
        entity.setBedrooms(dto.getBedrooms());
        entity.setBathrooms(dto.getBathrooms() != null ? dto.getBathrooms().intValue() : null);
        entity.setSize(dto.getSize());
        entity.setSizeUnit(dto.getSizeUnit());
        entity.setCurrency(dto.getCurrency());
        entity.setType(dto.getType() != null ? dto.getType() : PropertyType.HOUSE);
        entity.setStatus(dto.getStatus() != null ? dto.getStatus() : PropertyStatus.AVAILABLE);
        entity.setYearBuilt(dto.getYearBuilt());
        entity.setFloors(dto.getFloors());
        entity.setFeaturedImageUrl(dto.getFeaturedImageUrl());
        entity.setVideoUrl(dto.getVideoUrl());
        
        // Features will be handled separately in the service layer
        
        return entity;
    }
    
    public static void updateEntityFromDto(HouseDTO dto, House entity) {
        if (dto == null || entity == null) {
            return;
        }
        
        // We don't update the ID as it shouldn't change
        if (dto.getTitle() != null) entity.setTitle(dto.getTitle());
        if (dto.getDescription() != null) entity.setDescription(dto.getDescription());
        if (dto.getPrice() != null) entity.setPrice(dto.getPrice());
        if (dto.getLocation() != null) entity.setLocation(dto.getLocation());
        if (dto.getBedrooms() != null) entity.setBedrooms(dto.getBedrooms());
        if (dto.getBathrooms() != null) entity.setBathrooms(dto.getBathrooms().intValue());
        if (dto.getSize() != null) entity.setSize(dto.getSize());
        if (dto.getSizeUnit() != null) entity.setSizeUnit(dto.getSizeUnit());
        if (dto.getCurrency() != null) entity.setCurrency(dto.getCurrency());
        if (dto.getType() != null) entity.setType(dto.getType());
        if (dto.getStatus() != null) entity.setStatus(dto.getStatus());
        if (dto.getYearBuilt() != null) entity.setYearBuilt(dto.getYearBuilt());
        if (dto.getFloors() != null) entity.setFloors(dto.getFloors());
        if (dto.getFeaturedImageUrl() != null) entity.setFeaturedImageUrl(dto.getFeaturedImageUrl());
        if (dto.getVideoUrl() != null) entity.setVideoUrl(dto.getVideoUrl());
        
        // Features will be handled separately in the service layer
    }
}
