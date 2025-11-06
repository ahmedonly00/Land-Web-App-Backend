package com.iwacu250.landplots.service;

import com.iwacu250.landplots.dto.ImageDTO;
import com.iwacu250.landplots.dto.PlotDTO;
import com.iwacu250.landplots.dto.PlotRequestDTO;
import com.iwacu250.landplots.entity.Feature;
import com.iwacu250.landplots.entity.Image;
import com.iwacu250.landplots.entity.Plot;
import com.iwacu250.landplots.entity.PropertyStatus;
import com.iwacu250.landplots.exception.ResourceNotFoundException;
import com.iwacu250.landplots.mapper.PlotMapper;
import com.iwacu250.landplots.repository.FeatureRepository;
import com.iwacu250.landplots.repository.ImageRepository;
import com.iwacu250.landplots.repository.PlotRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.lang.NonNull;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PlotService {

    @Autowired
    private PlotRepository plotRepository;

    @Autowired
    private ImageRepository imageRepository;

    @Autowired
    private FeatureRepository featureRepository;

    @Autowired
    private FileStorageService fileStorageService;

    // ModelMapper has been replaced with manual mapping in PlotMapper

    @Transactional(readOnly = true)
    public Page<PlotDTO> getAllPlots(int page, int size, String sortBy, String sortDir,
                                      String status, String location, Double minPrice,
                                      Double maxPrice, Double minSize, Double maxSize) {
        Sort sort = sortDir.equalsIgnoreCase("asc") 
                ? Sort.by(sortBy).ascending() 
                : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<Plot> plots = plotRepository.searchPlots(status, location, minPrice, maxPrice, minSize, maxSize, pageable);
        return plots.map(this::convertToDTO);
    }

    @Transactional(readOnly = true)
    public List<PlotDTO> getFeaturedPlots(int limit) {
        Pageable pageable = PageRequest.of(0, limit);
        List<Plot> plots = plotRepository.findFeaturedPlots(pageable);
        return plots.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public PlotDTO getPlotById(Long id) {
        Plot plot = plotRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Plot", "id", id));
        return convertToDTO(plot);
    }

    @Transactional
    public PlotDTO createPlot(PlotRequestDTO plotRequestDTO) {
        Plot plot = new Plot();
        updatePlotFromDTO(plot, plotRequestDTO);
        Plot savedPlot = plotRepository.save(plot);
        return convertToDTO(savedPlot);
    }

    @Transactional
    public PlotDTO updatePlot(Long id, PlotRequestDTO plotRequestDTO) {
        Plot plot = plotRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Plot", "id", id));
        
        updatePlotFromDTO(plot, plotRequestDTO);
        Plot updatedPlot = plotRepository.save(plot);
        return convertToDTO(updatedPlot);
    }

    @Transactional
    public void deletePlot(@NonNull Long id) {
        
        // Get the plot to delete its images
        Plot plot = plotRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Plot", "id", id));
        
        // Delete all images from cloud storage
        for (Image image : plot.getImages()) {
            try {
                fileStorageService.deleteFile(image.getImageUrl());
            } catch (IOException e) {
                System.err.println("Failed to delete image file: " + image.getImageUrl() + ", Error: " + e.getMessage());
            }
        }
        
        plotRepository.delete(plot);
    }

    @Transactional
    public PlotDTO updatePlotStatus(Long id, String statusStr) {
        Plot plot = plotRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Plot", "id", id));
        PropertyStatus status = PropertyStatus.valueOf(statusStr);
        plot.setStatus(status);
        Plot updatedPlot = plotRepository.save(plot);
        return convertToDTO(updatedPlot);
    }

    @Transactional
    public ImageDTO uploadImage(Long plotId, MultipartFile file, Integer displayOrder, Boolean isFeatured) throws IOException {
        Plot plot = plotRepository.findById(plotId)
                .orElseThrow(() -> new ResourceNotFoundException("Plot", "id", plotId));

        String imageUrl = fileStorageService.uploadFile(file);

        Image image = new Image();
        image.setPlot(plot);
        image.setImageUrl(imageUrl);
        image.setDisplayOrder(displayOrder != null ? displayOrder : plot.getImages().size());
        image.setIsFeatured(isFeatured != null ? isFeatured : false);

        Image savedImage = imageRepository.save(image);

        // Update featured image if this is marked as featured
        if (Boolean.TRUE.equals(isFeatured)) {
            plot.setFeaturedImageUrl(imageUrl);
            plotRepository.save(plot);
        }

        return convertImageToDTO(savedImage);
    }

    @Transactional
    public void deleteImage(Long imageId) {
        Image image = imageRepository.findById(imageId)
                .orElseThrow(() -> new ResourceNotFoundException("Image", "id", imageId));
        
        try {
            fileStorageService.deleteFile(image.getImageUrl());
        } catch (IOException e) {
            System.err.println("Failed to delete image file: " + image.getImageUrl() + ", Error: " + e.getMessage());
        }
        
        imageRepository.delete(image);
    }

    @Transactional
    public void reorderImages(Long plotId, List<Long> imageIds) {
        // Verify plot exists
        if (!plotRepository.existsById(plotId)) {
            throw new ResourceNotFoundException("Plot", "id", plotId);
        }

        for (int i = 0; i < imageIds.size(); i++) {
            Long imageId = imageIds.get(i);
            Image image = imageRepository.findById(imageId)
                    .orElseThrow(() -> new ResourceNotFoundException("Image", "id", imageId));
            image.setDisplayOrder(i);
            imageRepository.save(image);
        }
    }

    private void updatePlotFromDTO(Plot plot, PlotRequestDTO dto) {
        // Update basic fields
        PlotMapper.updateEntityFromRequest(plot, dto);
        
        // Handle features
        plot.getFeatures().clear();
        if (dto.getFeatures() != null && !dto.getFeatures().isEmpty()) {
            for (String featureName : dto.getFeatures()) {
                Feature feature = featureRepository.findByName(featureName)
                        .orElseGet(() -> {
                            Feature newFeature = new Feature(featureName);
                            return featureRepository.save(newFeature);
                        });
                plot.addFeature(feature);
            }
        }
    }

    private PlotDTO convertToDTO(Plot plot) {
        return PlotMapper.toDto(plot);
    }
    
    // Kept for backward compatibility with existing code
    private ImageDTO convertImageToDTO(Image image) {
        return PlotMapper.toImageDto(image);
    }
}
