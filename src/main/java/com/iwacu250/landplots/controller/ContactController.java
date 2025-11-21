package com.iwacu250.landplots.controller;

import com.iwacu250.landplots.dto.InquiryDTO;
import com.iwacu250.landplots.entity.Inquiry;
import com.iwacu250.landplots.service.InquiryService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/contact")
@CrossOrigin(origins = {"https://iwacu250.com/", "https://www.iwacu250.com/"})
public class ContactController {

    @Autowired
    private InquiryService inquiryService;

    @PostMapping(value = "/submitInquiry")
    public ResponseEntity<?> submitInquiry(@Valid @RequestBody InquiryDTO inquiryDTO) {
        Inquiry inquiry = inquiryService.createInquiry(inquiryDTO);
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Your inquiry has been submitted successfully. We will contact you soon.");
        response.put("inquiryId", inquiry.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
