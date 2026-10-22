package com.example.backend.appForm;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class ApplicationFormRequest {
    private String fullName;
    private String address;
    private String phoneNumber;
    private String email;
    private String birthDate;
    private String birthPlace;
    private String nationality;
    private String diploma;
    private String diplomaYear;
    private String institution;
    private String experience;
    private String technicalSkills;
    private String personalSkills;
    private Integer userId;

    private MultipartFile cv;
    private MultipartFile motivationLetter;
    private MultipartFile diplomaFile;
}
