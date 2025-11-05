package com.iwacu250.landplots.service;

import com.iwacu250.landplots.dto.InquiryDTO;
import com.iwacu250.landplots.model.Inquiry;
import com.iwacu250.landplots.model.Plot;
import com.iwacu250.landplots.repository.InquiryRepository;
import com.iwacu250.landplots.repository.PlotRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class InquiryService {

    @Autowired
    private InquiryRepository inquiryRepository;

    @Autowired
    private PlotRepository plotRepository;

    @Transactional
    public Inquiry createInquiry(InquiryDTO inquiryDTO) {
        Inquiry inquiry = new Inquiry();
        inquiry.setName(inquiryDTO.getName());
        inquiry.setEmail(inquiryDTO.getEmail());
        inquiry.setPhone(inquiryDTO.getPhone());
        inquiry.setMessage(inquiryDTO.getMessage());
        inquiry.setStatus("NEW");

        if (inquiryDTO.getPlotId() != null) {
            Plot plot = plotRepository.findById(inquiryDTO.getPlotId()).orElse(null);
            inquiry.setPlot(plot);
        }

        return inquiryRepository.save(inquiry);
    }

    @Transactional(readOnly = true)
    public Page<Inquiry> getAllInquiries(int page, int size, String status) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        
        if (status != null && !status.isEmpty()) {
            return inquiryRepository.findByStatus(status, pageable);
        }
        
        return inquiryRepository.findAll(pageable);
    }

    @Transactional(readOnly = true)
    public Long countInquiriesByStatus(String status) {
        return inquiryRepository.countByStatus(status);
    }
}
