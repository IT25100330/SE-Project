package com.company.HireNova.Repository.Employer;

import com.company.HireNova.Entity.Employer.PremiumPackage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PremiumPackageRepository
        extends JpaRepository<PremiumPackage, Long> {

    // packages an employer can buy, shortest first
    List<PremiumPackage> findByActiveTrueOrderByDurationDaysAsc();

    Optional<PremiumPackage> findByPackageNameIgnoreCase(String packageName);
}