package com.company.HireNova.DTO.Employer;

import jakarta.validation.constraints.*;
import lombok.Data;

import static com.company.HireNova.Validation.ValidationRules.*;


@Data
public class CompanyProfileDTO {

    private Long employerId;

    @NotBlank(message = "Company name is required")
    @Size(min = 2, max = 100, message = "Company name must be 2-100 characters")
    private String companyName;

    @Size(max = 50, message = "Business registration number can be at most 50 characters")
    private String brn;

    @Size(max = 100, message = "Organization type can be at most 100 characters")
    private String organizationType;

    private Long industryId;

    private String industry;

    @Size(max = 2000, message = "Description can be at most 2000 characters")
    private String description;

    @Pattern(regexp = URL_OPTIONAL, message = URL_MSG)
    @Size(max = 255, message = "Website link is too long")
    private String website;

    @Size(max = 500, message = "Logo link is too long")
    private String logo;

    @Size(max = 60, message = "Country can be at most 60 characters")
    private String country;

    @Size(max = 60, message = "Province can be at most 60 characters")
    private String province;

    @Size(max = 60, message = "District can be at most 60 characters")
    private String district;

    @Size(max = 60, message = "City can be at most 60 characters")
    private String city;

    @Size(max = 200, message = "Address can be at most 200 characters")
    private String address;

    @Size(max = 100, message = "Contact person can be at most 100 characters")
    private String contactPerson;

    @Size(max = 100, message = "Designation can be at most 100 characters")
    private String designation;

    @Email(message = EMAIL_MSG)
    @Size(max = 120, message = "Email is too long")
    private String contactEmail;

    @Pattern(regexp = PHONE_OPTIONAL, message = PHONE_MSG)
    private String contactPhone;
}