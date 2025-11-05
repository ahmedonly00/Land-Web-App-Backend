package com.iwacu250.landplots.controller;

import com.iwacu250.landplots.dto.DashboardStatsDTO;
import com.iwacu250.landplots.entity.Inquiry;
import com.iwacu250.landplots.service.DashboardService;
import com.iwacu250.landplots.service.InquiryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/dashboard")
@CrossOrigin(origins = "*", maxAge = 3600)
@PreAuthorize("hasRole('ADMIN')")
public class DashboardController {

    @Autowired
    private DashboardService dashboardService;

    @Autowired
    private InquiryService inquiryService;

    @GetMapping("/stats")
    public ResponseEntity<DashboardStatsDTO> getDashboardStats() {
        DashboardStatsDTO stats = dashboardService.getDashboardStats();
        return ResponseEntity.ok(stats);
    }

    @GetMapping("/inquiries")
    public ResponseEntity<Page<Inquiry>> getInquiries(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String status) {
        Page<Inquiry> inquiries = inquiryService.getAllInquiries(page, size, status);
        return ResponseEntity.ok(inquiries);
    }
}
