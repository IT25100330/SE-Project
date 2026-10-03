package com.company.HireNova.Service.Employer;

import com.company.HireNova.DTO.Employer.EmployerDashboardResponseDTO;
import com.company.HireNova.Entity.Candidate.ApplicationStatus;
import com.company.HireNova.Entity.Employer.Employer;
import com.company.HireNova.Entity.Employer.RecruitmentRequest;
import com.company.HireNova.Entity.Employer.RequestStatus;
import com.company.HireNova.Entity.Notification.RecipientType;
import com.company.HireNova.Repository.Candidate.JobApplicationRepository;
import com.company.HireNova.Repository.Employer.EmployerRepository;
import com.company.HireNova.Repository.Employer.RecruitmentRequestRepository;
import com.company.HireNova.Repository.Notification.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EmployerDashboardService {

    private final EmployerRepository employerRepository;
    private final RecruitmentRequestRepository recruitmentRequestRepository;
    private final JobApplicationRepository jobApplicationRepository;
    private final NotificationRepository notificationRepository;

    public EmployerDashboardResponseDTO getDashboard(String email) {

        // ==============================
        // Find logged-in employer
        // ==============================

        Employer employer = employerRepository
                .findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException("Employer not found"));

        Long employerId = employer.getEmployerId();

        // ==============================
        // Company details
        // ==============================

        String companyName = "";

        if (employer.getCompanyProfile() != null) {
            companyName =
                    employer.getCompanyProfile().getCompanyName();
        }

        // ==============================
        // Recruitment Requests
        // ==============================

        List<RecruitmentRequest> requests =
                recruitmentRequestRepository
                        .findByEmployerEmployerId(employerId);

        long totalRequests = requests.size();

        long pendingRequests =
                requests.stream()
                        .filter(request ->
                                request.getStatus() ==
                                        RequestStatus.PENDING)
                        .count();

        long approvedRequests =
                requests.stream()
                        .filter(request ->
                                request.getStatus() ==
                                        RequestStatus.APPROVED)
                        .count();

        long rejectedRequests =
                requests.stream()
                        .filter(request ->
                                request.getStatus() ==
                                        RequestStatus.REJECTED)
                        .count();

        long cancelledRequests =
                requests.stream()
                        .filter(request ->
                                request.getStatus() ==
                                        RequestStatus.CANCELLED)
                        .count();

        // ==============================
        // Active Requests
        // ==============================
        //
        // APPROVED requests are considered
        // active unless they are cancelled.
        // ==============================

        long activeRequests =
                requests.stream()
                        .filter(request ->
                                request.getStatus() ==
                                        RequestStatus.APPROVED)
                        .count();

        // ==============================
        // Premium Requests
        // ==============================

        LocalDateTime now = LocalDateTime.now();

        long premiumRequests =
                requests.stream()
                        .filter(RecruitmentRequest::isPremium)
                        .count();

        long activePremiumRequests =
                requests.stream()
                        .filter(request ->
                                isPremiumActive(request, now))
                        .count();

        // ==============================
        // Recommended Candidates
        // ==============================

        long recommendedCandidates =
                jobApplicationRepository
                        .findByRecruitmentRequestEmployerEmployerIdAndStatus(
                                employerId,
                                ApplicationStatus.RECOMMENDED
                        )
                        .size();

        // ==============================
        // Unread Notifications
        // ==============================

        long unreadNotifications =
                notificationRepository
                        .findByRecipientTypeAndRecipientIdAndIsRead(
                                RecipientType.EMPLOYER,
                                employerId,
                                false
                        )
                        .size();

        // ==============================
        // Return Dashboard Data
        // ==============================

        return new EmployerDashboardResponseDTO(
                companyName,
                employer.getEmail(),
                employer.getContactNumber(),

                totalRequests,
                pendingRequests,
                approvedRequests,
                rejectedRequests,
                cancelledRequests,

                activeRequests,

                premiumRequests,
                activePremiumRequests,

                recommendedCandidates,

                unreadNotifications
        );
    }

    private boolean isPremiumActive(
            RecruitmentRequest request,
            LocalDateTime now) {

        return request.isPremium()
                && request.getPremiumStartDate() != null
                && request.getPremiumEndDate() != null
                && !now.isBefore(request.getPremiumStartDate())
                && !now.isAfter(request.getPremiumEndDate());
    }
}