package com.iwacu250.landplots.service;

import com.iwacu250.landplots.dto.HouseDTO;
import com.iwacu250.landplots.entity.House;
import com.iwacu250.landplots.entity.HouseFeature;
import com.iwacu250.landplots.entity.PropertyStatus;
import com.iwacu250.landplots.entity.PropertyType;
import com.iwacu250.landplots.exception.ResourceNotFoundException;
import com.iwacu250.landplots.mapper.HouseMapper;
import com.iwacu250.landplots.repository.HouseFeatureRepository;
import com.iwacu250.landplots.repository.HouseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
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
        House house = HouseMapper.toEntity(houseDTO);
        House savedHouse = houseRepository.save(house);
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

}
