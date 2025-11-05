package com.iwacu250.landplots.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/config")
public class ConfigController {

    @Value("${DB_HOST:Not_Found}")
    private String dbHost;

    @Value("${SERVER_IP:Not_Found}")
    private String serverIp;

    @Value("${FRONTEND_URL:Not_Found}")
    private String frontendUrl;

    @Value("${spring.application.name:landplots}")
    private String appName;

    @GetMapping("/check")
    public ResponseEntity<Map<String, String>> checkConfig() {
        Map<String, String> config = new HashMap<>();
        config.put("application", appName);
        config.put("status", "active");
        config.put("db_host", dbHost);
        config.put("server_ip", serverIp);
        config.put("frontend_url", frontendUrl);
        
        // Add more configurations as needed
        config.putAll(System.getenv());
        
        return ResponseEntity.ok(config);
    }
}
