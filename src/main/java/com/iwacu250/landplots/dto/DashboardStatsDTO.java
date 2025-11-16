package com.iwacu250.landplots.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DashboardStatsDTO {
    // Plot statistics
    private Long totalPlots;
    private Long availablePlots;
    private Long soldPlots;
    private Long reservedPlots;
    
    // House statistics
    private Long totalHouses;
    private Long availableHouses;
    private Long soldHouses;
    private Long reservedHouses;
    
    // Inquiry statistics
    private Long totalInquiries;
    private Long newInquiries;
}
