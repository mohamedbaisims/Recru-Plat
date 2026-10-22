package com.example.backend.appForm;

import com.example.backend.user.User;
import com.example.backend.workdomain.WorkDomain;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class ApplicationForm {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Informations personnelles
    private String fullName;
    private String address;
    private String phoneNumber;
    private String email;
    private LocalDate birthDate;
    private String birthPlace;
    private String nationality;

    // Informations académiques
    private String diploma;
    private String diplomaYear;
    private String institution;
    private String experience;
    private String technicalSkills;
    private String personalSkills;

    // Documents (chemin vers fichiers ou base64)
    //les fichiers il faut etre de types pdf et en retire le byte
    @Lob

    private byte[] cvPath;

    @Lob

    private byte[] motivationLetterPath;

    @Lob

    private byte[] diplomaFilePath;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    @JsonBackReference
    private User user;
    @ManyToOne
    @JoinColumn(name = "work_domain_id")
    private WorkDomain workDomain;

    @Enumerated(EnumType.STRING)
    @Column(length = 50)
    private ApplicationStatus status = ApplicationStatus.PENDING;
}