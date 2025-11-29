package com.iwacu250.landplots.service;

import com.iwacu250.landplots.dto.HouseDTO;
import com.iwacu250.landplots.dto.ImageDTO;
import com.iwacu250.landplots.entity.House;
import com.iwacu250.landplots.entity.HouseFeature;
import com.iwacu250.landplots.entity.HouseFeatureJoin;
import com.iwacu250.landplots.entity.HouseImage;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import com.iwacu250.landplots.entity.PropertyStatus;
import com.iwacu250.landplots.entity.PropertyType;
import com.iwacu250.landplots.exception.ResourceNotFoundException;
import com.iwacu250.landplots.mapper.HouseMapper;
import com.iwacu250.landplots.repository.HouseFeatureRepository;
import com.iwacu250.landplots.repository.HouseRepository;
import com.iwacu250.landplots.repository.HouseImageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
@Transactional
public class HouseService {

    private final HouseRepository houseRepository;
    private final HouseFeatureRepository featureRepository;
    private final HouseImageRepository houseImageRepository;
    private final FileStorageService fileStorageService;

    @Autowired
    public HouseService(HouseRepository houseRepository, 
                       HouseFeatureRepository featureRepository,
                       HouseImageRepository houseImageRepository,
                       FileStorageService fileStorageService) {
        this.houseRepository = houseRepository;
        this.featureRepository = featureRepository;
        this.houseImageRepository = houseImageRepository;
        this.fileStorageService = fileStorageService;
    }

    public HouseDTO createHouse(HouseDTO houseDTO) {
        System.out.println("Creating house with DTO: " + houseDTO);
        System.out.println("Title: " + houseDTO.getTitle());
        System.out.println("Location: " + houseDTO.getLocation());
        System.out.println("Size: " + houseDTO.getSize());
        System.out.println("Price: " + houseDTO.getPrice());
        System.out.println("Type: " + houseDTO.getType());
        System.out.println("Status: " + houseDTO.getStatus());
        
        House house = HouseMapper.toEntity(houseDTO);
        System.out.println("Converted to entity: " + house);
        
        try {
            House savedHouse = houseRepository.save(house);
            System.out.println("Saved house: " + savedHouse);
            return HouseMapper.toDto(savedHouse);
        } catch (Exception e) {
            System.err.println("Error saving house: " + e.getMessage());
            throw new RuntimeException("Failed to save house", e);
        }
    }

    @Transactional
    public HouseDTO updateHouse(Long id, HouseDTO houseDTO) {
        if (id == null) {
            throw new IllegalArgumentException("House ID cannot be null");
        }
        
        // Load the existing house with its features in a single query
        House existingHouse = houseRepository.findByIdWithFeatures(id)
            .orElseThrow(() -> new ResourceNotFoundException("House not found with id: " + id));
        
        // Update basic fields using the mapper
        HouseMapper.updateEntityFromDto(houseDTO, existingHouse);
        
        // Handle features if they are provided in the DTO
        if (houseDTO.getFeatures() != null) {
            // First, collect all features to remove
            Set<HouseFeature> featuresToRemove = new HashSet<>();
            for (HouseFeatureJoin join : existingHouse.getHouseFeatures().values()) {
                boolean existsInNewList = houseDTO.getFeatures().stream()
                    .anyMatch(dto -> dto.getName().equals(join.getFeature().getName()));
                
                if (!existsInNewList) {
                    featuresToRemove.add(join.getFeature());
                }
            }
            
            // Remove features not in the new list
            featuresToRemove.forEach(existingHouse::removeFeature);
            
            // Then add or update features from the DTO
            for (com.iwacu250.landplots.dto.HouseFeatureDTO featureDto : houseDTO.getFeatures()) {
                boolean exists = existingHouse.getHouseFeatures().values().stream()
                    .anyMatch(join -> join.getFeature().getName().equals(featureDto.getName()));
                
                if (!exists) {
                    // Try to find an existing feature with the same name
                    HouseFeature feature = featureRepository.findByName(featureDto.getName())
                        .orElseGet(() -> {
                            // Create a new feature if it doesn't exist
                            HouseFeature newFeature = new HouseFeature();
                            newFeature.setName(featureDto.getName());
                            newFeature.setDescription(featureDto.getDescription());
                            newFeature.setIcon(featureDto.getIcon());
                            return featureRepository.save(newFeature);
                        });
                    
                    // Add the feature to the house
                    existingHouse.addFeature(feature);
                }
            }
        }
        
        // Save the updated house
        House updatedHouse = houseRepository.save(existingHouse);
        return HouseMapper.toDto(updatedHouse);
    }

    @Transactional(readOnly = true)
    public HouseDTO getHouseById(Long id) {
        House house = houseRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("House not found with id: " + id));
        return HouseMapper.toDto(house);
    }

    @Transactional(readOnly = true)
    public Page<HouseDTO> getAllHouses(Pageable pageable) {
        return houseRepository.findAll(pageable)
            .map(HouseMapper::toDto);
    }

    public void deleteHouse(Long id) {
        if (!houseRepository.existsById(id)) {
            throw new ResourceNotFoundException("House not found with id: " + id);
        }
        houseRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public Page<HouseDTO> searchHouses(String location, Double minPrice, Double maxPrice, 
                                     Integer bedrooms, PropertyType type, PropertyStatus status,
                                     Pageable pageable) {
        // Validate price range
        if (minPrice != null && maxPrice != null && minPrice > maxPrice) {
            throw new IllegalArgumentException("Minimum price cannot be greater than maximum price");
        }
        
        // Convert empty string to null for location
        String locationParam = (location != null && location.trim().isEmpty()) ? null : location;
        
        // Execute the search with all parameters
        return houseRepository.searchHouses(
            locationParam,
            minPrice,
            maxPrice,
            bedrooms,
            type,
            status,
            pageable
        ).map(HouseMapper::toDto);
    }

    public ImageDTO uploadImage(Long houseId, MultipartFile file, Integer displayOrder, Boolean isFeatured) throws IOException {
        try {
            System.out.println("HouseService: Starting image upload for house ID: " + houseId);
            System.out.println("HouseService: File name: " + file.getOriginalFilename());
            System.out.println("HouseService: File size: " + file.getSize());
            System.out.println("HouseService: Content type: " + file.getContentType());
            
            if (file == null || file.isEmpty()) {
                throw new IllegalArgumentException("File cannot be null or empty");
            }

            House house = houseRepository.findById(houseId)
                    .orElseThrow(() -> new ResourceNotFoundException("House", "id", houseId));
            
            System.out.println("HouseService: Found house: " + house.getTitle());

            // Upload the file to the configured storage
            var uploadResponse = fileStorageService.uploadFile(file, "houses");
            System.out.println("HouseService: File uploaded successfully: " + uploadResponse.getUrl());

            // Create and save the house image entity
            HouseImage houseImage = new HouseImage();
            houseImage.setHouse(house);
            houseImage.setImageUrl(uploadResponse.getUrl());
            houseImage.setCloudinaryPublicId(uploadResponse.getPublicId());
            houseImage.setDisplayOrder(displayOrder != null ? displayOrder : 0);
            houseImage.setIsFeatured(isFeatured != null ? isFeatured : false);
            houseImage.setUploadedAt(LocalDateTime.now()); // Set uploadedAt timestamp

            System.out.println("HouseService: Saving house image entity...");
            HouseImage savedImage = houseImageRepository.save(houseImage);
            System.out.println("HouseService: House image saved with ID: " + savedImage.getId());

            if (savedImage.getIsFeatured() || house.getFeaturedImageUrl() == null || house.getFeaturedImageUrl().isEmpty()) {
                house.setFeaturedImageUrl(savedImage.getImageUrl());
                houseRepository.save(house);
                System.out.println("HouseService: Updated house featured_image_url: " + savedImage.getImageUrl());
            }
            
            // Convert to ImageDTO for response
            ImageDTO imageDTO = new ImageDTO();
            imageDTO.setId(savedImage.getId());
            imageDTO.setImageUrl(savedImage.getImageUrl());
            imageDTO.setDisplayOrder(savedImage.getDisplayOrder());
            imageDTO.setIsFeatured(savedImage.getIsFeatured());
            
            return imageDTO;
        } catch (Exception e) {
            System.err.println("HouseService: Error uploading image: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    public String uploadVideo(Long houseId, MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("File cannot be null or empty");
        }

        House house = houseRepository.findById(houseId)
                .orElseThrow(() -> new ResourceNotFoundException("House", "id", houseId));

        // Upload the video file to the configured storage
        var uploadResponse = fileStorageService.uploadFile(file, "houses/videos");

        // Update house with video URL
        house.setVideoUrl(uploadResponse.getUrl());
        houseRepository.save(house);

        return uploadResponse.getUrl();
    }

    // public void deleteImage(Long imageId) {
    //     HouseImage houseImage = houseImageRepository.findById(imageId)
    //             .orElseThrow(() -> new ResourceNotFoundException("HouseImage", "id", imageId));

    //     // Delete file from storage
    //     if (houseImage.getCloudinaryPublicId() != null) {
    //         fileStorageService.deleteFile(houseImage.getCloudinaryPublicId());
    //     }

    //     // Delete image record from database
    //     houseImageRepository.delete(houseImage);
    // }

    public void deleteImage(Long imageId) {
        HouseImage houseImage = houseImageRepository.findById(imageId)
                .orElseThrow(() -> new ResourceNotFoundException("HouseImage", "id", imageId));

        House house = houseImage.getHouse();
        String deletedImageUrl = houseImage.getImageUrl();

        // Delete file from storage
        if (houseImage.getCloudinaryPublicId() != null) {
            fileStorageService.deleteFile(houseImage.getCloudinaryPublicId());
        }

        // Delete image record from database
        houseImageRepository.delete(houseImage);

        if (deletedImageUrl.equals(house.getFeaturedImageUrl())) {
            // Find another image for this house
            List<HouseImage> remainingImages = houseImageRepository.findByHouseIdOrderByDisplayOrder(house.getId());
            if (!remainingImages.isEmpty()) {
                house.setFeaturedImageUrl(remainingImages.get(0).getImageUrl());
            } else {
                house.setFeaturedImageUrl(null);
            }
            houseRepository.save(house);
            System.out.println("HouseService: Updated featured image after deletion");
        }
    }

    public HouseDTO updateHouseStatus(Long id, String status) {
        House house = houseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("House", "id", id));

        try {
            PropertyStatus newStatus = PropertyStatus.valueOf(status.toUpperCase());
            house.setStatus(newStatus);
            House updatedHouse = houseRepository.save(house);
            return HouseMapper.toDto(updatedHouse);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid status value: " + status);
        }
    }

}
