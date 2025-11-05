package com.iwacu250.landplots.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DashboardStatsDTO {
    private Long totalPlots;
    private Long availablePlots;
    private Long soldPlots;
    private Long reservedPlots;
    private Long totalInquiries;
    private Long newInquiries;
}
