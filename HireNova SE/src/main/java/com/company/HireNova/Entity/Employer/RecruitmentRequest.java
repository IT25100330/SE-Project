package com.company.HireNova.Entity.Employer;

import com.company.HireNova.Entity.Admin.Industry;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "recruitment_requests")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RecruitmentRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long requestId;

    @Column(nullable = false)
    private String jobTitle;

    @Column(nullable = false)
    private Integer numberOfVacancies;

    @Column(nullable = false, length = 2000)
    private String jobDescription;

    private String requiredSkills;

    private String experienceRequired;

    private String salaryRange;

    private String employmentType;

    private String location;

    private String applicationDeadline;

    @Column(nullable = false)
    private boolean premium;

    private LocalDateTime premiumStartDate;

    private LocalDateTime premiumEndDate;

    @ManyToOne
    @JoinColumn(name = "premium_package_id")
    private PremiumPackage premiumPackage;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RequestStatus status;

    @ManyToOne
    @JoinColumn(name = "employer_id", nullable = false)
    private Employer employer;

    // INDUSTRY
    @ManyToOne
    @JoinColumn(name = "industry_id", nullable = false)
    private Industry industry;
}