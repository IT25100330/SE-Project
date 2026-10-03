package com.company.HireNova.Service.Employer;

import com.company.HireNova.DTO.Employer.CompanyProfileDTO;
import com.company.HireNova.Entity.Admin.Industry;
import com.company.HireNova.Entity.Employer.CompanyProfile;
import com.company.HireNova.Entity.Employer.Employer;
import com.company.HireNova.Repository.Admin.IndustryRepository;
import com.company.HireNova.Repository.Employer.CompanyProfileRepository;
import com.company.HireNova.Repository.Employer.EmployerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CompanyProfileService {

    private final CompanyProfileRepository companyProfileRepository;
    private final EmployerRepository employerRepository;
    private final IndustryRepository industryRepository;


    // ==========================================
    // GET PROFILE - EMPLOYER
    // ==========================================

    public CompanyProfileDTO getProfile(String email) {

        Employer employer = employerRepository
                .findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException("Employer not found"));

        CompanyProfile profile =
                companyProfileRepository
                        .findByEmployerEmployerId(employer.getEmployerId())
                        .orElseThrow(() ->
                                new RuntimeException("Company profile not found"));

        return convertToDTO(profile);
    }


    // ==========================================
    // UPDATE PROFILE - EMPLOYER
    // ==========================================

    public CompanyProfileDTO updateProfile(
            String email,
            CompanyProfileDTO dto) {

        Employer employer = employerRepository
                .findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException("Employer not found"));

        CompanyProfile profile =
                companyProfileRepository
                        .findByEmployerEmployerId(employer.getEmployerId())
                        .orElseThrow(() ->
                                new RuntimeException("Company profile not found"));


        profile.setCompanyName(dto.getCompanyName());
        profile.setBrn(dto.getBrn());
        profile.setOrganizationType(dto.getOrganizationType());


        // FIND INDUSTRY
        Industry industry = industryRepository
                .findById(dto.getIndustryId())
                .orElseThrow(() ->
                        new RuntimeException("Industry not found"));

        profile.setIndustry(industry);

        // CHECK INDUSTRY ACTIVE
        if (!industry.isActive()) {
            throw new RuntimeException(
                    "Selected industry is inactive"
            );
        }

        // ASSIGN INDUSTRY
        profile.setIndustry(industry);


        profile.setDescription(dto.getDescription());
        profile.setWebsite(dto.getWebsite());
        profile.setLogo(dto.getLogo());
        profile.setCountry(dto.getCountry());
        profile.setProvince(dto.getProvince());
        profile.setDistrict(dto.getDistrict());
        profile.setCity(dto.getCity());
        profile.setAddress(dto.getAddress());
        profile.setContactPerson(dto.getContactPerson());
        profile.setDesignation(dto.getDesignation());
        profile.setContactEmail(dto.getContactEmail());
        profile.setContactPhone(dto.getContactPhone());


        CompanyProfile updatedProfile =
                companyProfileRepository.save(profile);

        return convertToDTO(updatedProfile);
    }


    // ==========================================
    // DELETE PROFILE - EMPLOYER
    // ==========================================

    public void deleteProfile(String email) {

        Employer employer = employerRepository
                .findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException("Employer not found"));

        CompanyProfile profile =
                employer.getCompanyProfile();

        if (profile == null) {
            throw new RuntimeException(
                    "Company profile not found"
            );
        }

        employer.setCompanyProfile(null);

        employerRepository.save(employer);

        companyProfileRepository.delete(profile);
    }


    // ==========================================
    // ADMIN - GET COMPANY PROFILE
    // ==========================================

    public CompanyProfileDTO getProfileByEmployerId(
            Long employerId) {

        Employer employer =
                employerRepository
                        .findById(employerId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Employer not found"
                                ));

        CompanyProfile profile =
                companyProfileRepository
                        .findByEmployerEmployerId(
                                employer.getEmployerId()
                        )
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Company profile not found"
                                ));

        return convertToDTO(profile);
    }


    // ==========================================
    // ADMIN - UPDATE COMPANY PROFILE
    // ==========================================

    public CompanyProfileDTO updateProfileByEmployerId(
            Long employerId,
            CompanyProfileDTO dto) {

        Employer employer =
                employerRepository
                        .findById(employerId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Employer not found"
                                ));

        CompanyProfile profile =
                companyProfileRepository
                        .findByEmployerEmployerId(
                                employer.getEmployerId()
                        )
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Company profile not found"
                                ));


        profile.setCompanyName(dto.getCompanyName());
        profile.setBrn(dto.getBrn());
        profile.setOrganizationType(dto.getOrganizationType());


        // FIND INDUSTRY
        Industry industry = industryRepository
                .findById(dto.getIndustryId())
                .orElseThrow(() ->
                        new RuntimeException(
                                "Industry not found"
                        ));

        // CHECK INDUSTRY ACTIVE
        if (!industry.isActive()) {
            throw new RuntimeException(
                    "Selected industry is inactive"
            );
        }

        // ASSIGN INDUSTRY
        profile.setIndustry(industry);


        profile.setDescription(dto.getDescription());
        profile.setWebsite(dto.getWebsite());
        profile.setLogo(dto.getLogo());
        profile.setCountry(dto.getCountry());
        profile.setProvince(dto.getProvince());
        profile.setDistrict(dto.getDistrict());
        profile.setCity(dto.getCity());
        profile.setAddress(dto.getAddress());
        profile.setContactPerson(dto.getContactPerson());
        profile.setDesignation(dto.getDesignation());
        profile.setContactEmail(dto.getContactEmail());
        profile.setContactPhone(dto.getContactPhone());


        CompanyProfile updatedProfile =
                companyProfileRepository.save(profile);

        return convertToDTO(updatedProfile);
    }


    // ==========================================
    // CONVERT ENTITY → DTO
    // ==========================================

    private CompanyProfileDTO convertToDTO(
            CompanyProfile profile) {

        CompanyProfileDTO dto =
                new CompanyProfileDTO();

        dto.setCompanyName(
                profile.getCompanyName()
        );

        dto.setBrn(
                profile.getBrn()
        );

        dto.setOrganizationType(
                profile.getOrganizationType()
        );


        // INDUSTRY
        if (profile.getIndustry() != null) {

            dto.setIndustryId(
                    profile.getIndustry()
                            .getIndustryId()
            );

            dto.setIndustry(
                    profile.getIndustry()
                            .getIndustryName()
            );
        }

        // EMPLOYER ID
        if (profile.getEmployer() != null) {

            dto.setEmployerId(
                    profile.getEmployer()
                            .getEmployerId()
            );
        }


        dto.setDescription(
                profile.getDescription()
        );

        dto.setWebsite(
                profile.getWebsite()
        );

        dto.setLogo(
                profile.getLogo()
        );

        dto.setCountry(
                profile.getCountry()
        );

        dto.setProvince(
                profile.getProvince()
        );

        dto.setDistrict(
                profile.getDistrict()
        );

        dto.setCity(
                profile.getCity()
        );

        dto.setAddress(
                profile.getAddress()
        );

        dto.setContactPerson(
                profile.getContactPerson()
        );

        dto.setDesignation(
                profile.getDesignation()
        );

        dto.setContactEmail(
                profile.getContactEmail()
        );

        dto.setContactPhone(
                profile.getContactPhone()
        );


        return dto;
    }
}