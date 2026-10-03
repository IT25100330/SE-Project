package com.company.HireNova.Repository.Employer;

import com.company.HireNova.Entity.Employer.CompanyProfile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CompanyProfileRepository
        extends JpaRepository<CompanyProfile, Long> {

    Optional<CompanyProfile> findByEmployerEmployerId(Long employerId);
}