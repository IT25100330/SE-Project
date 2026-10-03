package com.company.HireNova.Service.Employer;

import com.company.HireNova.DTO.Employer.EmployerLoginDTO;
import com.company.HireNova.DTO.Employer.EmployerRegistrationDTO;
import com.company.HireNova.DTO.Employer.EmployerResponseDTO;
import com.company.HireNova.Entity.Admin.Industry;
import com.company.HireNova.Entity.Employer.CompanyProfile;
import com.company.HireNova.Entity.Notification.EmailVerification;
import com.company.HireNova.Entity.Employer.Employer;
import com.company.HireNova.Exception.DuplicateEmailException;
import com.company.HireNova.Exception.FieldValidationException;
import com.company.HireNova.Repository.Admin.IndustryRepository;
import com.company.HireNova.Repository.Employer.EmployerRepository;
import com.company.HireNova.Service.Notification.EmailService;
import com.company.HireNova.Service.Notification.OtpService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.company.HireNova.Security.JwtUtil;

@Service
@RequiredArgsConstructor
public class EmployerService {

    private final EmployerRepository employerRepository;
    private final PasswordEncoder passwordEncoder;

    private final IndustryRepository industryRepository;
    private final OtpService otpService;
    private final EmailService emailService;
    private final JwtUtil jwtUtil;


    public EmployerResponseDTO registerEmployer(EmployerRegistrationDTO dto) {

        if (employerRepository.existsByEmail(dto.getEmail())) {
            throw new DuplicateEmailException("Email already registered");
        }

        if (!dto.getPassword().equals(dto.getConfirmPassword())) {
            throw new FieldValidationException("confirmPassword", "Passwords do not match");
        }

        if (!otpService.isEmailVerified(dto.getEmail())) {
            throw new RuntimeException(
                    "Please verify your email before registering"
            );
        }

        // 1. Create Employer account
        Employer employer = new Employer();

        employer.setEmail(dto.getEmail());
        employer.setPassword(
                passwordEncoder.encode(dto.getPassword())
        );
        employer.setContactNumber(dto.getContactNumber());
        employer.setActive(true);


        // 2. Create Company Profile
        CompanyProfile companyProfile = new CompanyProfile();

        companyProfile.setCompanyName(dto.getCompanyName());

// Find selected industry
        Industry industry = industryRepository
                .findById(dto.getIndustryId())
                .orElseThrow(() ->
                        new RuntimeException("Industry not found")
                );

// Link company profile with industry
        companyProfile.setIndustry(industry);

// Link profile with employer
        companyProfile.setEmployer(employer);

// Link employer with profile
        employer.setCompanyProfile(companyProfile);


        // 3. Save Employer + CompanyProfile
        Employer savedEmployer = employerRepository.save(employer);


        // 4. Return response
        return new EmployerResponseDTO(
                savedEmployer.getEmployerId(),
                companyProfile.getCompanyName(),
                savedEmployer.getEmail(),
                savedEmployer.getContactNumber()
        );
    }

    public String loginEmployer(EmployerLoginDTO dto) {

        Employer employer = employerRepository
                .findByEmail(dto.getEmail())
                .orElseThrow(() ->
                        new RuntimeException("Invalid email or password"));

        if (!passwordEncoder.matches(
                dto.getPassword(),
                employer.getPassword())) {

            throw new RuntimeException("Invalid email or password");
        }

        // SECURITY FIX (H1): a deactivated employer can no longer log in
        // (checked after the password, so the status isn't revealed to strangers)
        if (!employer.isActive()) {
            throw new RuntimeException("Employer account is inactive. Please contact the administrator.");
        }

//        String token =
//                jwtUtil.generateToken(
//                        employer.getEmail(),
//                        "EMPLOYER"
//                );
//
//        return jwtUtil.generateToken(employer.getEmail());

//        //Changed 16 2.14
        String token =
                jwtUtil.generateToken(
                        employer.getEmail(),
                        "EMPLOYER"
                );

        return token;
    }

    public void sendVerificationCode(String email) {

        if (employerRepository.existsByEmail(email)) {
            throw new DuplicateEmailException("Email already registered");
        }

        EmailVerification verification = otpService.generateOtp(email);

        emailService.sendOtpEmail(
                email,
                verification.getVerificationCode()
        );
    }

}