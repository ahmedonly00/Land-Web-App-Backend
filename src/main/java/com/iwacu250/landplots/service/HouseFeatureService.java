package com.iwacu250.landplots.service;

import com.iwacu250.landplots.dto.HouseFeatureDTO;
import com.iwacu250.landplots.entity.HouseFeature;
import com.iwacu250.landplots.exception.ResourceAlreadyExistsException;
import com.iwacu250.landplots.exception.ResourceNotFoundException;
import com.iwacu250.landplots.mapper.HouseFeatureMapper;
import com.iwacu250.landplots.repository.HouseFeatureRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.lang.NonNull;


@Service
@Transactional
public class HouseFeatureService {

    private final HouseFeatureRepository featureRepository;

    @Autowired
    public HouseFeatureService(HouseFeatureRepository featureRepository) {
        this.featureRepository = featureRepository;
    }

    public HouseFeatureDTO createFeature(HouseFeatureDTO featureDTO) {
        // Check if feature with this name already exists
        if (featureRepository.existsByName(featureDTO.getName())) {
            throw new ResourceAlreadyExistsException("Feature with name " + featureDTO.getName() + " already exists");
        }
        
        HouseFeature feature = HouseFeatureMapper.toEntity(featureDTO);
        if (feature == null) {
            throw new IllegalArgumentException("Failed to map HouseFeatureDTO to entity");
        }
        HouseFeature savedFeature = featureRepository.save(feature);
        return HouseFeatureMapper.toDto(savedFeature);
    }

    public HouseFeatureDTO updateFeature(Long id, HouseFeatureDTO featureDTO) {
        if (id == null) {
            throw new IllegalArgumentException("Feature ID cannot be null");
        }
        
        HouseFeature existingFeature = featureRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Feature not found with id: " + id));
        
        // Check if another feature with the same name exists
        if (!existingFeature.getName().equals(featureDTO.getName()) && 
            featureRepository.existsByName(featureDTO.getName())) {
            throw new ResourceAlreadyExistsException("Another feature with name " + featureDTO.getName() + " already exists");
        }
        
        HouseFeatureMapper.updateEntityFromDto(featureDTO, existingFeature);
        HouseFeature updatedFeature = featureRepository.save(existingFeature);
        return HouseFeatureMapper.toDto(updatedFeature);
    }

    @Transactional(readOnly = true)
    public HouseFeatureDTO getFeatureById(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("Feature ID cannot be null");
        }

        HouseFeature feature = featureRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Feature not found with id: " + id));
        return HouseFeatureMapper.toDto(feature);
    }

    @Transactional(readOnly = true)
    public Page<HouseFeatureDTO> getAllFeatures(Pageable pageable) {
        return featureRepository.findAll(pageable)
            .map(HouseFeatureMapper::toDto);
    }

    @Transactional(readOnly = true)
    public Page<HouseFeatureDTO> searchFeatures(String query, Pageable pageable) {
        Pageable pageRequest = pageable != null ? pageable : Pageable.unpaged();
        return featureRepository.findByNameContainingIgnoreCase(query, pageRequest)
            .map(HouseFeatureMapper::toDto);
    }

    public void deleteFeature(@NonNull Long id) {
        if (!featureRepository.existsById(id)) {
            throw new ResourceNotFoundException("Feature not found with id: " + id);
        }
        featureRepository.deleteById(id);
    }
}
