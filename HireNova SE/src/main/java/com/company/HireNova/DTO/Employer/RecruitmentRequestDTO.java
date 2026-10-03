package com.company.HireNova.DTO.Employer;

import com.company.HireNova.Validation.SalaryRange;
import com.company.HireNova.Validation.ValidDate;
import jakarta.validation.constraints.*;
import lombok.Data;

import static com.company.HireNova.Validation.ValidationRules.*;

import java.time.LocalDateTime;


@Data
public class RecruitmentRequestDTO {

    @NotBlank(message = "Job title is required")
    @Size(min = 3, max = 150, message = "Job title must be 3-150 characters")
    private String jobTitle;

    @NotNull(message = "Number of vacancies is required")
    @Min(value = 1, message = "There must be at least 1 vacancy")
    @Max(value = 100, message = "Vacancies can be at most 100")
    private Integer numberOfVacancies;

    @NotBlank(message = "Job description is required")
    @Size(min = 20, max = 5000, message = "Job description must be 20-5000 characters")
    private String jobDescription;

    @Size(max = 1000, message = "Required skills can be at most 1000 characters")
    private String requiredSkills;

    @Size(max = 100, message = "Experience can be at most 100 characters")
    private String experienceRequired;

    @SalaryRange
    @Size(max = 60, message = "Salary range can be at most 60 characters")
    private String salaryRange;

    @Pattern(regexp = EMPLOYMENT_TYPE, message = "Please choose an employment type from the list")
    private String employmentType;

    @Size(max = 100, message = "Location can be at most 100 characters")
    private String location;

    @ValidDate(future = true, maxMonthsAhead = 6)
    private String applicationDeadline;

    private boolean premium;

    private LocalDateTime premiumStartDate;

    private LocalDateTime premiumEndDate;

    private Long premiumPackageId;

    private Long requestId;

    private String status;

    private Long industryId;
}