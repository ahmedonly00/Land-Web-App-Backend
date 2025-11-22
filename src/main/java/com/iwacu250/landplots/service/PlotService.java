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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.NonNull;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PlotService {

    private static final Logger logger = LoggerFactory.getLogger(PlotService.class);

    @Autowired
    private PlotRepository plotRepository;

    @Autowired
    private ImageRepository imageRepository;

    @Autowired
    private FeatureRepository featureRepository;

    @Autowired
    private FileStorageService fileStorageService;


    @Transactional(readOnly = true)
    public Page<PlotDTO> getAllPlots(int page, int size, String sortBy, String direction,
                                      String statusParam, String location, Double minPrice,
                                      Double maxPrice, Double minSize, Double maxSize) {
        // Convert status string to enum
        PropertyStatus status = null;
        if (statusParam != null && !statusParam.isEmpty()) {
            try {
                status = PropertyStatus.valueOf(statusParam.toUpperCase());
            } catch (IllegalArgumentException e) {
                status = null;
            }
        }
        
        Sort sort = direction.equalsIgnoreCase("asc") 
                ? Sort.by(sortBy).ascending() 
                : Sort.by(sortBy).descending();
                
        Pageable pageable = PageRequest.of(page, size, sort);

        // Search plots with the given filters
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
        
        // Delete all associated images
        for (Image image : plot.getImages()) {
            deleteImage(image.getId());
        }
        
        // Delete the plot
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
        try {
            System.out.println("PlotService: Starting image upload for plot ID: " + plotId);
            System.out.println("PlotService: File name: " + file.getOriginalFilename());
            System.out.println("PlotService: File size: " + file.getSize());
            System.out.println("PlotService: Content type: " + file.getContentType());
            
            if (file == null || file.isEmpty()) {
                throw new IllegalArgumentException("File cannot be null or empty");
            }

            Plot plot = plotRepository.findById(plotId)
                    .orElseThrow(() -> new ResourceNotFoundException("Plot", "id", plotId));
            
            System.out.println("PlotService: Found plot: " + plot.getTitle());

            // Upload the file to the configured storage
            var uploadResponse = fileStorageService.uploadFile(file, "plots");
            System.out.println("PlotService: File uploaded successfully: " + uploadResponse.getUrl());

            // Create and save the image entity
            Image image = new Image();
            image.setPlot(plot);
            image.setImageUrl(uploadResponse.getUrl());
            image.setCloudinaryPublicId(uploadResponse.getPublicId());
            image.setContentType(uploadResponse.getContentType());
            image.setFileSize(file.getSize());
            image.setUploadedAt(LocalDateTime.now()); // Set uploadedAt timestamp
            
            // Safely get display order
            int displayOrderValue = 0;
            if (displayOrder != null) {
                displayOrderValue = displayOrder;
            } else if (plot.getImages() != null) {
                displayOrderValue = plot.getImages().size();
            }
            image.setDisplayOrder(displayOrderValue);
            image.setIsFeatured(Boolean.TRUE.equals(isFeatured));
            
            System.out.println("PlotService: Saving image entity...");
            Image savedImage = imageRepository.save(image);
            System.out.println("PlotService: Image saved with ID: " + savedImage.getId());

            // Update featured image if this is marked as featured
            if (Boolean.TRUE.equals(isFeatured)) {
                plot.setFeaturedImageUrl(uploadResponse.getUrl());
                plotRepository.save(plot);
            }

            return convertImageToDTO(savedImage);
        } catch (Exception e) {
            System.err.println("PlotService: Error uploading image: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    @Transactional
    public void deleteImage(Long imageId) {
        Image image = imageRepository.findById(imageId)
                .orElseThrow(() -> new ResourceNotFoundException("Image", "id", imageId));
        
        // Delete the file from storage
        boolean deleted = fileStorageService.deleteFile(
            image.getCloudinaryPublicId() != null ? 
            image.getCloudinaryPublicId() : 
            image.getImageUrl()
        );
        
        if (!deleted) {
            logger.warn("Failed to delete file for image ID: {}", imageId);
        }
        
        // Remove from any plot's featured image reference if needed
        Plot plot = image.getPlot();
        if (plot != null && plot.getFeaturedImageUrl() != null && 
            plot.getFeaturedImageUrl().equals(image.getImageUrl())) {
            plot.setFeaturedImageUrl(null);
            plotRepository.save(plot);
        }
        
        // Delete the image entity
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

    public String uploadVideo(Long plotId, MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("File cannot be null or empty");
        }

        Plot plot = plotRepository.findById(plotId)
                .orElseThrow(() -> new ResourceNotFoundException("Plot", "id", plotId));

        // Upload the video file to the configured storage
        var uploadResponse = fileStorageService.uploadFile(file, "plots/videos");

        // Update plot with video URL
        plot.setVideoUrl(uploadResponse.getUrl());
        plotRepository.save(plot);

        return uploadResponse.getUrl();
    }
}
