package com.iwacu250.landplots.controller;

import com.iwacu250.landplots.dto.HouseDTO;
import com.iwacu250.landplots.entity.PropertyStatus;
import com.iwacu250.landplots.entity.PropertyType;
import com.iwacu250.landplots.service.HouseService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/houses")
@CrossOrigin(origins = "*")
public class HouseController extends BaseController {

    private final HouseService houseService;

    public HouseController(HouseService houseService) {
        this.houseService = houseService;
    }

    @PostMapping(value = "/createHouse")
    public ResponseEntity<HouseDTO> createHouse(@Valid @RequestBody HouseDTO houseDTO) {
        HouseDTO createdHouse = houseService.createHouse(houseDTO);
        return created(createdHouse);
    }

    @PutMapping(value = "/updateHouse/{id}")
    public ResponseEntity<HouseDTO> updateHouse(
            @PathVariable Long id,
            @Valid @RequestBody HouseDTO houseDTO) {
        houseDTO.setId(id);
        HouseDTO updatedHouse = houseService.updateHouse(id, houseDTO);
        return ok(updatedHouse);
    }

    @GetMapping(value = "/getHouseById/{id}")
    public ResponseEntity<HouseDTO> getHouse(@PathVariable Long id) {
        HouseDTO house = houseService.getHouseById(id);
        return ok(house);
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
            return ok(houseService.searchHouses(location, minPrice, maxPrice, bedrooms, type, status, pageable));
        }
        
        // Otherwise, return all houses with pagination
        return ok(houseService.getAllHouses(pageable));
    }

    @DeleteMapping(value = "/deleteHouse/{id}")
    public ResponseEntity<Void> deleteHouse(@PathVariable Long id) {
        houseService.deleteHouse(id);
        return noContent();
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
        return ok(results);
    }
}
