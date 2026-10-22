package com.example.backend.notification;

public enum NotificationType {
    NEW_APPLICATION("Nouvelle candidature"),
    APPLICATION_ASSIGNED("Candidature assignée"),
    APPLICATION_STATUS_CHANGED("Statut candidature changé"),
    APPLICATION_REJECTED("Candidature refusée"),
    INTERVIEW_SCHEDULED("Entretien planifié"),
    NEW_CANDIDATE("Nouveau candidat"),
    REMINDER("Rappel");

    private final String description;

    NotificationType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
