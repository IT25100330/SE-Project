package com.company.HireNova.Repository.Employer;

import com.company.HireNova.Entity.Employer.RecruitmentRequest;
import com.company.HireNova.Entity.Employer.RequestStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface RecruitmentRequestRepository
        extends JpaRepository<RecruitmentRequest, Long> {

    List<RecruitmentRequest> findByEmployerEmployerId(Long employerId);

    List<RecruitmentRequest> findByStatus(RequestStatus status);

    // Industry-based filtering
    List<RecruitmentRequest> findByIndustry_IndustryId(Long industryId);

    // Industry + status filtering
    List<RecruitmentRequest> findByIndustry_IndustryIdAndStatus(
            Long industryId,
            RequestStatus status
    );

    // Premium requests whose Premium period has ended (used by PremiumExpiryJob)
    List<RecruitmentRequest> findByPremiumTrueAndPremiumEndDateBefore(LocalDateTime time);
}