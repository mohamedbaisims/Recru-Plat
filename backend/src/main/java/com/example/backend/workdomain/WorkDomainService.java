package com.example.backend.workdomain;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class WorkDomainService {
    private final WorkDomainRepository repository;

    public WorkDomain save(WorkDomain domain) {
        return repository.save(domain);
    }

    public List<WorkDomain> findAll() {
        return repository.findAll();
    }

    public WorkDomain update(Long id, WorkDomain domain) {
        WorkDomain existingDomain = repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Domaine introuvable"));

        existingDomain.setName(domain.getName());
        existingDomain.setKeywords(domain.getKeywords());

        return repository.save(existingDomain);
    }

    public void deleteById(Long id) {
        repository.deleteById(id);
    }
}
