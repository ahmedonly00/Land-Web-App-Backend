package com.iwacu250.landplots.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "house_features")
@Data
public class HouseFeature {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(length = 500)
    private String description;

    @Column(length = 50)
    private String icon; // For frontend display

    @ManyToMany(mappedBy = "features")
    private Set<House> houses = new HashSet<>();
}
