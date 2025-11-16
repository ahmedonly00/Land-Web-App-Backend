package com.iwacu250.landplots.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "features")  // Changed table name to avoid conflict with join table
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class HouseFeature {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @Column(nullable = false, unique = true)
    @EqualsAndHashCode.Include
    private String name;

    @Column(length = 500)
    private String description;

    @Column(length = 50)
    private String icon;
    
    @OneToMany(mappedBy = "feature", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<HouseFeatureJoin> houseJoins = new HashSet<>();
    
    public HouseFeature() {
        // Default constructor
    }
    
    public HouseFeature(String name, String icon, String description) {
        this.name = name;
        this.icon = icon;
        this.description = description;
    }
}
