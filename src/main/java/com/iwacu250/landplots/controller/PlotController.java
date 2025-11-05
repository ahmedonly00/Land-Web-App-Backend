package com.iwacu250.landplots.controller;

import com.iwacu250.landplots.dto.PlotDTO;
import com.iwacu250.landplots.service.PlotService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

// BigDecimal replaced with Double
import java.util.List;

@RestController
@RequestMapping("/api/plots")
@CrossOrigin(origins = "*", maxAge = 3600)
public class PlotController {

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
        
        // Default to AVAILABLE status for public endpoint
        String searchStatus = (status != null) ? status : "AVAILABLE";
        
        Page<PlotDTO> plots = plotService.getAllPlots(
                page, size, sortBy, sortDir, searchStatus, 
                location, minPrice, maxPrice, minSize, maxSize);
        return ResponseEntity.ok(plots);
    }

    @GetMapping("/featured")
    public ResponseEntity<List<PlotDTO>> getFeaturedPlots(
            @RequestParam(defaultValue = "6") int limit) {
        List<PlotDTO> plots = plotService.getFeaturedPlots(limit);
        return ResponseEntity.ok(plots);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PlotDTO> getPlotById(@PathVariable Long id) {
        PlotDTO plot = plotService.getPlotById(id);
        return ResponseEntity.ok(plot);
    }
}
