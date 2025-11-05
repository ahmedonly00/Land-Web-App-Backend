package com.iwacu250.landplots.service;

import com.iwacu250.landplots.dto.DashboardStatsDTO;
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
    private InquiryRepository inquiryRepository;

    @Transactional(readOnly = true)
    public DashboardStatsDTO getDashboardStats() {
        DashboardStatsDTO stats = new DashboardStatsDTO();
        
        stats.setTotalPlots(plotRepository.count());
        stats.setAvailablePlots(plotRepository.countByStatus("AVAILABLE"));
        stats.setSoldPlots(plotRepository.countByStatus("SOLD"));
        stats.setReservedPlots(plotRepository.countByStatus("RESERVED"));
        stats.setTotalInquiries(inquiryRepository.count());
        stats.setNewInquiries(inquiryRepository.countByStatus("NEW"));
        
        return stats;
    }
}
