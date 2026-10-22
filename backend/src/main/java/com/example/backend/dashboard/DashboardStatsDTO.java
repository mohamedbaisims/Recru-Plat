package com.example.backend.dashboard;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardStatsDTO {
    private Long totalCandidatures;
    private Long totalCandidats;
    private Long totalManagers;
    private Long totalWorkDomains;
    private Long acceptedCandidatures;
    private Long rejectedCandidatures;
    private Long pendingCandidatures;
    private Map<String, Long> candidaturesByDomain;
    private Map<String, Long> candidaturesByStatus;
}
