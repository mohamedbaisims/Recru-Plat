package com.example.backend.notification;

import com.example.backend.user.User;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "notification")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "manager_id", referencedColumnName = "id", nullable = true)
    @JsonBackReference
    private User manager;

    // 🆕 Pour les notifications aux candidats
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recipient_user_id", referencedColumnName = "id", nullable = true)
    @JsonBackReference
    private User recipientUser;

    private String title;
    private String message;
    private String candidatName;
    private String candidatEmail;
    private Long applicationId;

    @Enumerated(EnumType.STRING)
    @Column(length = 50)
    private NotificationType type;

    @Column(name = "is_read")
    private boolean read = false;

    private LocalDateTime createdAt;
    private LocalDateTime readAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getManager() {
        return manager;
    }

    public void setManager(User manager) {
        this.manager = manager;
    }

    public User getRecipientUser() {
        return recipientUser;
    }

    public void setRecipientUser(User recipientUser) {
        this.recipientUser = recipientUser;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getCandidatName() {
        return candidatName;
    }

    public void setCandidatName(String candidatName) {
        this.candidatName = candidatName;
    }

    public String getCandidatEmail() {
        return candidatEmail;
    }

    public void setCandidatEmail(String candidatEmail) {
        this.candidatEmail = candidatEmail;
    }

    public Long getApplicationId() {
        return applicationId;
    }

    public void setApplicationId(Long applicationId) {
        this.applicationId = applicationId;
    }

    public NotificationType getType() {
        return type;
    }

    public void setType(NotificationType type) {
        this.type = type;
    }

    public boolean isRead() {
        return read;
    }

    public void setRead(boolean read) {
        this.read = read;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getReadAt() {
        return readAt;
    }

    public void setReadAt(LocalDateTime readAt) {
        this.readAt = readAt;
    }
}
