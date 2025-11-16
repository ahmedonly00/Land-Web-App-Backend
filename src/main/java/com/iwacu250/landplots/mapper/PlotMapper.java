package com.iwacu250.landplots.mapper;

import com.iwacu250.landplots.dto.ImageDTO;
import com.iwacu250.landplots.dto.PlotDTO;
import com.iwacu250.landplots.dto.PlotRequestDTO;
import com.iwacu250.landplots.entity.Feature;
import com.iwacu250.landplots.entity.Image;
import com.iwacu250.landplots.entity.Plot;
import com.iwacu250.landplots.entity.PropertyStatus;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class PlotMapper {

    public static PlotDTO toDto(Plot plot) {
        if (plot == null) {
            return null;
        }

        PlotDTO dto = new PlotDTO();
        dto.setId(plot.getId());
        dto.setTitle(plot.getTitle());
        dto.setLocation(plot.getLocation());
        dto.setSize(plot.getSize());
        dto.setSizeUnit(plot.getSizeUnit());
        dto.setPrice(plot.getPrice());
        dto.setCurrency(plot.getCurrency());
        dto.setDescription(plot.getDescription());
        dto.setStatus(plot.getStatus());
        dto.setFeaturedImageUrl(plot.getFeaturedImageUrl());
        dto.setVideoUrl(plot.getVideoUrl());
        dto.setCreatedAt(plot.getCreatedAt());
        dto.setUpdatedAt(plot.getUpdatedAt());

        // Map images
        if (plot.getImages() != null) {
            List<ImageDTO> imageDTOs = plot.getImages().stream()
                    .map(PlotMapper::toImageDto)
                    .collect(Collectors.toList());
            dto.setImages(imageDTOs);
        }

        // Map features
        if (plot.getFeatures() != null) {
            Set<String> featureNames = plot.getFeatures().stream()
                    .map(Feature::getName)
                    .collect(Collectors.toSet());
            dto.setFeatures(featureNames);
        }

        return dto;
    }

    public static void updateEntityFromRequest(Plot plot, PlotRequestDTO dto) {
        if (dto == null || plot == null) {
            return;
        }

        plot.setTitle(dto.getTitle());
        plot.setLocation(dto.getLocation());
        plot.setSize(dto.getSize());
        plot.setSizeUnit(dto.getSizeUnit());
        plot.setPrice(dto.getPrice());
        plot.setCurrency(dto.getCurrency() != null ? dto.getCurrency() : "RWF");
        plot.setDescription(dto.getDescription());
        plot.setStatus(dto.getStatus() != null ? PropertyStatus.valueOf(dto.getStatus()) : PropertyStatus.AVAILABLE);
        plot.setVideoUrl(dto.getVideoUrl());
    }

    public static ImageDTO toImageDto(Image image) {
        if (image == null) {
            return null;
        }

        ImageDTO dto = new ImageDTO();
        dto.setId(image.getId());
        dto.setImageUrl(image.getImageUrl());
        dto.setDisplayOrder(image.getDisplayOrder());
        dto.setIsFeatured(image.getIsFeatured());
        dto.setUploadedAt(image.getUploadedAt());
        return dto;
    }
}
