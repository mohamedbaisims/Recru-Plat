package com.example.backend.appForm;
import com.example.backend.workdomain.WorkDomain;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ApplicationFormRepository extends JpaRepository<ApplicationForm, Long> {
    List<ApplicationForm> findByUserId(Integer userId);
    List<ApplicationForm> findByWorkDomainId(Long domainId);
    List<ApplicationForm> findByWorkDomainIsNull();
    long countByWorkDomainIsNotNull();
    Long countByStatus(ApplicationStatus status);
    Long countByWorkDomain(WorkDomain workDomain);
    
    @Query("SELECT d.name, COUNT(a) FROM ApplicationForm a JOIN a.workDomain d GROUP BY d.name")
    List<Object[]> countApplicationsByDomain();

    @Query("SELECT COUNT(a) FROM ApplicationForm a WHERE a.workDomain IS NULL")
    Long countUnassignedApplications();

}