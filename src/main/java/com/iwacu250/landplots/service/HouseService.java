package com.iwacu250.landplots.service;

import com.iwacu250.landplots.dto.HouseDTO;
import com.iwacu250.landplots.entity.House;
import com.iwacu250.landplots.entity.HouseFeature;
import com.iwacu250.landplots.entity.PropertyStatus;
import com.iwacu250.landplots.entity.PropertyType;
import com.iwacu250.landplots.exception.ResourceNotFoundException;
import com.iwacu250.landplots.repository.HouseFeatureRepository;
import com.iwacu250.landplots.repository.HouseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
public class HouseService {

    private final HouseRepository houseRepository;
    private final HouseFeatureRepository featureRepository;

    @Autowired
    public HouseService(HouseRepository houseRepository, 
                       HouseFeatureRepository featureRepository) {
        this.houseRepository = houseRepository;
        this.featureRepository = featureRepository;

    }

    public HouseDTO createHouse(HouseDTO houseDTO) {
        House house = convertToEntity(houseDTO);
        House savedHouse = houseRepository.save(house);
        return convertToDto(savedHouse);
    }

    public HouseDTO updateHouse(Long id, HouseDTO houseDTO) {
        House existingHouse = houseRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("House not found with id: " + id));
        
        // Update fields
        modelMapper.map(houseDTO, existingHouse);
        
        // Handle features
        if (houseDTO.getFeatures() != null) {
            Set<HouseFeature> features = houseDTO.getFeatures().stream()
                .map(featureDto -> featureRepository.findById(featureDto.getId())
                    .orElseGet(() -> {
                        HouseFeature newFeature = new HouseFeature();
                        newFeature.setName(featureDto.getName());
                        newFeature.setDescription(featureDto.getDescription());
                        newFeature.setIcon(featureDto.getIcon());
                        return featureRepository.save(newFeature);
                    }))
                .collect(Collectors.toSet());
            existingHouse.setFeatures(features);
        }
        
        House updatedHouse = houseRepository.save(existingHouse);
        return convertToDto(updatedHouse);
    }

    @Transactional(readOnly = true)
    public HouseDTO getHouseById(Long id) {
        House house = houseRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("House not found with id: " + id));
        return convertToDto(house);
    }

    @Transactional(readOnly = true)
    public Page<HouseDTO> getAllHouses(Pageable pageable) {
        return houseRepository.findAll(pageable)
            .map(this::convertToDto);
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
        return houseRepository.findAll((root, query, criteriaBuilder) -> {
            var predicates = new java.util.ArrayList<jakarta.persistence.criteria.Predicate>();
            
            if (location != null && !location.isEmpty()) {
                predicates.add(criteriaBuilder.like(
                    criteriaBuilder.lower(root.get("location")), 
                    "%" + location.toLowerCase() + "%"
                ));
            }
            
            if (minPrice != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("price"), minPrice));
            }
            
            if (maxPrice != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("price"), maxPrice));
            }
            
            if (bedrooms != null) {
                predicates.add(criteriaBuilder.equal(root.get("bedrooms"), bedrooms));
            }
            
            if (type != null) {
                predicates.add(criteriaBuilder.equal(root.get("type"), type));
            }
            
            if (status != null) {
                predicates.add(criteriaBuilder.equal(root.get("status"), status));
            }
            
            return criteriaBuilder.and(predicates.toArray(new jakarta.persistence.criteria.Predicate[0]));
        }, pageable)
        .map(this::convertToDto);
    }

    // Helper methods for conversion
    private HouseDTO convertToDto(House house) {
        HouseDTO dto = modelMapper.map(house, HouseDTO.class);
        dto.setImageUrls(house.getImages().stream()
            .map(HouseImage::getImageUrl)
            .collect(Collectors.toSet()));
        return dto;
    }

    private House convertToEntity(HouseDTO dto) {
        House house = modelMapper.map(dto, House.class);
        
        // Handle features
        if (dto.getFeatures() != null) {
            Set<HouseFeature> features = dto.getFeatures().stream()
                .map(featureDto -> modelMapper.map(featureDto, HouseFeature.class))
                .collect(Collectors.toSet());
            house.setFeatures(features);
        }
        
        return house;
    }
}
