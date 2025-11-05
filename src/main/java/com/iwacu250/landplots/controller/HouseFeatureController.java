package com.iwacu250.landplots.controller;

import com.iwacu250.landplots.dto.HouseFeatureDTO;
import com.iwacu250.landplots.service.HouseFeatureService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/house-features")
public class HouseFeatureController extends BaseController {

    private final HouseFeatureService featureService;

    public HouseFeatureController(HouseFeatureService featureService) {
        this.featureService = featureService;
    }

    @PostMapping
    public ResponseEntity<HouseFeatureDTO> createFeature(@Valid @RequestBody HouseFeatureDTO featureDTO) {
        HouseFeatureDTO createdFeature = featureService.createFeature(featureDTO);
        return created(createdFeature);
    }

    @PutMapping("/{id}")
    public ResponseEntity<HouseFeatureDTO> updateFeature(
            @PathVariable Long id,
            @Valid @RequestBody HouseFeatureDTO featureDTO) {
        featureDTO.setId(id);
        HouseFeatureDTO updatedFeature = featureService.updateFeature(id, featureDTO);
        return ok(updatedFeature);
    }

    @GetMapping("/{id}")
    public ResponseEntity<HouseFeatureDTO> getFeature(@PathVariable Long id) {
        HouseFeatureDTO feature = featureService.getFeatureById(id);
        return ok(feature);
    }

    @GetMapping
    public ResponseEntity<Page<HouseFeatureDTO>> getAllFeatures(Pageable pageable) {
        Page<HouseFeatureDTO> features = featureService.getAllFeatures(pageable);
        return ok(features);
    }

    @GetMapping("/search")
    public ResponseEntity<Page<HouseFeatureDTO>> searchFeatures(
            @RequestParam String query,
            Pageable pageable) {
        Page<HouseFeatureDTO> features = featureService.searchFeatures(query, pageable);
        return ok(features);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFeature(@PathVariable Long id) {
        featureService.deleteFeature(id);
        return noContent();
    }
}
