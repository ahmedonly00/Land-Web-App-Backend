package com.iwacu250.landplots.controller;

import com.iwacu250.landplots.dto.PlotDTO;
import com.iwacu250.landplots.service.PlotService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/plots")
@CrossOrigin(origins = {"https://iwacu250.com/", "https://www.iwacu250.com/"})
public class PlotController {

    @Autowired
    private PlotService plotService;

    @GetMapping(value = {"", "/getAllPlots"})
    public ResponseEntity<Page<PlotDTO>> getAllPlots(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String location,
            @RequestParam(required = false) Double minPrice,
            @RequestParam(required = false) Double maxPrice,
            @RequestParam(required = false) Double minSize,
            @RequestParam(required = false) Double maxSize) {
        
        try {
            // Validate sort direction
            String direction = "desc".equalsIgnoreCase(sortDir) ? "desc" : "asc";
            
            // Default to AVAILABLE status for public endpoint
            String searchStatus = (status != null) ? status : "AVAILABLE";
            
            Page<PlotDTO> plots = plotService.getAllPlots(
                    page, size, sortBy, direction, searchStatus, 
                    location, minPrice, maxPrice, minSize, maxSize);
            return ResponseEntity.ok(plots);
        } catch (Exception e) {
            // Fallback to default sorting if there's an error
            Page<PlotDTO> plots = plotService.getAllPlots(
                    page, size, "id", "desc", "AVAILABLE", 
                    location, minPrice, maxPrice, minSize, maxSize);
            return ResponseEntity.ok(plots);
        }
    }

    @GetMapping(value = {"/featured", "/getFeaturedPlots"})
    public ResponseEntity<List<PlotDTO>> getFeaturedPlots(
            @RequestParam(defaultValue = "6") int limit) {
        List<PlotDTO> plots = plotService.getFeaturedPlots(limit);
        return ResponseEntity.ok(plots);
    }

    @GetMapping(value = {"/{id}", "/getPlotById/{id}"})
    public ResponseEntity<PlotDTO> getPlotById(@PathVariable Long id) {
        PlotDTO plot = plotService.getPlotById(id);
        return ResponseEntity.ok(plot);
    }
}
