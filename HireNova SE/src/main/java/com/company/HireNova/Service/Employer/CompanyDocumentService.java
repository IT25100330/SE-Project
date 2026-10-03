package com.company.HireNova.Service.Employer;

import com.company.HireNova.DTO.Employer.CompanyDocumentDTO;
import com.company.HireNova.Entity.Employer.CompanyDocument;
import com.company.HireNova.Entity.Employer.Employer;
import com.company.HireNova.Repository.Employer.CompanyDocumentRepository;
import com.company.HireNova.Repository.Employer.EmployerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CompanyDocumentService {

    private final CompanyDocumentRepository companyDocumentRepository;
    private final EmployerRepository employerRepository;

    // Add Document
    public CompanyDocumentDTO addDocument(
            String email,
            CompanyDocumentDTO dto) {

        Employer employer = employerRepository
                .findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException("Employer not found"));

        CompanyDocument document = new CompanyDocument();

        document.setDocumentName(dto.getDocumentName());
        document.setDocumentType(dto.getDocumentType());
        document.setDocumentUrl(dto.getDocumentUrl());

        document.setUploadedDate(LocalDateTime.now());
        document.setEmployer(employer);

        CompanyDocument savedDocument =
                companyDocumentRepository.save(document);

        return convertToDTO(savedDocument);
    }

    // View Documents
    public List<CompanyDocumentDTO> getDocuments(String email) {

        Employer employer = employerRepository
                .findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException("Employer not found"));

        return companyDocumentRepository
                .findByEmployerEmployerId(employer.getEmployerId())
                .stream()
                .map(this::convertToDTO)
                .toList();
    }

    //Update documents
    public CompanyDocumentDTO updateDocument(
            String email,
            Long documentId,
            CompanyDocumentDTO dto) {

        Employer employer = employerRepository
                .findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException("Employer not found"));

        CompanyDocument document =
                companyDocumentRepository
                        .findById(documentId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Document not found"));

        if (!document.getEmployer()
                .getEmployerId()
                .equals(employer.getEmployerId())) {

            throw new RuntimeException(
                    "You are not authorized to update this document");
        }

        document.setDocumentName(dto.getDocumentName());
        document.setDocumentType(dto.getDocumentType());
        document.setDocumentUrl(dto.getDocumentUrl());

        CompanyDocument updatedDocument =
                companyDocumentRepository.save(document);

        return convertToDTO(updatedDocument);
    }


    //Delete Documents
    public void deleteDocument(
            String email,
            Long documentId) {

        Employer employer = employerRepository
                .findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException("Employer not found"));

        CompanyDocument document =
                companyDocumentRepository
                        .findById(documentId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Document not found"));

        if (!document.getEmployer()
                .getEmployerId()
                .equals(employer.getEmployerId())) {

            throw new RuntimeException(
                    "You are not authorized to delete this document");
        }

        companyDocumentRepository.delete(document);
    }


    // ==========================================
// ADMIN - GET EMPLOYER DOCUMENTS
// ==========================================

    public List<CompanyDocumentDTO> getDocumentsByEmployerId(
            Long employerId) {

        employerRepository.findById(employerId)
                .orElseThrow(() ->
                        new RuntimeException("Employer not found"));

        return companyDocumentRepository
                .findByEmployerEmployerId(employerId)
                .stream()
                .map(this::convertToDTO)
                .toList();
    }


// ==========================================
// ADMIN - UPDATE DOCUMENT
// ==========================================

    public CompanyDocumentDTO updateDocumentByAdmin(
            Long employerId,
            Long documentId,
            CompanyDocumentDTO dto) {

        employerRepository.findById(employerId)
                .orElseThrow(() ->
                        new RuntimeException("Employer not found"));

        CompanyDocument document =
                companyDocumentRepository.findById(documentId)
                        .orElseThrow(() ->
                                new RuntimeException("Document not found"));

        if (!document.getEmployer()
                .getEmployerId()
                .equals(employerId)) {

            throw new RuntimeException(
                    "Document does not belong to this employer");
        }

        document.setDocumentName(dto.getDocumentName());
        document.setDocumentType(dto.getDocumentType());
        document.setDocumentUrl(dto.getDocumentUrl());

        CompanyDocument updatedDocument =
                companyDocumentRepository.save(document);

        return convertToDTO(updatedDocument);
    }


// ==========================================
// ADMIN - DELETE DOCUMENT
// ==========================================

    public void deleteDocumentByAdmin(
            Long employerId,
            Long documentId) {

        employerRepository.findById(employerId)
                .orElseThrow(() ->
                        new RuntimeException("Employer not found"));

        CompanyDocument document =
                companyDocumentRepository.findById(documentId)
                        .orElseThrow(() ->
                                new RuntimeException("Document not found"));

        if (!document.getEmployer()
                .getEmployerId()
                .equals(employerId)) {

            throw new RuntimeException(
                    "Document does not belong to this employer");
        }

        companyDocumentRepository.delete(document);
    }





    private CompanyDocumentDTO convertToDTO(
            CompanyDocument document) {

        CompanyDocumentDTO dto = new CompanyDocumentDTO();

        dto.setDocumentId(document.getDocumentId());
        dto.setDocumentName(document.getDocumentName());
        dto.setDocumentType(document.getDocumentType());
        dto.setDocumentUrl(document.getDocumentUrl());
        dto.setUploadedDate(document.getUploadedDate());


        return dto;
    }
}
