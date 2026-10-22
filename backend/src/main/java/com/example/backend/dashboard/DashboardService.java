package com.example.backend.dashboard;

import com.example.backend.appForm.ApplicationFormRepository;
import com.example.backend.appForm.ApplicationStatus;
import com.example.backend.user.UserRepository;
import com.example.backend.user.Role;
import com.example.backend.manager.ManagerRepository;
import com.example.backend.workdomain.WorkDomainRepository;
import org.springframework.stereotype.Service;
import java.util.HashMap;
import java.util.Map;

@Service
public class DashboardService {
    private final ApplicationFormRepository applicationFormRepository;
    private final UserRepository userRepository;
    private final ManagerRepository managerRepository;
    private final WorkDomainRepository workDomainRepository;

    public DashboardService(
            ApplicationFormRepository applicationFormRepository,
            UserRepository userRepository,
            ManagerRepository managerRepository,
            WorkDomainRepository workDomainRepository) {
        this.applicationFormRepository = applicationFormRepository;
        this.userRepository = userRepository;
        this.managerRepository = managerRepository;
        this.workDomainRepository = workDomainRepository;
    }

    public DashboardStatsDTO getDashboardStats() {
        Long totalCandidatures = applicationFormRepository.count();
        Long totalCandidats = userRepository.countByRole(Role.USER);
        Long totalManagers = userRepository.countByRole(Role.MANAGER);
        Long totalWorkDomains = workDomainRepository.count();

        Long pendingCandidatures = applicationFormRepository.countByStatus(ApplicationStatus.PENDING);
        Long acceptedCandidatures = applicationFormRepository.countByStatus(ApplicationStatus.INTERVIEW_SCHEDULED);
        Long rejectedCandidatures = applicationFormRepository.countByStatus(ApplicationStatus.REJECTED);

        Map<String, Long> candidaturesByDomain = getCandidaturesByDomain();

        Map<String, Long> candidaturesByStatus = new HashMap<>();
        candidaturesByStatus.put("Acceptée", acceptedCandidatures);
        candidaturesByStatus.put("Rejetée", rejectedCandidatures);
        candidaturesByStatus.put("En attente", pendingCandidatures);

        return DashboardStatsDTO.builder()
                .totalCandidatures(totalCandidatures)
                .totalCandidats(totalCandidats)
                .totalManagers(totalManagers)
                .totalWorkDomains(totalWorkDomains)
                .acceptedCandidatures(acceptedCandidatures)
                .rejectedCandidatures(rejectedCandidatures)
                .pendingCandidatures(pendingCandidatures)
                .candidaturesByDomain(candidaturesByDomain)
                .candidaturesByStatus(candidaturesByStatus)
                .build();
    }

    private Map<String, Long> getCandidaturesByDomain() {
        Map<String, Long> result = new HashMap<>();
        
        // Récupérer tous les domaines avec leurs candidatures
        var domaines = workDomainRepository.findAll();
        for (var domaine : domaines) {
            Long count = applicationFormRepository.countByWorkDomain(domaine);
            result.put(domaine.getName(), count);
        }
        
        return result;
    }
}
