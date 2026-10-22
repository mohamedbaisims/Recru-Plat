package com.example.backend.appForm;

import lombok.Data;

@Data
public class ApplicationFormDTO {
    private Long id;
    private String fullName;
    private String email;
    private String address;
    private String diploma;
    private String experience;
    private String institution;
    private String userEmail;      // extra: pour affichage dans l'admin
    private String userFullName;
    private String technicalSkills;
    private String personalSkills;// extra: pour affichage dans l'admin
    private ApplicationStatus status; // 🆕 Statut de la candidature
}
