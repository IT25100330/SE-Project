package com.company.HireNova.DTO.Employer;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class EmployerResponseDTO {

    private Long employerId;
    private String companyName;
    private String email;
    private String contactNumber;
}
