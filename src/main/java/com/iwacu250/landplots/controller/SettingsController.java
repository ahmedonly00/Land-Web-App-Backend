package com.iwacu250.landplots.controller;

import com.iwacu250.landplots.service.SettingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/settings")
@CrossOrigin(origins = {"https://iwacu250.com/", "https://www.iwacu250.com/"})
public class SettingsController {

    @Autowired
    private SettingService settingService;

    @GetMapping(value = "/getPublicSettings")
    public ResponseEntity<Map<String, String>> getPublicSettings() {
        Map<String, String> settings = settingService.getPublicSettings();
        return ResponseEntity.ok(settings);
    }

    @GetMapping(value = "/public")
    public ResponseEntity<Map<String, String>> getPublicSettingsAlt() {
        Map<String, String> settings = settingService.getPublicSettings();
        return ResponseEntity.ok(settings);
    }

    @GetMapping(value = "/getAllSettings")
    public ResponseEntity<Map<String, String>> getAllSettings() {
        try {
            System.out.println("SettingsController: Received request for getAllSettings");
            Map<String, String> settings = settingService.getAllSettings();
            System.out.println("SettingsController: Successfully retrieved settings");
            return ResponseEntity.ok(settings);
        } catch (Exception e) {
            System.err.println("SettingsController: Error in getAllSettings: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    @PutMapping(value = "/updateSettings")
    public ResponseEntity<?> updateSettings(@RequestBody Map<String, String> settings) {
        for (Map.Entry<String, String> entry : settings.entrySet()) {
            settingService.updateSetting(entry.getKey(), entry.getValue());
        }
        return ResponseEntity.ok().body("{\"message\": \"Settings updated successfully\"}");
    }
}
