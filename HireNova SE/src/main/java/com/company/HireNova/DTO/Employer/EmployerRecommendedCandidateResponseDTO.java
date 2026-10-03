package com.company.HireNova.DTO.Employer;

import lombok.Data;

@Data
public class EmployerRecommendedCandidateResponseDTO {

    private Long applicationId;

    private Long candidateId;

    private String firstName;
    private String lastName;
    private String email;
    private String contactNumber;
    private String nic;


    private String jobTitle;
    private Long requestId;
    private boolean cvAvailable;

    private String applicationStatus;

    private Long interviewId;
    private Integer interviewRating;
    private String technicalSkills;
    private String communicationSkills;
    private String interviewComments;
    private String interviewerRecommendation;
}