package com.iwacu250.landplots.controller;

import com.iwacu250.landplots.dto.ImageDTO;
import com.iwacu250.landplots.dto.PlotDTO;
import com.iwacu250.landplots.dto.PlotRequestDTO;
import com.iwacu250.landplots.service.PlotService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.lang.NonNull;

import java.io.IOException;
// BigDecimal replaced with Double
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/plots")
@CrossOrigin(origins = "*", maxAge = 3600)
@PreAuthorize("hasRole('ADMIN')")
public class AdminPlotController {

    @Autowired
    private PlotService plotService;

    @GetMapping
    public ResponseEntity<Page<PlotDTO>> getAllPlots(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String location,
            @RequestParam(required = false) Double minPrice,
            @RequestParam(required = false) Double maxPrice,
            @RequestParam(required = false) Double minSize,
            @RequestParam(required = false) Double maxSize) {
        
        Page<PlotDTO> plots = plotService.getAllPlots(
                page, size, sortBy, sortDir, status, 
                location, minPrice, maxPrice, minSize, maxSize);
        return ResponseEntity.ok(plots);
    }

    @PostMapping
    public ResponseEntity<PlotDTO> createPlot(@Valid @RequestBody PlotRequestDTO plotRequestDTO) {
        PlotDTO createdPlot = plotService.createPlot(plotRequestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdPlot);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PlotDTO> updatePlot(
            @PathVariable Long id,
            @Valid @RequestBody PlotRequestDTO plotRequestDTO) {
        PlotDTO updatedPlot = plotService.updatePlot(id, plotRequestDTO);
        return ResponseEntity.ok(updatedPlot);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletePlot(@PathVariable @NonNull Long id) {
        plotService.deletePlot(id);
        Map<String, String> response = new HashMap<>();
        response.put("message", "Plot deleted successfully");
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<PlotDTO> updatePlotStatus(
            @PathVariable Long id,
            @RequestBody Map<String, String> statusUpdate) {
        String status = statusUpdate.get("status");
        PlotDTO updatedPlot = plotService.updatePlotStatus(id, status);
        return ResponseEntity.ok(updatedPlot);
    }

    @PostMapping("/{id}/images")
    public ResponseEntity<ImageDTO> uploadImage(
            @PathVariable Long id,
            @RequestParam("file") MultipartFile file,
            @RequestParam(required = false) Integer displayOrder,
            @RequestParam(required = false) Boolean isFeatured) throws IOException {
        
        ImageDTO imageDTO = plotService.uploadImage(id, file, displayOrder, isFeatured);
        return ResponseEntity.status(HttpStatus.CREATED).body(imageDTO);
    }

    @DeleteMapping("/images/{imageId}")
    public ResponseEntity<?> deleteImage(@PathVariable Long imageId) {
        plotService.deleteImage(imageId);
        Map<String, String> response = new HashMap<>();
        response.put("message", "Image deleted successfully");
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}/images/reorder")
    public ResponseEntity<?> reorderImages(
            @PathVariable Long id,
            @RequestBody List<Long> imageIds) {
        plotService.reorderImages(id, imageIds);
        Map<String, String> response = new HashMap<>();
        response.put("message", "Images reordered successfully");
        return ResponseEntity.ok(response);
    }
}
