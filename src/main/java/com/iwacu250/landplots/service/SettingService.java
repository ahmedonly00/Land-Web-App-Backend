package com.iwacu250.landplots.service;

import com.iwacu250.landplots.entity.Setting;
import com.iwacu250.landplots.repository.SettingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class SettingService {

    @Autowired
    private SettingRepository settingRepository;

    @Transactional(readOnly = true)
    public String getSettingValue(String key) {
        return settingRepository.findBySettingKey(key)
                .map(Setting::getSettingValue)
                .orElse(null);
    }

    @Transactional(readOnly = true)
    public Map<String, String> getAllSettings() {
        try {
            System.out.println("SettingService: Starting getAllSettings...");
            List<Setting> settings = settingRepository.findAll();
            System.out.println("SettingService: Found " + settings.size() + " settings");
            Map<String, String> settingsMap = new HashMap<>();
            for (Setting setting : settings) {
                settingsMap.put(setting.getSettingKey(), setting.getSettingValue());
            }
            System.out.println("SettingService: Successfully created settings map with " + settingsMap.size() + " entries");
            return settingsMap;
        } catch (Exception e) {
            System.err.println("SettingService: Error in getAllSettings: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    @Transactional(readOnly = true)
    public Map<String, String> getPublicSettings() {
        Map<String, String> publicSettings = new HashMap<>();
        publicSettings.put("whatsapp_number", getSettingValue("whatsapp_number"));
        publicSettings.put("company_name", getSettingValue("company_name"));
        publicSettings.put("company_email", getSettingValue("company_email"));
        publicSettings.put("company_address", getSettingValue("company_address"));
        publicSettings.put("company_phone", getSettingValue("company_phone"));
        return publicSettings;
    }

    @Transactional
    public Setting updateSetting(String key, String value) {
        Setting setting = settingRepository.findBySettingKey(key)
                .orElse(new Setting(key, value));
        setting.setSettingValue(value);
        return settingRepository.save(setting);
    }

    @Transactional
    public void initializeDefaultSettings() {
        if (settingRepository.count() == 0) {
            settingRepository.save(new Setting("whatsapp_number", "+250780314239"));
            settingRepository.save(new Setting("company_name", "Iwacu 250"));
            settingRepository.save(new Setting("company_email", "karimukanakuze2050@gmail.com"));
            settingRepository.save(new Setting("company_address", "Kigali, Rwanda"));
            settingRepository.save(new Setting("company_phone", "+250780314239"));
        }
    }
}

