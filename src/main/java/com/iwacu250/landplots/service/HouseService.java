package com.iwacu250.landplots.service;

import com.iwacu250.landplots.dto.HouseDTO;
import com.iwacu250.landplots.dto.ImageDTO;
import com.iwacu250.landplots.entity.House;
import com.iwacu250.landplots.entity.HouseFeature;
import com.iwacu250.landplots.entity.HouseImage;
import com.iwacu250.landplots.entity.PropertyStatus;
import com.iwacu250.landplots.entity.PropertyType;
import com.iwacu250.landplots.exception.ResourceNotFoundException;
import com.iwacu250.landplots.mapper.HouseMapper;
import com.iwacu250.landplots.repository.HouseFeatureRepository;
import com.iwacu250.landplots.repository.HouseRepository;
import com.iwacu250.landplots.repository.HouseImageRepository;
import com.iwacu250.landplots.service.FileStorageService;
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
        
        House savedHouse = houseRepository.save(house);
        System.out.println("Saved house: " + savedHouse);
        
        return HouseMapper.toDto(savedHouse);
    }

    public HouseDTO updateHouse(Long id, HouseDTO houseDTO) {
        if (id == null) {
            throw new IllegalArgumentException("House ID cannot be null");
        }
        
        House existingHouse = houseRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("House not found with id: " + id));
        
        // Update fields using the mapper
        HouseMapper.updateEntityFromDto(houseDTO, existingHouse);
        
        // Handle features
        if (houseDTO.getFeatures() != null) {
            // First, remove all existing features
            existingHouse.getHouseFeatures().clear();
            
            // Then add all features from DTO
            houseDTO.getFeatures().forEach(featureDto -> {
                HouseFeature feature = featureRepository.findByName(featureDto.getName())
                    .orElseGet(() -> {
                        HouseFeature newFeature = new HouseFeature();
                        newFeature.setName(featureDto.getName());
                        newFeature.setDescription(featureDto.getDescription());
                        newFeature.setIcon(featureDto.getIcon());
                        return featureRepository.save(newFeature);
                    });
                existingHouse.addFeature(feature);
            });
        }
        
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
    public Page<HouseDTO> searchHouses(String location, BigDecimal minPrice, BigDecimal maxPrice, 
                                     Integer bedrooms, PropertyType type, PropertyStatus status,
                                     Pageable pageable) {
        // For now, we'll return all houses with pagination
        // In a real application, you would implement the filtering logic here
        // or create a custom repository method with @Query
        return houseRepository.findAll(pageable).map(HouseMapper::toDto);
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

    public void deleteImage(Long imageId) {
        HouseImage houseImage = houseImageRepository.findById(imageId)
                .orElseThrow(() -> new ResourceNotFoundException("HouseImage", "id", imageId));

        // Delete file from storage
        if (houseImage.getCloudinaryPublicId() != null) {
            fileStorageService.deleteFile(houseImage.getCloudinaryPublicId());
        }

        // Delete image record from database
        houseImageRepository.delete(houseImage);
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
