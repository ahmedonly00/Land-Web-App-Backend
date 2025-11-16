package com.iwacu250.landplots.controller;

import com.iwacu250.landplots.dto.ImageDTO;
import com.iwacu250.landplots.dto.PlotDTO;
import com.iwacu250.landplots.dto.PlotRequestDTO;
import com.iwacu250.landplots.service.PlotService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.lang.NonNull;
import org.springframework.transaction.annotation.Transactional;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/plots")
@CrossOrigin(origins = "*")
public class AdminPlotController {

    @Autowired
    private PlotService plotService;

    @GetMapping(value = "/getAllPlots")
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
        
        try {
            System.out.println("AdminPlotController: Getting all plots with params: page=" + page + ", size=" + size + ", status=" + status);
            Page<PlotDTO> plots = plotService.getAllPlots(
                    page, size, sortBy, sortDir, status, 
                    location, minPrice, maxPrice, minSize, maxSize);
            System.out.println("AdminPlotController: Successfully retrieved " + plots.getContent().size() + " plots");
            return ResponseEntity.ok(plots);
        } catch (Exception e) {
            System.err.println("AdminPlotController: Error getting plots: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    @GetMapping(value = "/getPlotById/{id}")
    public ResponseEntity<PlotDTO> getPlotById(@PathVariable Long id) {
        try {
            System.out.println("AdminPlotController: Getting plot by id: " + id);
            PlotDTO plot = plotService.getPlotById(id);
            System.out.println("AdminPlotController: Successfully retrieved plot: " + plot.getTitle());
            return ResponseEntity.ok(plot);
        } catch (Exception e) {
            System.err.println("AdminPlotController: Error getting plot by id: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    @PostMapping(value = "/createPlot")
    public ResponseEntity<PlotDTO> createPlot(@Valid @RequestBody PlotRequestDTO plotRequestDTO) {
        PlotDTO createdPlot = plotService.createPlot(plotRequestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdPlot);
    }

    @PutMapping(value = "/updatePlot/{id}")
    public ResponseEntity<PlotDTO> updatePlot(
            @PathVariable Long id,
            @Valid @RequestBody PlotRequestDTO plotRequestDTO) {
        PlotDTO updatedPlot = plotService.updatePlot(id, plotRequestDTO);
        return ResponseEntity.ok(updatedPlot);
    }

    @DeleteMapping(value = "/deletePlot/{id}")
    public ResponseEntity<?> deletePlot(@PathVariable @NonNull Long id) {
        plotService.deletePlot(id);
        Map<String, String> response = new HashMap<>();
        response.put("message", "Plot deleted successfully");
        return ResponseEntity.ok(response);
    }

    @PatchMapping(value = "/updatePlotStatus/{id}")
    public ResponseEntity<PlotDTO> updatePlotStatus(
            @PathVariable Long id,
            @RequestBody Map<String, String> statusUpdate) {
        String status = statusUpdate.get("status");
        PlotDTO updatedPlot = plotService.updatePlotStatus(id, status);
        return ResponseEntity.ok(updatedPlot);
    }

    @GetMapping("/test")
    public ResponseEntity<String> test() {
        return ResponseEntity.ok("AdminPlotController is working!");
    }

    @PostMapping(value = "/uploadImage/{id}")
    @Transactional
    public ResponseEntity<ImageDTO> uploadImage(
            @PathVariable Long id,
            @RequestParam("file") MultipartFile file,
            @RequestParam(required = false) Integer displayOrder,
            @RequestParam(required = false) Boolean isFeatured) throws IOException {
        
        try {
            System.out.println("AdminPlotController: Received image upload request for plot ID: " + id);
            System.out.println("AdminPlotController: File name: " + file.getOriginalFilename());
            System.out.println("AdminPlotController: File size: " + file.getSize());
            System.out.println("AdminPlotController: Content type: " + file.getContentType());
            
            ImageDTO imageDTO = plotService.uploadImage(id, file, displayOrder, isFeatured);
            System.out.println("AdminPlotController: Image uploaded successfully");
            return ResponseEntity.status(HttpStatus.CREATED).body(imageDTO);
        } catch (Exception e) {
            System.err.println("AdminPlotController: Error uploading image: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    @DeleteMapping(value = "/deleteImage/{imageId}")
    public ResponseEntity<?> deleteImage(@PathVariable Long imageId) {
        plotService.deleteImage(imageId);
        Map<String, String> response = new HashMap<>();
        response.put("message", "Image deleted successfully");
        return ResponseEntity.ok(response);
    }

    @PostMapping(value = "/uploadVideo/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Map<String, String>> uploadVideo(
            @PathVariable Long id,
            @RequestParam("file") MultipartFile file) throws IOException {
        
        String videoUrl = plotService.uploadVideo(id, file);
        Map<String, String> response = new HashMap<>();
        response.put("videoUrl", videoUrl);
        response.put("message", "Video uploaded successfully");
        return ResponseEntity.ok(response);
    }

    @PutMapping(value = "/reorderImages/{id}")
    public ResponseEntity<?> reorderImages(
            @PathVariable Long id,
            @RequestBody List<Long> imageIds) {
        
        plotService.reorderImages(id, imageIds);
        Map<String, String> response = new HashMap<>();
        response.put("message", "Images reordered successfully");
        return ResponseEntity.ok(response);
    }
}
