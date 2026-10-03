package com.company.HireNova.DTO.Employer;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmployerDashboardResponseDTO {

    private String companyName;
    private String email;
    private String contactNumber;

    private long totalRequests;
    private long pendingRequests;
    private long approvedRequests;
    private long rejectedRequests;
    private long cancelledRequests;

    private long activeRequests;

    private long premiumRequests;
    private long activePremiumRequests;

    private long recommendedCandidates;

    private long unreadNotifications;
}