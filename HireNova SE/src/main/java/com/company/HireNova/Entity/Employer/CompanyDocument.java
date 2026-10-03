package com.company.HireNova.Entity.Employer;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "company_documents")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CompanyDocument {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long documentId;

    private String documentName;

    private String documentType;

    private String documentUrl;

    private LocalDateTime uploadedDate;

    @ManyToOne
    @JoinColumn(name = "employer_id", nullable = false)
    private Employer employer;
}