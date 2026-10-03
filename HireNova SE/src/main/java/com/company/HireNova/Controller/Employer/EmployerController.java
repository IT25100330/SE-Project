package com.company.HireNova.Controller.Employer;

import com.company.HireNova.DTO.Notification.SendVerificationDTO;
import com.company.HireNova.DTO.Employer.*;
import com.company.HireNova.DTO.Notification.VerifyOtpDTO;
import com.company.HireNova.Entity.Employer.PremiumPackage;
import com.company.HireNova.Repository.Employer.PremiumPackageRepository;
import com.company.HireNova.Service.Employer.*;
import com.company.HireNova.Service.Notification.OtpService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.security.core.Authentication;
import com.company.HireNova.DTO.Employer.EmployerRecommendedCandidateResponseDTO;
import java.util.List;



@RestController
@RequestMapping("/api/employers")
@RequiredArgsConstructor
public class EmployerController {

    private final EmployerService employerService;
    private final OtpService otpService;
    private final CompanyDocumentService companyDocumentService;
    private final CompanyProfileService companyProfileService;
    private final RecruitmentRequestService recruitmentRequestService;
    private final PremiumPackageRepository premiumPackageRepository;
    private final EmployerDashboardService employerDashboardService;


    // Employer Details
    @PostMapping("/register")
    public ResponseEntity<EmployerResponseDTO> registerEmployer(
            @Valid @RequestBody EmployerRegistrationDTO dto) {

        EmployerResponseDTO response = employerService.registerEmployer(dto);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @PostMapping("/verify")
    public ResponseEntity<String> verifyOtp(
            @Valid @RequestBody VerifyOtpDTO dto) {

        otpService.verifyOtp(dto);

        return ResponseEntity.ok("Email verified successfully");
    }


    @PostMapping("/send-verification")
    public ResponseEntity<String> sendVerificationCode(
            @Valid @RequestBody SendVerificationDTO dto) {

        employerService.sendVerificationCode(dto.getEmail());

        return ResponseEntity.ok(
                "Verification code sent to your email"
        );
    }

    @PostMapping("/login")
    public ResponseEntity<String> loginEmployer(
            @Valid @RequestBody EmployerLoginDTO dto) {

        String token = employerService.loginEmployer(dto);

        return ResponseEntity.ok(token);
    }

    @GetMapping("/test-auth")
    public ResponseEntity<String> testAuthentication() {

        return ResponseEntity.ok("JWT authentication successful");

    }

    @GetMapping("/profile")
    public ResponseEntity<?> getCompanyProfile(
            Authentication authentication) {

        try {

            String email = authentication.getName();

            CompanyProfileDTO profile =
                    companyProfileService.getProfile(email);

            return ResponseEntity.ok(profile);

        } catch (Exception e) {

            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("ERROR: " + e.getClass().getName()
                            + " - " + e.getMessage());
        }
    }


    @PutMapping("/profile")
    public ResponseEntity<CompanyProfileDTO> updateCompanyProfile(
            Authentication authentication,
            @Valid @RequestBody CompanyProfileDTO dto) {

        String email = authentication.getName();

        CompanyProfileDTO updatedProfile =
                companyProfileService.updateProfile(email, dto);

        return ResponseEntity.ok(updatedProfile);
    }

    @DeleteMapping("/profile")
    public ResponseEntity<String> deleteCompanyProfile(
            Authentication authentication) {

        String email = authentication.getName();

        companyProfileService.deleteProfile(email);

        return ResponseEntity.ok(
                "Company profile deleted successfully"
        );
    }

    // Documents
    @PostMapping("/documents")
    public ResponseEntity<CompanyDocumentDTO> addDocument(
            Authentication authentication,
            @Valid @RequestBody CompanyDocumentDTO dto) {

        String email = authentication.getName();

        CompanyDocumentDTO document =
                companyDocumentService.addDocument(email, dto);

        return ResponseEntity.ok(document);
    }


    @GetMapping("/documents")
    public ResponseEntity<List<CompanyDocumentDTO>> getDocuments(
            Authentication authentication) {

        String email = authentication.getName();

        List<CompanyDocumentDTO> documents =
                companyDocumentService.getDocuments(email);

        return ResponseEntity.ok(documents);
    }

    @PutMapping("/documents/{id}")
    public ResponseEntity<CompanyDocumentDTO> updateDocument(
            Authentication authentication,
            @PathVariable Long id,
            @Valid @RequestBody CompanyDocumentDTO dto) {

        String email = authentication.getName();

        CompanyDocumentDTO updatedDocument =
                companyDocumentService.updateDocument(
                        email, id, dto);

        return ResponseEntity.ok(updatedDocument);
    }

    @DeleteMapping("/documents/{id}")
    public ResponseEntity<String> deleteDocument(
            Authentication authentication,
            @PathVariable Long id) {

        String email = authentication.getName();

        companyDocumentService.deleteDocument(
                email, id);

        return ResponseEntity.ok(
                "Document deleted successfully");
    }


    //Recruitment Requests
    @PostMapping("/recruitment-requests")
    public ResponseEntity<RecruitmentRequestDTO> createRecruitmentRequest(
            Authentication authentication,
            @Valid @RequestBody RecruitmentRequestDTO dto) {

        String email = authentication.getName();

        RecruitmentRequestDTO request =
                recruitmentRequestService.createRequest(email, dto);

        return ResponseEntity.ok(request);
    }


    @GetMapping("/recruitment-requests")
    public ResponseEntity<List<RecruitmentRequestDTO>> getMyRecruitmentRequests(
            Authentication authentication) {

        String email = authentication.getName();

        List<RecruitmentRequestDTO> requests =
                recruitmentRequestService.getMyRequests(email);

        return ResponseEntity.ok(requests);
    }


    @GetMapping("/recruitment-requests/{id}")
    public ResponseEntity<RecruitmentRequestDTO> getRecruitmentRequestById(
            Authentication authentication,
            @PathVariable Long id) {

        String email = authentication.getName();

        RecruitmentRequestDTO request =
                recruitmentRequestService.getRequestById(email, id);

        return ResponseEntity.ok(request);
    }


    @PutMapping("/recruitment-requests/{id}")
    public ResponseEntity<RecruitmentRequestDTO> updateRecruitmentRequest(
            Authentication authentication,
            @PathVariable Long id,
            @Valid @RequestBody RecruitmentRequestDTO dto) {

        String email = authentication.getName();

        RecruitmentRequestDTO updatedRequest =
                recruitmentRequestService.updateRequest(
                        email,
                        id,
                        dto
                );

        return ResponseEntity.ok(updatedRequest);
    }


    @PutMapping("/recruitment-requests/{id}/cancel")
    public ResponseEntity<String> cancelRecruitmentRequest(
            Authentication authentication,
            @PathVariable Long id) {

        String email = authentication.getName();

        recruitmentRequestService.cancelRequest(email, id);

        return ResponseEntity.ok(
                "Recruitment request cancelled successfully"
        );
    }


    @DeleteMapping("/recruitment-requests/{id}")
    public ResponseEntity<String> deleteRecruitmentRequest(
            Authentication authentication,
            @PathVariable Long id) {

        String email = authentication.getName();

        recruitmentRequestService.deleteRequest(email, id);

        return ResponseEntity.ok(
                "Recruitment request deleted successfully"
        );
    }


    // Premium Package
    @GetMapping("/premium-packages")
    public ResponseEntity<List<PremiumPackage>> getPremiumPackages() {

        // only packages that can be bought, shortest first (1, 3, 7 days)
        return ResponseEntity.ok(
                premiumPackageRepository.findByActiveTrueOrderByDurationDaysAsc()
        );
    }


    @GetMapping("/recommended-candidates")
    public ResponseEntity<List<EmployerRecommendedCandidateResponseDTO>>
    getRecommendedCandidates(Authentication authentication) {

        return ResponseEntity.ok(
                recruitmentRequestService
                        .getRecommendedCandidates(
                                authentication.getName()
                        )
        );
    }


    @GetMapping("/recommended-candidates/{applicationId}")
    public ResponseEntity<EmployerRecommendedCandidateResponseDTO>
    getRecommendedCandidate(
            Authentication authentication,
            @PathVariable Long applicationId) {

        String email = authentication.getName();

        EmployerRecommendedCandidateResponseDTO candidate =
                recruitmentRequestService
                        .getRecommendedCandidate(
                                email,
                                applicationId
                        );

        return ResponseEntity.ok(candidate);
    }


    @PutMapping("/recommended-candidates/{applicationId}/hire")
    public ResponseEntity<EmployerRecommendedCandidateResponseDTO>
    hireCandidate(
            Authentication authentication,
            @PathVariable Long applicationId) {

        String email = authentication.getName();

        EmployerRecommendedCandidateResponseDTO candidate =
                recruitmentRequestService.hireCandidate(
                        email,
                        applicationId
                );

        return ResponseEntity.ok(candidate);
    }

    @PutMapping("/recommended-candidates/{applicationId}/reject")
    public ResponseEntity<EmployerRecommendedCandidateResponseDTO>
    rejectCandidate(
            Authentication authentication,
            @PathVariable Long applicationId) {

        String email = authentication.getName();

        EmployerRecommendedCandidateResponseDTO candidate =
                recruitmentRequestService.rejectCandidate(
                        email,
                        applicationId
                );

        return ResponseEntity.ok(candidate);
    }



    @GetMapping("/dashboard")
    public ResponseEntity<EmployerDashboardResponseDTO> getDashboard(
            Authentication authentication) {

        String email = authentication.getName();

        return ResponseEntity.ok(
                employerDashboardService.getDashboard(email)
        );
    }

}