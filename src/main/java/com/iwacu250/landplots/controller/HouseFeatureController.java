package com.iwacu250.landplots.controller;

import com.iwacu250.landplots.dto.HouseFeatureDTO;
import com.iwacu250.landplots.service.HouseFeatureService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.lang.NonNull;

import org.springframework.web.util.UriComponentsBuilder;

@RestController
@RequestMapping("/api/house-features")
@CrossOrigin(origins = {"https://iwacu250.com/", "https://www.iwacu250.com/"})
@Validated
public class HouseFeatureController extends BaseController {

    private final HouseFeatureService featureService;

    public HouseFeatureController(HouseFeatureService featureService) {
        this.featureService = featureService;
    }

    @PostMapping(value = "/createFeature")
    public ResponseEntity<HouseFeatureDTO> createFeature(
            @Valid @RequestBody HouseFeatureDTO featureDTO,
            UriComponentsBuilder uriComponentsBuilder) {
        HouseFeatureDTO createdFeature = featureService.createFeature(featureDTO);
        
        return ResponseEntity
                .created(uriComponentsBuilder
                    .path("/api/house-features/{id}")
                    .buildAndExpand(createdFeature.getId())
                    .toUri())
                .body(createdFeature);
    }

    @PutMapping(value = "/updateFeature/{id}")
    public ResponseEntity<HouseFeatureDTO> updateFeature(
            @PathVariable @Min(value = 1, message = "ID must be a positive number") Long id,
            @Valid @RequestBody HouseFeatureDTO featureDTO) {
        // Ensure the ID in the path matches the ID in the DTO if present
        if (featureDTO.getId() != null && !featureDTO.getId().equals(id)) {
            throw new IllegalArgumentException("ID in path does not match ID in request body");
        }
        featureDTO.setId(id);
        HouseFeatureDTO updatedFeature = featureService.updateFeature(id, featureDTO);
        return ok(updatedFeature);
    }

    @GetMapping(value = "/getFeature/{id}")
    public ResponseEntity<HouseFeatureDTO> getFeature(
            @PathVariable @Min(value = 1, message = "ID must be a positive number") Long id) {
        HouseFeatureDTO feature = featureService.getFeatureById(id);
        return ok(feature);
    }

    @GetMapping(value = "/getAllFeatures")
    public ResponseEntity<Page<HouseFeatureDTO>> getAllFeatures(Pageable pageable) {
        Page<HouseFeatureDTO> features = featureService.getAllFeatures(pageable);
        return ok(features);
    }

    @GetMapping(value = "/searchFeatures")
    public ResponseEntity<Page<HouseFeatureDTO>> searchFeatures(
            @RequestParam String query,
            Pageable pageable) {
        Page<HouseFeatureDTO> features = featureService.searchFeatures(query, pageable);
        return ok(features);
    }

    @DeleteMapping(value = "/deleteFeature/{id}")
    public ResponseEntity<Void> deleteFeature(
            @PathVariable @NonNull @Min(value = 1, message = "ID must be a positive number") Long id) {
        featureService.deleteFeature(id);
        return noContent();
    }
}
