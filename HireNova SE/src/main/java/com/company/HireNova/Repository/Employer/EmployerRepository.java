package com.company.HireNova.Repository.Employer;

import com.company.HireNova.Entity.Employer.Employer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface EmployerRepository
        extends JpaRepository<Employer, Long> {

    Optional<Employer> findByEmail(String email);

    boolean existsByEmail(String email);

    // Get employers who have recruitment requests
    // belonging to a specific industry
    @Query("""
            SELECT DISTINCT e
            FROM Employer e
            JOIN RecruitmentRequest rr
                ON rr.employer.employerId = e.employerId
            WHERE rr.industry.industryId = :industryId
            """)
    List<Employer> findByIndustryId(Long industryId);
}