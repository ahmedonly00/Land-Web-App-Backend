package com.iwacu250.landplots.controller;

import com.iwacu250.landplots.dto.HouseDTO;
import com.iwacu250.landplots.dto.ImageDTO;
import com.iwacu250.landplots.entity.PropertyType;
import com.iwacu250.landplots.entity.PropertyStatus;
import com.iwacu250.landplots.service.HouseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.Valid;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/houses")
@CrossOrigin(origins = "*")
public class AdminHouseController extends BaseController {

    private final HouseService houseService;

    @Autowired
    public AdminHouseController(HouseService houseService) {
        this.houseService = houseService;
    }

    @PostMapping(value = "/createHouse")
    public ResponseEntity<HouseDTO> createHouse(@Valid @RequestBody HouseDTO houseDTO) {
        try {
            System.out.println("AdminHouseController: Received request to create house: " + houseDTO);
            HouseDTO createdHouse = houseService.createHouse(houseDTO);
            System.out.println("AdminHouseController: House created successfully: " + createdHouse);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdHouse);
        } catch (Exception e) {
            System.err.println("AdminHouseController: Error creating house: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    @PutMapping(value = "/updateHouse/{id}")
    public ResponseEntity<HouseDTO> updateHouse(
            @PathVariable Long id,
            @Valid @RequestBody HouseDTO houseDTO) {
        try {
            System.out.println("AdminHouseController: Received update request for house ID: " + id);
            System.out.println("AdminHouseController: HouseDTO features: " + houseDTO.getFeatures());
            if (houseDTO.getFeatures() != null) {
                System.out.println("AdminHouseController: Features type: " + houseDTO.getFeatures().getClass().getName());
            }
            
            houseDTO.setId(id);
            HouseDTO updatedHouse = houseService.updateHouse(id, houseDTO);
            return ResponseEntity.ok(updatedHouse);
        } catch (Exception e) {
            System.err.println("AdminHouseController: Error updating house: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    @GetMapping(value = "/getHouseById/{id}")
    public ResponseEntity<HouseDTO> getHouse(@PathVariable Long id) {
        HouseDTO house = houseService.getHouseById(id);
        return ResponseEntity.ok(house);
    }

    @GetMapping(value = "/getAllHouses")
    public ResponseEntity<Page<HouseDTO>> getAllHouses(
            @PageableDefault(size = 10) Pageable pageable,
            @RequestParam(required = false) String location,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(required = false) Integer bedrooms,
            @RequestParam(required = false) PropertyType type,
            @RequestParam(required = false) PropertyStatus status) {
        
        if (location != null || minPrice != null || maxPrice != null || 
            bedrooms != null || type != null || status != null) {
            // Use search if any filter is provided
            return ResponseEntity.ok(houseService.searchHouses(location, minPrice, maxPrice, bedrooms, type, status, pageable));
        }
        
        // Otherwise, return all houses with pagination
        return ResponseEntity.ok(houseService.getAllHouses(pageable));
    }

    @DeleteMapping(value = "/deleteHouse/{id}")
    public ResponseEntity<Void> deleteHouse(@PathVariable Long id) {
        houseService.deleteHouse(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping(value = "/searchHouses")
    public ResponseEntity<Page<HouseDTO>> searchHouses(
            @RequestParam(required = false) String location,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(required = false) Integer bedrooms,
            @RequestParam(required = false) PropertyType type,
            @RequestParam(required = false) PropertyStatus status,
            @PageableDefault(size = 10) Pageable pageable) {
        
        Page<HouseDTO> results = houseService.searchHouses(
            location, minPrice, maxPrice, bedrooms, type, status, pageable);
        return ResponseEntity.ok(results);
    }

    @PostMapping(value = "/uploadImage/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ImageDTO> uploadImage(
            @PathVariable Long id,
            @RequestParam("file") MultipartFile file,
            @RequestParam(required = false) Integer displayOrder,
            @RequestParam(required = false) Boolean isFeatured) throws IOException {
        
        try {
            System.out.println("AdminHouseController: Received image upload request for house ID: " + id);
            System.out.println("AdminHouseController: File name: " + file.getOriginalFilename());
            System.out.println("AdminHouseController: File size: " + file.getSize());
            System.out.println("AdminHouseController: Content type: " + file.getContentType());
            
            ImageDTO imageDTO = houseService.uploadImage(id, file, displayOrder, isFeatured);
            System.out.println("AdminHouseController: Image uploaded successfully");
            return ResponseEntity.status(HttpStatus.CREATED).body(imageDTO);
        } catch (Exception e) {
            System.err.println("AdminHouseController: Error uploading image: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    @PostMapping(value = "/uploadVideo/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Map<String, String>> uploadVideo(
            @PathVariable Long id,
            @RequestParam("file") MultipartFile file) throws IOException {
        
        String videoUrl = houseService.uploadVideo(id, file);
        Map<String, String> response = new HashMap<>();
        response.put("videoUrl", videoUrl);
        response.put("message", "Video uploaded successfully");
        return ResponseEntity.ok(response);
    }

    @DeleteMapping(value = "/deleteImage/{imageId}")
    public ResponseEntity<Map<String, String>> deleteImage(@PathVariable Long imageId) {
        houseService.deleteImage(imageId);
        Map<String, String> response = new HashMap<>();
        response.put("message", "Image deleted successfully");
        return ResponseEntity.ok(response);
    }

    @PutMapping(value = "/updateHouseStatus/{id}")
    public ResponseEntity<HouseDTO> updateHouseStatus(
            @PathVariable Long id,
            @RequestBody Map<String, String> statusUpdate) {
        
        String status = statusUpdate.get("status");
        HouseDTO updatedHouse = houseService.updateHouseStatus(id, status);
        return ResponseEntity.ok(updatedHouse);
    }
}
