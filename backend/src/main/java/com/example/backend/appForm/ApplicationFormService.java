package com.example.backend.appForm;
import com.example.backend.user.User;
import com.example.backend.user.UserRepository;
import com.example.backend.workdomain.WorkDomain;
import com.example.backend.workdomain.WorkDomainRepository;
import com.example.backend.notification.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.io.IOException;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ApplicationFormService {
    private final ApplicationFormRepository repository;
    private final UserRepository userRepository;
    private final WorkDomainRepository workDomainRepository;
    private final NotificationService notificationService;
    public ApplicationForm save(ApplicationForm application) {
        if (application.getUser() != null && application.getUser().getId() != null) {
            Integer userId = application.getUser().getId(); // 👈 déjà Integer
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("Utilisateur introuvable avec l'ID: " + userId));
            application.setUser(user);
            System.out.println("User récupéré : " + user);
            System.out.println("Application avant save : " + application);
        }

        return repository.save(application);
    }

    /**
     * Sauvegarde une candidature ET crée une notification pour tous les managers
     * A utiliser lors de la soumission d'une nouvelle candidature
     */
    public ApplicationForm saveApplicationAndNotifyManagers(ApplicationForm application) {
        // Récupérer l'utilisateur si nécessaire
        if (application.getUser() != null && application.getUser().getId() != null) {
            Integer userId = application.getUser().getId();
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("Utilisateur introuvable avec l'ID: " + userId));
            application.setUser(user);
        }

        // Sauvegarder la candidature
        ApplicationForm savedForm = repository.save(application);

        // Créer les notifications pour tous les managers
        try {
            String candidateName = application.getFullName() != null ? 
                    application.getFullName() : 
                    (application.getUser() != null ? 
                            application.getUser().getFirstname() + " " + application.getUser().getLastname() : 
                            "Nouveau candidat");
            String candidatEmail = application.getEmail();
            
            notificationService.notifyManagersOfNewApplication(
                    candidateName,
                    candidatEmail,
                    savedForm.getId()
            );
            System.out.println("✅ Notification créée pour tous les managers");
        } catch (Exception e) {
            System.err.println("❌ Erreur lors de la création de la notification: " + e.getMessage());
            e.printStackTrace();
            // Ne pas bloquer la sauvegarde si la notification échoue
        }

        return savedForm;
    }

    public List<ApplicationForm> findAllByUser(Integer userId) {
        return repository.findByUserId(userId);
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }

    public ApplicationForm update(Long id, ApplicationForm updated) {
        ApplicationForm existing = repository.findById(id).orElseThrow();
        mergeApplication(existing, updated);
        return repository.save(existing);
    }

    public ApplicationForm updateWithFiles(Long id, ApplicationForm updated, byte[] cvBytes, byte[] motivationBytes, byte[] diplomaBytes) {
        ApplicationForm existing = repository.findById(id).orElseThrow();
        mergeApplication(existing, updated);

        if (cvBytes != null) {
            existing.setCvPath(cvBytes);
        }
        if (motivationBytes != null) {
            existing.setMotivationLetterPath(motivationBytes);
        }
        if (diplomaBytes != null) {
            existing.setDiplomaFilePath(diplomaBytes);
        }

        return repository.save(existing);
    }

    private void mergeApplication(ApplicationForm target, ApplicationForm source) {
        target.setFullName(source.getFullName());
        target.setAddress(source.getAddress());
        target.setPhoneNumber(source.getPhoneNumber());
        target.setEmail(source.getEmail());
        target.setBirthDate(source.getBirthDate());
        target.setBirthPlace(source.getBirthPlace());
        target.setNationality(source.getNationality());
        target.setDiploma(source.getDiploma());
        target.setDiplomaYear(source.getDiplomaYear());
        target.setInstitution(source.getInstitution());
        target.setExperience(source.getExperience());
        target.setTechnicalSkills(source.getTechnicalSkills());
        target.setPersonalSkills(source.getPersonalSkills());

        if (source.getUser() != null && source.getUser().getId() != null) {
            User user = userRepository.findById(source.getUser().getId())
                    .orElseThrow(() -> new RuntimeException("Utilisateur introuvable avec l'ID: " + source.getUser().getId()));
            target.setUser(user);
        }

        if (source.getWorkDomain() != null) {
            target.setWorkDomain(source.getWorkDomain());
        }

        if (source.getStatus() != null) {
            target.setStatus(source.getStatus());
        }
    }
    public ApplicationForm saveWithFiles(ApplicationFormRequest request) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

        ApplicationForm form = new ApplicationForm();
        form.setFullName(request.getFullName());
        form.setAddress(request.getAddress());
        form.setPhoneNumber(request.getPhoneNumber());
        form.setEmail(request.getEmail());
        form.setBirthDate(LocalDate.parse(request.getBirthDate()));
        form.setBirthPlace(request.getBirthPlace());
        form.setNationality(request.getNationality());
        form.setDiploma(request.getDiploma());
        form.setDiplomaYear(request.getDiplomaYear());
        form.setInstitution(request.getInstitution());
        form.setExperience(request.getExperience());
        form.setTechnicalSkills(request.getTechnicalSkills());
        form.setPersonalSkills(request.getPersonalSkills());
        form.setUser(user);

        try {
            if (request.getCv() != null && !request.getCv().isEmpty()) {
                form.setCvPath(request.getCv().getBytes());
            }
            if (request.getMotivationLetter() != null && !request.getMotivationLetter().isEmpty()) {
                form.setMotivationLetterPath(request.getMotivationLetter().getBytes());
            }
            if (request.getDiplomaFile() != null && !request.getDiplomaFile().isEmpty()) {
                form.setDiplomaFilePath(request.getDiploma().getBytes());
            }
        } catch (IOException e) {
            throw new RuntimeException("Erreur lors de la lecture des fichiers : " + e.getMessage(), e);
        }

        ApplicationForm savedForm = repository.save(form);
        
        // 🔔 Créer une notification pour tous les managers
        notificationService.notifyManagersOfNewApplication(
                user.getFirstname() + " " + user.getLastname(),
                user.getEmail(),
                savedForm.getId()
        );
        
        return savedForm;
    }
    public List<ApplicationFormDTO> getAllAsDTO() {
        List<ApplicationForm> forms = repository.findAll();

        return forms.stream().map(form -> {
            ApplicationFormDTO dto = new ApplicationFormDTO();
            dto.setId(form.getId());
            dto.setFullName(form.getFullName());
            dto.setEmail(form.getEmail());
            dto.setDiploma(form.getDiploma());
            dto.setInstitution(form.getInstitution());

            dto.setAddress(form.getAddress());                 // ✅ à ajouter
            dto.setExperience(form.getExperience());           // ✅ à ajouter
            dto.setTechnicalSkills(form.getTechnicalSkills()); // ✅ à ajouter
            dto.setPersonalSkills(form.getPersonalSkills());   // ✅ à ajouter

            //

            if (form.getUser() != null) {
                dto.setUserEmail(form.getUser().getEmail());
                dto.setUserFullName(form.getUser().getFirstname() + " " + form.getUser().getLastname());
            }

            return dto;
        }).toList();
    }
    public List<ApplicationForm> getAllApplications() {
        return repository.findAll();
    }
    public void assignDomain(Long appId, Long domainId) {
        ApplicationForm app = repository.findById(appId)
                .orElseThrow(() -> new RuntimeException("Candidature introuvable"));

        WorkDomain domain = workDomainRepository.findById(domainId)
                .orElseThrow(() -> new RuntimeException("Domaine introuvable"));

        app.setWorkDomain(domain);
        repository.save(app);
    }
    public List<ApplicationFormDTO> getByWorkDomain(Long domainId) {
        return repository.findByWorkDomainId(domainId)
                .stream()
                .filter(app -> app.getStatus() != ApplicationStatus.REJECTED)
                .map(ApplicationFormMapper::toDto)
                .collect(Collectors.toList());
    }
    public Map<String, Object> getDashboardStats() {

        List<Object[]> stats = repository.countApplicationsByDomain();
        Long unassigned = repository.countUnassignedApplications();

        Map<String, Object> result = new HashMap<>();

        List<Map<String, Object>> domainStats = new ArrayList<>();

        for (Object[] row : stats) {
            Map<String, Object> item = new HashMap<>();
            item.put("domain", row[0]);
            item.put("count", row[1]);
            domainStats.add(item);
        }

        result.put("byDomain", domainStats);
        result.put("unassigned", unassigned);

        return result;
    }


}