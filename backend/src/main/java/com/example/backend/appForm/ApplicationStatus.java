package com.example.backend.appForm;

public enum ApplicationStatus {
    PENDING("En attente"),
    INTERVIEW_SCHEDULED("Entretien programmé"),
    REJECTED("Refusée");

    private final String displayName;

    ApplicationStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
