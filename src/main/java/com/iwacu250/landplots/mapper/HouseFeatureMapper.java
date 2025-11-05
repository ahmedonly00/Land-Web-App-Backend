package com.iwacu250.landplots.mapper;

import com.iwacu250.landplots.dto.HouseFeatureDTO;
import com.iwacu250.landplots.entity.HouseFeature;

public class HouseFeatureMapper {

    public static HouseFeatureDTO toDto(HouseFeature entity) {
        if (entity == null) {
            return null;
        }
        
        HouseFeatureDTO dto = new HouseFeatureDTO();
        dto.setId(entity.getId());
        dto.setName(entity.getName());
        dto.setDescription(entity.getDescription());
        dto.setIcon(entity.getIcon());
        
        return dto;
    }
    
    public static HouseFeature toEntity(HouseFeatureDTO dto) {
        if (dto == null) {
            return null;
        }
        
        HouseFeature entity = new HouseFeature();
        // Note: We don't set ID when creating a new entity as it's auto-generated
        if (dto.getId() != null) {
            entity.setId(dto.getId());
        }
        entity.setName(dto.getName());
        entity.setDescription(dto.getDescription());
        entity.setIcon(dto.getIcon());
        
        return entity;
    }
    
    public static void updateEntityFromDto(HouseFeatureDTO dto, HouseFeature entity) {
        if (dto == null || entity == null) {
            return;
        }
        
        // We don't update the ID as it shouldn't change
        entity.setName(dto.getName());
        entity.setDescription(dto.getDescription());
        entity.setIcon(dto.getIcon());
    }
}
