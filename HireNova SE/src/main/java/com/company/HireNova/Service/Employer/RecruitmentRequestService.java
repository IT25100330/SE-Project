package com.company.HireNova.Service.Employer;

import com.company.HireNova.DTO.Employer.RecruitmentRequestDTO;
import com.company.HireNova.DTO.Notification.NotificationCreateDTO;

import com.company.HireNova.Entity.Admin.Industry;
import com.company.HireNova.Entity.Employer.*;

import com.company.HireNova.Repository.Admin.IndustryRepository;
import com.company.HireNova.Repository.Employer.CompanyProfileRepository;
import com.company.HireNova.Repository.Employer.EmployerRepository;
import com.company.HireNova.Repository.Employer.PremiumPackageRepository;
import com.company.HireNova.Repository.Employer.RecruitmentRequestRepository;
import com.company.HireNova.Repository.Interviewer.InterviewFeedbackRepository;

import com.company.HireNova.Service.Notification.EmailService;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

import com.company.HireNova.Entity.Notification.NotificationChannel;
import com.company.HireNova.Entity.Notification.NotificationType;
import com.company.HireNova.Entity.Notification.RecipientType;
import com.company.HireNova.Entity.Notification.SenderType;

import com.company.HireNova.Entity.Payment.Payment;
import com.company.HireNova.Factory.NotificationFactory;
import com.company.HireNova.Service.Notification.NotificationService;

import com.company.HireNova.Repository.Candidate.CandidateCVRepository;
import com.company.HireNova.DTO.Employer.EmployerRecommendedCandidateResponseDTO;
import com.company.HireNova.Entity.Candidate.ApplicationStatus;
import com.company.HireNova.Entity.Candidate.JobApplication;
import com.company.HireNova.Repository.Candidate.JobApplicationRepository;

import java.util.List;
import java.util.stream.Collectors;
import java.time.LocalDateTime;
import java.util.Comparator;


@Service
@RequiredArgsConstructor
public class RecruitmentRequestService {

    private final RecruitmentRequestRepository recruitmentRequestRepository;
    private final EmployerRepository employerRepository;
    private final NotificationService notificationService;

    // Design pattern: Factory — builds every notification (see Factory/NotificationFactory)
    private final NotificationFactory notificationFactory;
    private final PremiumPackageRepository premiumPackageRepository;
    private final CandidateCVRepository candidateCVRepository;
    private final JobApplicationRepository jobApplicationRepository;
    private final InterviewFeedbackRepository interviewFeedbackRepository;
    private final EmailService emailService;
    private final CompanyProfileRepository companyProfileRepository;
    private final IndustryRepository industryRepository;


    // ==========================================
    // CREATE RECRUITMENT REQUEST
    // ==========================================

    public RecruitmentRequestDTO createRequest(
            String email,
            RecruitmentRequestDTO dto) {

        Employer employer = employerRepository
                .findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException("Employer not found"));

        RecruitmentRequest request =
                new RecruitmentRequest();

        request.setJobTitle(dto.getJobTitle());
        request.setNumberOfVacancies(dto.getNumberOfVacancies());
        request.setJobDescription(dto.getJobDescription());
        request.setRequiredSkills(dto.getRequiredSkills());
        request.setExperienceRequired(dto.getExperienceRequired());
        request.setSalaryRange(dto.getSalaryRange());
        request.setEmploymentType(dto.getEmploymentType());
        request.setLocation(dto.getLocation());
        request.setApplicationDeadline(dto.getApplicationDeadline());

        // New request starts as PENDING
        request.setStatus(RequestStatus.PENDING);

        // Link request to logged-in employer
        request.setEmployer(employer);


        // ==========================================
        // GET INDUSTRY FROM COMPANY PROFILE
        // ==========================================

        CompanyProfile companyProfile =
                companyProfileRepository
                        .findByEmployerEmployerId(
                                employer.getEmployerId()
                        )
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Company profile not found"
                                )
                        );

        if (companyProfile.getIndustry() == null) {
            throw new RuntimeException(
                    "Company profile is not assigned to an industry"
            );
        }

        Industry industry =
                companyProfile.getIndustry();

        if (!industry.isActive()) {
            throw new RuntimeException(
                    "Company profile industry is inactive"
            );
        }

        // Assign industry to recruitment request
        request.setIndustry(industry);


        RecruitmentRequest savedRequest =
                recruitmentRequestRepository.save(request);


        // ==========================================
        // NOTIFY RECRUITMENT OFFICER
        // ==========================================

        notificationService.createNotification(
                notificationFactory.requestSubmittedToOfficer(
                        notificationService.getRecruitmentOfficerIdByIndustry(savedRequest.getIndustry().getIndustryId()),
                        savedRequest));


        // ==========================================
        // NOTIFY EMPLOYER
        // ==========================================

        notificationService.createNotification(
                notificationFactory.requestSubmittedToEmployer(
                        savedRequest));

        return convertToDTO(savedRequest);
    }


    // ==========================================
    // VIEW EMPLOYER'S OWN REQUESTS
    // ==========================================

    public List<RecruitmentRequestDTO> getMyRequests(
            String email) {

        Employer employer =
                employerRepository
                        .findByEmail(email)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Employer not found"
                                )
                        );

        return recruitmentRequestRepository
                .findByEmployerEmployerId(
                        employer.getEmployerId()
                )
                .stream()
                .map(this::convertToDTO)
                .toList();
    }


    // ==========================================
    // VIEW SINGLE REQUEST
    // ==========================================

    public RecruitmentRequestDTO getRequestById(
            String email,
            Long requestId) {

        Employer employer =
                employerRepository
                        .findByEmail(email)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Employer not found"
                                )
                        );

        RecruitmentRequest request =
                recruitmentRequestRepository
                        .findById(requestId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Recruitment request not found"
                                )
                        );

        if (!request.getEmployer()
                .getEmployerId()
                .equals(employer.getEmployerId())) {

            throw new RuntimeException(
                    "You are not authorized to view this request"
            );
        }

        return convertToDTO(request);
    }


    // ==========================================
    // UPDATE RECRUITMENT REQUEST
    // ==========================================

    public RecruitmentRequestDTO updateRequest(
            String email,
            Long requestId,
            RecruitmentRequestDTO dto) {

        Employer employer =
                employerRepository
                        .findByEmail(email)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Employer not found"
                                )
                        );

        RecruitmentRequest request =
                recruitmentRequestRepository
                        .findById(requestId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Recruitment request not found"
                                )
                        );

        if (!request.getEmployer()
                .getEmployerId()
                .equals(employer.getEmployerId())) {

            throw new RuntimeException(
                    "You are not authorized to update this request"
            );
        }


        request.setJobTitle(
                dto.getJobTitle()
        );

        request.setNumberOfVacancies(
                dto.getNumberOfVacancies()
        );

        request.setJobDescription(
                dto.getJobDescription()
        );

        request.setRequiredSkills(
                dto.getRequiredSkills()
        );

        request.setExperienceRequired(
                dto.getExperienceRequired()
        );

        request.setSalaryRange(
                dto.getSalaryRange()
        );

        request.setEmploymentType(
                dto.getEmploymentType()
        );

        request.setLocation(
                dto.getLocation()
        );

        request.setApplicationDeadline(
                dto.getApplicationDeadline()
        );


        // ==========================================
        // KEEP INDUSTRY FROM COMPANY PROFILE
        // ==========================================

        CompanyProfile companyProfile =
                companyProfileRepository
                        .findByEmployerEmployerId(
                                employer.getEmployerId()
                        )
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Company profile not found"
                                )
                        );

        if (companyProfile.getIndustry() == null) {
            throw new RuntimeException(
                    "Company profile is not assigned to an industry"
            );
        }

        Industry industry =
                companyProfile.getIndustry();

        if (!industry.isActive()) {
            throw new RuntimeException(
                    "Company profile industry is inactive"
            );
        }

        request.setIndustry(industry);


        RecruitmentRequest updatedRequest =
                recruitmentRequestRepository.save(request);


        // ==========================================
        // NOTIFY RECRUITMENT OFFICER
        // ==========================================

        notificationService.createNotification(
                notificationFactory.requestUpdatedToOfficer(
                        notificationService.getRecruitmentOfficerIdByIndustry(updatedRequest.getIndustry().getIndustryId()),
                        updatedRequest));


        // ==========================================
        // NOTIFY EMPLOYER
        // ==========================================

        notificationService.createNotification(
                notificationFactory.requestUpdatedToEmployer(
                        updatedRequest));


        return convertToDTO(updatedRequest);
    }


    // ==========================================
    // CANCEL RECRUITMENT REQUEST
    // ==========================================

    public void cancelRequest(
            String email,
            Long requestId) {

        Employer employer =
                employerRepository
                        .findByEmail(email)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Employer not found"
                                )
                        );

        RecruitmentRequest request =
                recruitmentRequestRepository
                        .findById(requestId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Recruitment request not found"
                                )
                        );

        if (!request.getEmployer()
                .getEmployerId()
                .equals(employer.getEmployerId())) {

            throw new RuntimeException(
                    "You are not authorized to cancel this request"
            );
        }

        request.setStatus(
                RequestStatus.CANCELLED
        );

        RecruitmentRequest cancelledRequest =
                recruitmentRequestRepository.save(request);


        // ==========================================
        // NOTIFY RECRUITMENT OFFICER
        // ==========================================

        notificationService.createNotification(
                notificationFactory.requestCancelledToOfficer(
                        notificationService.getRecruitmentOfficerIdByIndustry(cancelledRequest.getIndustry().getIndustryId()),
                        cancelledRequest));


        // ==========================================
        // NOTIFY EMPLOYER
        // ==========================================

        notificationService.createNotification(
                notificationFactory.requestCancelledToEmployer(
                        cancelledRequest));
    }


    // ==========================================
    // DELETE RECRUITMENT REQUEST
    // ==========================================

    public void deleteRequest(
            String email,
            Long requestId) {

        Employer employer =
                employerRepository
                        .findByEmail(email)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Employer not found"
                                )
                        );

        RecruitmentRequest request =
                recruitmentRequestRepository
                        .findById(requestId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Recruitment request not found"
                                )
                        );

        if (!request.getEmployer()
                .getEmployerId()
                .equals(employer.getEmployerId())) {

            throw new RuntimeException(
                    "You are not authorized to delete this request"
            );
        }

        recruitmentRequestRepository.delete(request);
    }


    // ==========================================
    // PREMIUM PURCHASE
    // ==========================================

    /**
     * Switches Premium on for a request. Called ONLY by PaymentService after a
     * payment has been PAID (before, an endpoint switched Premium on for free).
     * A renewal after Premium ended starts a new period from now.
     */
    public RecruitmentRequest activatePremium(
            RecruitmentRequest request,
            PremiumPackage premiumPackage,
            Payment payment) {

        LocalDateTime startDate = LocalDateTime.now();
        LocalDateTime endDate = startDate.plusDays(premiumPackage.getDurationDays());

        request.setPremium(true);
        request.setPremiumStartDate(startDate);
        request.setPremiumEndDate(endDate);
        request.setPremiumPackage(premiumPackage);

        RecruitmentRequest savedRequest =
                recruitmentRequestRepository.save(request);


        // ==========================================
        // NOTIFY RECRUITMENT OFFICER
        // ==========================================

        notificationService.createNotification(
                notificationFactory.premiumPurchasedToOfficer(
                        notificationService.getRecruitmentOfficerIdByIndustry(savedRequest.getIndustry().getIndustryId()),
                        savedRequest));


        // ==========================================
        // NOTIFY EMPLOYER (receipt email)
        // ==========================================

        notificationService.createNotification(
                notificationFactory.premiumActivatedToEmployer(
                        savedRequest,
                        premiumPackage,
                        payment));


        return savedRequest;
    }


    // ==========================================
    // CANDIDATE JOB LISTING
    // ==========================================

    public List<RecruitmentRequestDTO> getAvailableJobs(
            Long industryId) {

        LocalDateTime now =
                LocalDateTime.now();

        List<RecruitmentRequest> requests =
                recruitmentRequestRepository
                        .findByIndustry_IndustryIdAndStatus(
                                industryId,
                                RequestStatus.APPROVED
                        );

        return requests.stream()
                .sorted(
                        Comparator.comparing(
                                (RecruitmentRequest request) ->
                                        isPremiumActive(
                                                request,
                                                now
                                        )
                        ).reversed()
                )
                .map(this::convertToDTO)
                .toList();
    }


    private boolean isPremiumActive(
            RecruitmentRequest request,
            LocalDateTime now) {

        return request.isPremium()
                && request.getPremiumStartDate() != null
                && request.getPremiumEndDate() != null
                && !now.isBefore(
                request.getPremiumStartDate()
        )
                && !now.isAfter(
                request.getPremiumEndDate()
        );
    }


    // ==========================================
    // CONVERT ENTITY → DTO
    // ==========================================

    private RecruitmentRequestDTO convertToDTO(
            RecruitmentRequest request) {

        RecruitmentRequestDTO dto =
                new RecruitmentRequestDTO();

        dto.setRequestId(
                request.getRequestId()
        );

        // ← ADD THIS
        dto.setStatus(
                request.getStatus() != null ? request.getStatus().name() : null
        );


        dto.setJobTitle(
                request.getJobTitle()
        );

        dto.setNumberOfVacancies(
                request.getNumberOfVacancies()
        );

        dto.setJobDescription(
                request.getJobDescription()
        );

        dto.setRequiredSkills(
                request.getRequiredSkills()
        );

        dto.setExperienceRequired(
                request.getExperienceRequired()
        );

        dto.setSalaryRange(
                request.getSalaryRange()
        );

        dto.setEmploymentType(
                request.getEmploymentType()
        );

        dto.setLocation(
                request.getLocation()
        );

        dto.setApplicationDeadline(
                request.getApplicationDeadline()
        );


        // Industry ID
        if (request.getIndustry() != null) {

            dto.setIndustryId(
                    request.getIndustry()
                            .getIndustryId()
            );
        }


        dto.setPremium(
                request.isPremium()
        );

        dto.setPremiumStartDate(
                request.getPremiumStartDate()
        );

        dto.setPremiumEndDate(
                request.getPremiumEndDate()
        );


        if (request.getPremiumPackage() != null) {

            dto.setPremiumPackageId(
                    request.getPremiumPackage()
                            .getPackageId()
            );
        }


        return dto;
    }


    // ==========================================
    // GET RECOMMENDED CANDIDATES
    // ==========================================

    public List<EmployerRecommendedCandidateResponseDTO>
    getRecommendedCandidates(String email) {

        Employer employer =
                employerRepository
                        .findByEmail(email)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Employer not found"
                                )
                        );

        List<JobApplication> applications =
                jobApplicationRepository
                        .findByRecruitmentRequestEmployerEmployerIdAndStatus(
                                employer.getEmployerId(),
                                ApplicationStatus.RECOMMENDED
                        );

        return applications.stream()
                .map(this::convertToRecommendedCandidateDTO)
                .collect(Collectors.toList());
    }


    // ==========================================
    // CONVERT RECOMMENDED CANDIDATE
    // ==========================================

    private EmployerRecommendedCandidateResponseDTO
    convertToRecommendedCandidateDTO(
            JobApplication application) {

        EmployerRecommendedCandidateResponseDTO dto =
                new EmployerRecommendedCandidateResponseDTO();

        dto.setApplicationId(
                application.getApplicationId()
        );

        dto.setApplicationStatus(
                application.getStatus().name()
        );


        // Candidate
        dto.setCandidateId(
                application.getCandidate()
                        .getCandidateId()
        );

        dto.setFirstName(
                application.getCandidate()
                        .getFirstName()
        );

        dto.setLastName(
                application.getCandidate()
                        .getLastName()
        );

        dto.setEmail(
                application.getCandidate()
                        .getEmail()
        );

        dto.setContactNumber(
                application.getCandidate()
                        .getContactNumber()
        );

        dto.setNic(
                application.getCandidate()
                        .getNic()
        );


        dto.setCvAvailable(
                candidateCVRepository
                        .findByCandidateCandidateId(
                                application.getCandidate()
                                        .getCandidateId()
                        )
                        .isPresent()
        );


        // Recruitment Request
        dto.setRequestId(
                application.getRecruitmentRequest()
                        .getRequestId()
        );

        dto.setJobTitle(
                application.getRecruitmentRequest()
                        .getJobTitle()
        );


        // Interview Feedback
        interviewFeedbackRepository
                .findByInterviewJobApplicationRecruitmentRequestRequestId(
                        application.getRecruitmentRequest()
                                .getRequestId()
                )
                .stream()
                .filter(feedback ->
                        feedback.getInterview()
                                .getJobApplication()
                                .getApplicationId()
                                .equals(
                                        application.getApplicationId()
                                )
                )
                .findFirst()
                .ifPresent(feedback -> {

                    dto.setInterviewId(
                            feedback.getInterview()
                                    .getInterviewId()
                    );

                    dto.setInterviewRating(
                            feedback.getRating()
                    );

                    dto.setTechnicalSkills(
                            feedback.getTechnicalSkills()
                    );

                    dto.setCommunicationSkills(
                            feedback.getCommunicationSkills()
                    );

                    dto.setInterviewComments(
                            feedback.getComments()
                    );

                    dto.setInterviewerRecommendation(
                            feedback.getRecommendation()
                    );
                });


        return dto;
    }


    // ==========================================
    // GET SINGLE RECOMMENDED CANDIDATE
    // ==========================================

    public EmployerRecommendedCandidateResponseDTO
    getRecommendedCandidate(
            String email,
            Long applicationId) {

        Employer employer =
                employerRepository
                        .findByEmail(email)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Employer not found"
                                )
                        );

        JobApplication application =
                jobApplicationRepository
                        .findById(applicationId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Application not found"
                                )
                        );


        // Check ownership
        if (!application.getRecruitmentRequest()
                .getEmployer()
                .getEmployerId()
                .equals(employer.getEmployerId())) {

            throw new RuntimeException(
                    "You are not authorized to view this candidate"
            );
        }


        // Only recommended candidates can be viewed
        if (application.getStatus()
                != ApplicationStatus.RECOMMENDED) {

            throw new RuntimeException(
                    "This candidate has not been recommended"
            );
        }

        return convertToRecommendedCandidateDTO(
                application
        );
    }


    // ==========================================
    // HIRE CANDIDATE
    // ==========================================

    public EmployerRecommendedCandidateResponseDTO
    hireCandidate(
            String email,
            Long applicationId) {

        Employer employer =
                employerRepository
                        .findByEmail(email)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Employer not found"
                                )
                        );

        JobApplication application =
                jobApplicationRepository
                        .findById(applicationId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Application not found"
                                )
                        );


        // Security check
        if (!application.getRecruitmentRequest()
                .getEmployer()
                .getEmployerId()
                .equals(employer.getEmployerId())) {

            throw new RuntimeException(
                    "You are not authorized to make a decision on this candidate"
            );
        }


        // Only recommended candidates can be hired
        if (application.getStatus()
                != ApplicationStatus.RECOMMENDED) {

            throw new RuntimeException(
                    "Only recommended candidates can be hired"
            );
        }


        // Final decision
        application.setStatus(
                ApplicationStatus.HIRED
        );

        JobApplication savedApplication =
                jobApplicationRepository.save(
                        application
                );


        // ==========================================
        // CANDIDATE NOTIFICATION
        // ==========================================

        notificationService.createNotification(
                notificationFactory.candidateSelected(
                        application));


        // ==========================================
        // RO NOTIFICATION
        // ==========================================

        notificationService.createNotification(
                notificationFactory.candidateHiredToOfficer(
                        notificationService.getRecruitmentOfficerIdByIndustry(application.getRecruitmentRequest().getIndustry().getIndustryId()),
                        application));


        return convertToRecommendedCandidateDTO(
                savedApplication
        );
    }


    // ==========================================
    // REJECT CANDIDATE
    // ==========================================

    public EmployerRecommendedCandidateResponseDTO
    rejectCandidate(
            String email,
            Long applicationId) {

        Employer employer =
                employerRepository
                        .findByEmail(email)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Employer not found"
                                )
                        );

        JobApplication application =
                jobApplicationRepository
                        .findById(applicationId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Application not found"
                                )
                        );


        // Security check
        if (!application.getRecruitmentRequest()
                .getEmployer()
                .getEmployerId()
                .equals(employer.getEmployerId())) {

            throw new RuntimeException(
                    "You are not authorized to make a decision on this candidate"
            );
        }


        // Only recommended candidates can be rejected
        if (application.getStatus()
                != ApplicationStatus.RECOMMENDED) {

            throw new RuntimeException(
                    "Only recommended candidates can be rejected"
            );
        }


        // Final decision
        application.setStatus(
                ApplicationStatus.REJECTED
        );

        JobApplication savedApplication =
                jobApplicationRepository.save(
                        application
                );


        // ==========================================
        // CANDIDATE NOTIFICATION
        // ==========================================

        notificationService.createNotification(
                notificationFactory.candidateNotSelected(
                        application));


        // ==========================================
        // RO NOTIFICATION
        // ==========================================

        notificationService.createNotification(
                notificationFactory.candidateRejectedToOfficer(
                        notificationService.getRecruitmentOfficerIdByIndustry(application.getRecruitmentRequest().getIndustry().getIndustryId()),
                        application));


        return convertToRecommendedCandidateDTO(
                savedApplication
        );
    }
}