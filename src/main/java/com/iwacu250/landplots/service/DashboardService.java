package com.iwacu250.landplots.service;

import com.iwacu250.landplots.dto.DashboardStatsDTO;
import com.iwacu250.landplots.entity.PropertyStatus;
import com.iwacu250.landplots.repository.HouseRepository;
import com.iwacu250.landplots.repository.InquiryRepository;
import com.iwacu250.landplots.repository.PlotRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DashboardService {

    @Autowired
    private PlotRepository plotRepository;

    @Autowired
    private HouseRepository houseRepository;

    @Autowired
    private InquiryRepository inquiryRepository;

    @Transactional(readOnly = true)
    public DashboardStatsDTO getDashboardStats() {
        DashboardStatsDTO stats = new DashboardStatsDTO();
        
        // Plot statistics
        stats.setTotalPlots(plotRepository.count());
        stats.setAvailablePlots(plotRepository.countByStatus(PropertyStatus.AVAILABLE));
        stats.setSoldPlots(plotRepository.countByStatus(PropertyStatus.SOLD));
        stats.setReservedPlots(plotRepository.countByStatus(PropertyStatus.PENDING)); // Using PENDING instead of RESERVED
        
        // House statistics
        stats.setTotalHouses(houseRepository.count());
        stats.setAvailableHouses(houseRepository.countByStatus(PropertyStatus.AVAILABLE));
        stats.setSoldHouses(houseRepository.countByStatus(PropertyStatus.SOLD));
        stats.setReservedHouses(houseRepository.countByStatus(PropertyStatus.PENDING)); // Using PENDING instead of RESERVED
        
        // Inquiry statistics
        stats.setTotalInquiries(inquiryRepository.count());
        stats.setNewInquiries(inquiryRepository.countByStatus("NEW"));
        
        return stats;
    }
}
