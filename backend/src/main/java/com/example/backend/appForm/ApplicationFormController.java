package com.example.backend.appForm;

import com.example.backend.appForm.ApplicationForm;
import com.example.backend.user.User;
import com.example.backend.user.UserRepository;
import com.example.backend.appForm.ApplicationFormService;
import com.example.backend.notification.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.http.MediaType;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/applications")
@RequiredArgsConstructor
public class ApplicationFormController {

    private final ApplicationFormService service,applicationFormService;
    private final UserRepository userRepository;
    private final ApplicationFormRepository repository;
    private final NotificationService notificationService;
    private final CvOcrService cvOcrService;

    // ── OCR: extract fields from CV PDF ─────────────────────────────────────────
    @PostMapping(value = "/extract-cv", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Map<String, String>> extractCv(
            @RequestParam("cv") MultipartFile cvFile) throws IOException {
        if (cvFile == null || cvFile.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        String contentType = cvFile.getContentType();
        if (contentType == null || !contentType.equals("application/pdf")) {
            return ResponseEntity.badRequest().build();
        }
        Map<String, String> extracted = cvOcrService.extract(cvFile.getBytes());
        return ResponseEntity.ok(extracted);
    }


    @PostMapping(value = "/with-files", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApplicationForm> createWithFiles(
            @RequestParam String fullName,
            @RequestParam String address,
            @RequestParam String phoneNumber,
            @RequestParam String email,
            @RequestParam String birthDate,
            @RequestParam String birthPlace,
            @RequestParam String nationality,
            @RequestParam String diploma,
            @RequestParam String diplomaYear,
            @RequestParam String institution,
            @RequestParam String experience,
            @RequestParam String technicalSkills,
            @RequestParam String personalSkills,
            @RequestParam Integer userId,

            @RequestParam("cv") MultipartFile cvFile,
            @RequestParam("motivationLetter") MultipartFile motivationFile,
            @RequestParam("diplomaFile") MultipartFile diplomaFile
    ) throws IOException {


        if (!cvFile.getContentType().equals("application/pdf")
                || !motivationFile.getContentType().equals("application/pdf")
                || !diplomaFile.getContentType().equals("application/pdf")) {
            return ResponseEntity.badRequest().body(null);
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Utilisateur introuvable"));

        ApplicationForm form = ApplicationForm.builder()
                .fullName(fullName)
                .address(address)
                .phoneNumber(phoneNumber)
                .email(email)
                .birthDate(LocalDate.parse(birthDate))
                .birthPlace(birthPlace)
                .nationality(nationality)
                .diploma(diploma)
                .diplomaYear(diplomaYear)
                .institution(institution)
                .experience(experience)
                .technicalSkills(technicalSkills)
                .personalSkills(personalSkills)
                .cvPath(cvFile.getBytes())
                .motivationLetterPath(motivationFile.getBytes())
                .diplomaFilePath(diplomaFile.getBytes())
                .user(user)
                .build();

        // 🔔 Utiliser la méthode du service qui crée aussi les notifications
        return ResponseEntity.ok(service.saveApplicationAndNotifyManagers(form));
    }

    //@PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
//    public ResponseEntity<ApplicationForm> create(@ModelAttribute ApplicationFormRequest request) {
  //      return ResponseEntity.ok(service.saveWithFiles(request));
    //}

    // 📄 Liste des candidatures d'un user
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<ApplicationForm>> getUserApps(@PathVariable Integer userId) {
        return ResponseEntity.ok(service.findAllByUser(userId));
    }

    // ✏️ Mise à jour classique
    @PutMapping("/{id}")
    public ResponseEntity<ApplicationForm> update(@PathVariable Long id, @RequestBody ApplicationForm updated) {
        return ResponseEntity.ok(service.update(id, updated));
    }

    @PutMapping(value = "/{id}/with-files", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApplicationForm> updateWithFiles(
            @PathVariable Long id,
            @RequestParam String fullName,
            @RequestParam String address,
            @RequestParam String phoneNumber,
            @RequestParam String email,
            @RequestParam String birthDate,
            @RequestParam String birthPlace,
            @RequestParam String nationality,
            @RequestParam String diploma,
            @RequestParam String diplomaYear,
            @RequestParam String institution,
            @RequestParam String experience,
            @RequestParam String technicalSkills,
            @RequestParam String personalSkills,
            @RequestParam Integer userId,
            @RequestParam(value = "cv", required = false) MultipartFile cvFile,
            @RequestParam(value = "motivationLetter", required = false) MultipartFile motivationFile,
            @RequestParam(value = "diplomaFile", required = false) MultipartFile diplomaFile
    ) throws IOException {
        if (!isPdfFile(cvFile) || !isPdfFile(motivationFile) || !isPdfFile(diplomaFile)) {
            return ResponseEntity.badRequest().body(null);
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Utilisateur introuvable"));

        ApplicationForm updated = ApplicationForm.builder()
                .fullName(fullName)
                .address(address)
                .phoneNumber(phoneNumber)
                .email(email)
                .birthDate(LocalDate.parse(birthDate))
                .birthPlace(birthPlace)
                .nationality(nationality)
                .diploma(diploma)
                .diplomaYear(diplomaYear)
                .institution(institution)
                .experience(experience)
                .technicalSkills(technicalSkills)
                .personalSkills(personalSkills)
                .user(user)
                .build();

        return ResponseEntity.ok(service.updateWithFiles(
                id,
                updated,
                cvFile != null && !cvFile.isEmpty() ? cvFile.getBytes() : null,
                motivationFile != null && !motivationFile.isEmpty() ? motivationFile.getBytes() : null,
                diplomaFile != null && !diplomaFile.isEmpty() ? diplomaFile.getBytes() : null
        ));
    }

    private boolean isPdfFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return true;
        }

        String contentType = file.getContentType();
        String fileName = file.getOriginalFilename();

        return "application/pdf".equalsIgnoreCase(contentType)
                || (fileName != null && fileName.toLowerCase().endsWith(".pdf"));
    }

    // ❌ Suppression
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
    // DTO version pour le frontend (ex: manager)
    @GetMapping("/dto")
    public ResponseEntity<List<ApplicationFormDTO>> getAllApplicationsAsDTO() {
        return ResponseEntity.ok(service.getAllAsDTO());
    }

    // Version brute si tu veux l'utiliser ailleurs (ou à supprimer)
    @GetMapping
    public ResponseEntity<List<ApplicationForm>> getAllApplications() {
        List<ApplicationForm> applications = applicationFormService.getAllApplications();
        return ResponseEntity.ok(applications);
    }
    @PutMapping("/{id}/assign-domain/{domainId}")
    public ResponseEntity<?> assignDomainToApplication(
            @PathVariable Long id,
            @PathVariable Long domainId
    ) {
        applicationFormService.assignDomain(id, domainId);
        return ResponseEntity.ok().build();
    }
    @GetMapping("/dashboard")
    public Map<String, Object> getDashboard() {
        return applicationFormService.getDashboardStats();
    }
    @GetMapping("/by-domain/{domainId}")
    public ResponseEntity<List<ApplicationFormDTO>> getApplicationsByDomain(@PathVariable Long domainId) {
        return ResponseEntity.ok(service.getByWorkDomain(domainId));
    }
    @GetMapping("/unassigned")
    @PreAuthorize("hasRole('MANAGER') or hasRole('ADMIN')")
    public ResponseEntity<List<ApplicationForm>> getUnassignedApplications() {
        List<ApplicationForm> unassigned = repository.findByWorkDomainIsNull();
        return ResponseEntity.ok(unassigned);
    }
    @GetMapping("/assigned/count")
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<Long> countAssignedApplications() {
        long count = repository.countByWorkDomainIsNotNull();
        return ResponseEntity.ok(count);
    }
    @GetMapping("/{id}/file/{type}")
    @PreAuthorize("hasRole('MANAGER') or hasRole('ADMIN')")
    public ResponseEntity<byte[]> getFile(
            @PathVariable Long id,
            @PathVariable String type
    ) {
        System.out.println("📥 Requête reçue -> id=" + id + ", type=" + type);

        ApplicationForm app = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Candidature introuvable"));

        byte[] fileData;
        String filename;

        switch (type.toLowerCase()) {
            case "cv":
                fileData = app.getCvPath();
                filename = "CV.pdf";
                break;
            case "motivation":
                fileData = app.getMotivationLetterPath();
                filename = "LettreMotivation.pdf";
                break;
            case "diploma":
                fileData = app.getDiplomaFilePath();
                filename = "Diplome.pdf";
                break;
            default:
                System.out.println("❌ Type inconnu reçu : " + type);
                return ResponseEntity.badRequest().build();
        }

        if (fileData == null) {
            System.out.println("⚠️ Fichier introuvable pour le type : " + type);
            return ResponseEntity.notFound().build();
        }

        System.out.println("✅ Fichier trouvé : " + filename);
        return ResponseEntity.ok()
                .header("Content-Disposition", "inline; filename=" + filename)
                .contentType(org.springframework.http.MediaType.APPLICATION_PDF)
                .body(fileData);
    }

    // 🆕 Refuser une candidature et notifier le candidat
    @PutMapping("/{id}/reject")
    @PreAuthorize("hasRole('MANAGER') or hasRole('ADMIN')")
    public ResponseEntity<?> rejectApplication(
            @PathVariable Long id,
            @RequestBody Map<String, String> body
    ) {
        String rejectionReason = body.getOrDefault("reason", "Raison non spécifiée");
        
        ApplicationForm app = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Candidature introuvable"));

        // Mettre à jour le statut
        app.setStatus(ApplicationStatus.REJECTED);
        repository.save(app);

        // Notifier le candidat
        if (app.getUser() != null) {
            notificationService.notifyCandidateOfRejection(
                    app.getFullName(),
                    app.getEmail(),
                    rejectionReason,
                    app.getUser().getId()
            );
        }

        return ResponseEntity.ok(Map.of("message", "Candidature refusée"));
    }

    // 🆕 Planifier un entretien et notifier le candidat
    @PutMapping("/{id}/schedule-interview")
    @PreAuthorize("hasRole('MANAGER') or hasRole('ADMIN')")
    public ResponseEntity<?> scheduleInterview(
            @PathVariable Long id,
            @RequestBody Map<String, String> body
    ) {
        String interviewDate = body.get("interviewDate");
        String interviewTime = body.get("interviewTime");
        String interviewType = body.getOrDefault("interviewType", "Technique");

        if (interviewDate == null || interviewTime == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "Date et heure requises"));
        }

        ApplicationForm app = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Candidature introuvable"));

        // Mettre à jour le statut
        app.setStatus(ApplicationStatus.INTERVIEW_SCHEDULED);
        repository.save(app);

        // Notifier le candidat
        if (app.getUser() != null) {
            notificationService.notifyCandidateOfInterviewScheduled(
                    app.getFullName(),
                    app.getEmail(),
                    interviewDate,
                    interviewTime,
                    interviewType,
                    app.getUser().getId()
            );
        }

        return ResponseEntity.ok(Map.of(
                "message", "Entretien planifié",
                "date", interviewDate,
                "time", interviewTime,
                "status", "INTERVIEW_SCHEDULED"
        ));
    }

}
