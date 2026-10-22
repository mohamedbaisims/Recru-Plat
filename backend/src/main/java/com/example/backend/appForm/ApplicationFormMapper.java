package com.example.backend.appForm;

public class ApplicationFormMapper {

    public static ApplicationFormDTO toDto(ApplicationForm form) {
        ApplicationFormDTO dto = new ApplicationFormDTO();
        dto.setId(form.getId());
        dto.setFullName(form.getFullName());
        dto.setEmail(form.getEmail());
        dto.setDiploma(form.getDiploma());
        dto.setInstitution(form.getInstitution());
        dto.setAddress(form.getAddress());
        dto.setExperience(form.getExperience());
        dto.setTechnicalSkills(form.getTechnicalSkills());
        dto.setPersonalSkills(form.getPersonalSkills());
        dto.setStatus(form.getStatus()); // 🆕 Ajouter le statut



        if (form.getUser() != null) {
            dto.setUserEmail(form.getUser().getEmail());
            dto.setUserFullName(form.getUser().getFirstname() + " " + form.getUser().getLastname());
        }

        return dto;
    }
    public static ApplicationForm fromDto(ApplicationFormDTO dto) {
        ApplicationForm entity = new ApplicationForm();

        entity.setId(entity.getId());
        entity.setFullName(entity.getFullName());
        entity.setEmail(entity.getEmail());
        entity.setDiploma(entity.getDiploma());
        entity.setInstitution(entity.getInstitution());

        return entity;
    }
}
