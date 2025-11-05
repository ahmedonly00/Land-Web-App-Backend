package com.iwacu250.landplots.controller;

import com.iwacu250.landplots.service.SettingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*", maxAge = 3600)
public class SettingsController {

    @Autowired
    private SettingService settingService;

    @GetMapping("/settings/public")
    public ResponseEntity<Map<String, String>> getPublicSettings() {
        Map<String, String> settings = settingService.getPublicSettings();
        return ResponseEntity.ok(settings);
    }

    @GetMapping("/admin/settings")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, String>> getAllSettings() {
        Map<String, String> settings = settingService.getAllSettings();
        return ResponseEntity.ok(settings);
    }

    @PutMapping("/admin/settings")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updateSettings(@RequestBody Map<String, String> settings) {
        for (Map.Entry<String, String> entry : settings.entrySet()) {
            settingService.updateSetting(entry.getKey(), entry.getValue());
        }
        return ResponseEntity.ok().body("{\"message\": \"Settings updated successfully\"}");
    }
}
