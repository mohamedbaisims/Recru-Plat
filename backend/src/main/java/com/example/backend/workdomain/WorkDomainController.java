package com.example.backend.workdomain;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/work-domains")
@RequiredArgsConstructor
public class WorkDomainController {
    private final WorkDomainService service;

    @PostMapping
    public ResponseEntity<WorkDomain> create(@RequestBody WorkDomain domain) {
        return ResponseEntity.ok(service.save(domain));
    }

    @GetMapping
    public ResponseEntity<List<WorkDomain>> getAll() {
        return ResponseEntity.ok(service.findAll());
    }

    @PutMapping("/{id}")
    public ResponseEntity<WorkDomain> update(@PathVariable Long id, @RequestBody WorkDomain domain) {
        return ResponseEntity.ok(service.update(id, domain));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
