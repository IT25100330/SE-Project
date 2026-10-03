package com.company.HireNova.DTO.Employer;

import jakarta.validation.constraints.*;
import lombok.Data;

import static com.company.HireNova.Validation.ValidationRules.*;

@Data
public class EmployerRegistrationDTO {

    @NotBlank(message = "Company name is required")
    @Size(min = 2, max = 100, message = "Company name must be 2-100 characters")
    private String companyName;

    @NotBlank(message = "Email is required")
    @Email(message = EMAIL_MSG)
    @Size(max = 120, message = "Email is too long")
    private String email;

    @NotBlank(message = "Password is required")
    @Pattern(regexp = PASSWORD, message = PASSWORD_MSG)
    private String password;

    @NotBlank(message = "Please confirm your password")
    private String confirmPassword;

    @NotBlank(message = "Contact number is required")
    @Pattern(regexp = PHONE, message = PHONE_MSG)
    private String contactNumber;

    @NotNull(message = "Please select your industry")
    private Long industryId;
}