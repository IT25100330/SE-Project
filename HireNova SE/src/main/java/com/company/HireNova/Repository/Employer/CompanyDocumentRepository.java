package com.company.HireNova.Repository.Employer;

import com.company.HireNova.Entity.Employer.CompanyDocument;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CompanyDocumentRepository
        extends JpaRepository<CompanyDocument, Long> {

    List<CompanyDocument> findByEmployerEmployerId(Long employerId);
}