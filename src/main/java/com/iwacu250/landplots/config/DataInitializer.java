package com.iwacu250.landplots.config;

import com.iwacu250.landplots.entity.Feature;
import com.iwacu250.landplots.entity.House;
import com.iwacu250.landplots.entity.HouseImage;
import com.iwacu250.landplots.entity.*;
import com.iwacu250.landplots.repository.*;
import com.iwacu250.landplots.service.SettingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.core.env.Environment;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.HashSet;

@Component
public class DataInitializer implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(DataInitializer.class);

    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private PlotRepository plotRepository;
    
    @Autowired
    private HouseRepository houseRepository;
    
    @Autowired
    private FeatureRepository featureRepository;
    
    @Autowired
    private HouseFeatureRepository houseFeatureRepository;
    
    @Autowired
    private ImageRepository imageRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private SettingService settingService;
    
    @Value("${ADMIN_USERNAME:admin}")
    private String adminUsername;
    
    @Value("${ADMIN_EMAIL:admin@example.com}")
    private String adminEmail;
    
    @Value("${ADMIN_PASSWORD:Admin@123}")
    private String adminPassword;
    
    @Value("${ADMIN_ROLE:ADMIN}")
    private String adminRole;

    @Override
    public void run(String... args) throws Exception {
        // Create default admin user if not exists
        if (userRepository.count() == 0) {
            createAdminUser();
            createSamplePlots();
            createSampleHouses();
            logger.info("Sample data initialized successfully");
        }

        // Initialize default settings
        settingService.initializeDefaultSettings();
        logger.info("Application initialized successfully");
    }
    
    private void createAdminUser() {
        try {
            if (adminUsername == null || adminEmail == null || adminPassword == null || adminRole == null) {
                throw new IllegalStateException("One or more admin environment variables are not set");
            }
            
            if (userRepository.existsByUsername(adminUsername) || userRepository.existsByEmail(adminEmail)) {
                logger.warn("Admin user already exists, skipping creation");
                return;
            }
            
            User admin = new User();
            admin.setUsername(adminUsername);
            admin.setEmail(adminEmail);
            admin.setPasswordHash(passwordEncoder.encode(adminPassword));
            // Convert role string to Role enum
            try {
                Role role = Role.valueOf(adminRole.toUpperCase());
                admin.setRoles(Set.of(role));
            } catch (IllegalArgumentException e) {
                logger.warn("Invalid role: {}. Defaulting to USER", adminRole);
                admin.setRoles(Set.of(Role.USER));
            }
            userRepository.save(admin);
            
            logger.info("Admin user created successfully - Username: {}", adminUsername);
            logger.debug("Admin email: {}", adminEmail);
            logger.debug("Admin role: {}", adminRole);
        } catch (Exception e) {
            logger.error("Failed to create admin user: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to initialize admin user", e);
        }
    }
    
    private void createSamplePlots() {
        if (plotRepository.count() == 0) {
            // Create plot 1
            Plot plot1 = new Plot();
            plot1.setTitle("Prime Residential Plot in Kigali");
            plot1.setLocation("Kigali, Kicukiro");
            plot1.setSize(500.0);
            plot1.setSizeUnit("sqm");
            plot1.setPrice(50000.0);
            plot1.setDescription("Beautiful plot in a quiet neighborhood with good road access and utilities nearby.");
            plot1.setStatus(PropertyStatus.AVAILABLE.name());
            plot1.setType(PropertyType.LAND.name()); // Changed from RESIDENTIAL to LAND since RESIDENTIAL is not a valid PropertyType
            plot1.setCreatedAt(LocalDateTime.now());
            
            // Add images to plot 1
            Image plot1Image1 = createImage("plot1_1.jpg", "Main view of the plot");
            Image plot1Image2 = createImage("plot1_2.jpg", "Road access view");
            plot1.setImages(new ArrayList<>(Arrays.asList(plot1Image1, plot1Image2)));
            
            // Create plot 2
            Plot plot2 = new Plot();
            plot2.setTitle("Commercial Plot in City Center");
            plot2.setLocation("Kigali, Nyarugenge");
            plot2.setSize(1000.0);
            plot2.setSizeUnit("sqm");
            plot2.setPrice(150000.0);
            plot2.setDescription("Prime commercial plot in the heart of the city, perfect for business development.");
            plot2.setStatus(PropertyStatus.AVAILABLE.name());
            plot2.setType(PropertyType.COMMERCIAL.name());
            plot2.setCreatedAt(LocalDateTime.now());
            
            // Add images to plot 2
            Image plot2Image1 = createImage("plot2_1.jpg", "Front view of the commercial plot");
            plot2.setImages(new ArrayList<>(Collections.singletonList(plot2Image1)));
            
            plotRepository.saveAll(Arrays.asList(plot1, plot2));
            logger.info("Sample plots created successfully");
        }
    }
    
    private void createSampleHouses() {
        if (houseRepository.count() == 0) {
            // Create house features if they don't exist
            Feature bedroom = createFeatureIfNotExists("Bedroom", "Number of bedrooms");
            Feature bathroom = createFeatureIfNotExists("Bathroom", "Number of bathrooms");
            Feature parking = createFeatureIfNotExists("Parking", "Parking spaces");
            Feature furnished = createFeatureIfNotExists("Furnished", "Fully furnished");
            Feature garden = createFeatureIfNotExists("Garden", "Private garden");
            
            // Save features to ensure they have IDs
            featureRepository.saveAll(Arrays.asList(bedroom, bathroom, parking, furnished, garden));
            
            // Create house 1
            House house1 = new House();
            house1.setTitle("Modern 3-Bedroom Villa");
            house1.setLocation("Kigali, Kiyovu");
            house1.setSize(250.0);
            house1.setPrice(350000.0);
            house1.setDescription("Beautiful modern villa with 3 bedrooms, 3 bathrooms, and a garden.");
            house1.setStatus(PropertyStatus.AVAILABLE.name());
            house1.setType(PropertyType.HOUSE.name()); // Changed from RESIDENTIAL to HOUSE
            // house1.setBuiltYear(2020); // Commented out as the method doesn't exist
            house1.setCreatedAt(LocalDateTime.now());
            
            // Save house1 first to get an ID
            house1 = houseRepository.save(house1);
            
            // Create house features for house 1
            createHouseFeature(house1, bedroom, "3");
            createHouseFeature(house1, bathroom, "3");
            createHouseFeature(house1, parking, "2");
            createHouseFeature(house1, furnished, "Yes");
            createHouseFeature(house1, garden, "Yes");
            
            // Add images to house 1
            HouseImage house1Image1 = createHouseImage("house1_1.jpg", "Front view of the villa", house1);
            HouseImage house1Image2 = createHouseImage("house1_2.jpg", "Living room", house1);
            HouseImage house1Image3 = createHouseImage("house1_3.jpg", "Kitchen", house1);
            house1.setImages(new ArrayList<>(Arrays.asList(house1Image1, house1Image2, house1Image3)));
            
            // Save house1 with images
            houseRepository.save(house1);
            
            // Create house 2
            House house2 = new House();
            house2.setTitle("Luxury Apartment with City View");
            house2.setLocation("Kigali, Nyarutarama");
            house2.setSize(180.0);
            house2.setPrice(280000.0);
            house2.setDescription("Luxury apartment with 2 bedrooms and amazing city views.");
            house2.setStatus(PropertyStatus.AVAILABLE.name());
            house2.setType(PropertyType.APARTMENT.name());
            // house2.setBuiltYear(2021); // Commented out as the method doesn't exist
            house2.setCreatedAt(LocalDateTime.now());
            
            // Save house2 first to get an ID
            house2 = houseRepository.save(house2);
            
            // Create house features for house 2
            createHouseFeature(house2, bedroom, "2");
            createHouseFeature(house2, bathroom, "2");
            createHouseFeature(house2, parking, "1");
            createHouseFeature(house2, furnished, "Semi-furnished");
            
            // Add images to house 2
            HouseImage house2Image1 = createHouseImage("house2_1.jpg", "Apartment building", house2);
            HouseImage house2Image2 = createHouseImage("house2_2.jpg", "Living area", house2);
            house2.setImages(new ArrayList<>(Arrays.asList(house2Image1, house2Image2)));
            
            houseRepository.saveAll(Arrays.asList(house1, house2));
            logger.info("Sample houses created successfully");
        }
    }
    
    private Feature createFeatureIfNotExists(String name, String description) {
        return featureRepository.findByName(name)
                .orElseGet(() -> {
                    Feature feature = new Feature();
                    feature.setName(name);
                    feature.setDescription(description);
                    return featureRepository.save(feature);
                });
    }
    
    private Image createImage(String imageUrl, String altText) {
        Image image = new Image();
        image.setImageUrl(imageUrl);
        image.setAltText(altText);
        return imageRepository.save(image);
    }
    
    private HouseImage createHouseImage(String imageUrl, String altText, House house) {
        HouseImage image = new HouseImage();
        image.setImageUrl(imageUrl);
        image.setAltText(altText);
        image.setHouse(house);
        return houseImageRepository.save(image);
    }
    
    private void createHouseFeature(House house, Feature feature, String value) {
        HouseFeature houseFeature = new HouseFeature();
        houseFeature.setName(feature.getName() + " - " + value);
        houseFeature.setDescription(feature.getDescription() + ": " + value);
        
        // Add the house to the feature's houses set
        Set<House> houses = new HashSet<>();
        houses.add(house);
        houseFeature.setHouses(houses);
        
        // Save the house feature
        houseFeatureRepository.save(houseFeature);
    }
}
