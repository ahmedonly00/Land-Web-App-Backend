package com.iwacu250.landplots.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.HashMap;
import java.util.Map;

public class BaseController {
    
    @GetMapping("/health")
    @ResponseStatus(HttpStatus.OK)
    public Map<String, String> healthCheck() {
        Map<String, String> response = new HashMap<>();
        response.put("status", "UP");
        response.put("service", this.getClass().getSimpleName().replace("Controller", ""));
        return response;
    }
    
    protected <T> ResponseEntity<T> created(T body) {
        return ResponseEntity.status(HttpStatus.CREATED).body(body);
    }
    
    protected <T> ResponseEntity<T> ok(T body) {
        return ResponseEntity.ok(body);
    }
    
    protected ResponseEntity<Void> noContent() {
        return ResponseEntity.noContent().build();
    }
}
