package com.company.HireNova.Entity.Employer;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "premium_packages")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PremiumPackage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long packageId;

    @Column(nullable = false, unique = true)
    private String packageName;

    @Column(nullable = false)
    private Integer durationDays;

    @Column(nullable = false)
    private Double price;

    @Column(nullable = false)
    private boolean active;
}