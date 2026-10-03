package com.company.HireNova.Entity.Employer;

import com.company.HireNova.Entity.Admin.Industry;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "company_profiles")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CompanyProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long profileId;

    private String companyName;

    private String brn;

    private String organizationType;

    @ManyToOne
    @JoinColumn(name = "industry_id", nullable = false)
    private Industry industry;

    private String description;

    private String website;

    private String logo;

    private String country;

    private String province;

    private String district;

    private String city;

    private String address;

    private String contactPerson;

    private String designation;

    private String contactEmail;

    private String contactPhone;

    @OneToOne
    @JoinColumn(
            name = "employer_id",
            nullable = false,
            unique = true
    )
    private Employer employer;
}