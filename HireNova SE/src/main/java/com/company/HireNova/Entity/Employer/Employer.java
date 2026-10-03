package com.company.HireNova.Entity.Employer;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "employers")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Employer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long employerId;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    private String contactNumber;

    @OneToOne(
            mappedBy = "employer",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private CompanyProfile companyProfile;

    private boolean active;
}