package com.company.HireNova.DTO.Employer;

import jakarta.validation.constraints.*;
import lombok.Data;

import static com.company.HireNova.Validation.ValidationRules.*;

import java.time.LocalDateTime;

@Data
public class CompanyDocumentDTO {

    private Long DocumentId ;

    @NotBlank(message = "Document name is required")
    @Size(max = 100, message = "Document name can be at most 100 characters")
    private String documentName;

    @NotBlank(message = "Document type is required")
    @Size(max = 50, message = "Document type can be at most 50 characters")
    private String documentType;

    @NotBlank(message = "Document link is required")
    @Pattern(regexp = URL, message = URL_MSG)
    @Size(max = 500, message = "Document link is too long")
    private String documentUrl;

    private LocalDateTime uploadedDate;
}