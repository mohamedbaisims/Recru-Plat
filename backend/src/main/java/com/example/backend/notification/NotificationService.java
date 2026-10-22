package com.example.backend.notification;

import com.example.backend.user.User;
import com.example.backend.user.UserRepository;
import com.example.backend.user.Role;
import com.example.backend.email.EmailService;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class NotificationService {
    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;
    private final EmailService emailService;

    public NotificationService(NotificationRepository notificationRepository, 
                             UserRepository userRepository,
                             EmailService emailService) {
        this.notificationRepository = notificationRepository;
        this.userRepository = userRepository;
        this.emailService = emailService;
    }

    /**
     * Créer une notification pour tous les managers
     * Appelé quand un nouveau candidat soumet une candidature
     */
    public void notifyManagersOfNewApplication(String candidatName, String candidatEmail, Long applicationId) {
        // Récupérer tous les managers
        List<User> managers = userRepository.findByRole(Role.MANAGER);
        
        for (User manager : managers) {
            Notification notification = Notification.builder()
                    .manager(manager)
                    .title("Nouvelle candidature soumise")
                    .message(candidatName + " (" + candidatEmail + ") a soumis une nouvelle candidature")
                    .candidatName(candidatName)
                    .candidatEmail(candidatEmail)
                    .applicationId(applicationId)
                    .type(NotificationType.NEW_APPLICATION)
                    .read(false)
                    .build();
            
            notificationRepository.save(notification);
        }
    }

    /**
     * Récupérer les notifications non lues d'un manager
     */
    public List<NotificationDTO> getUnreadNotifications(User manager) {
        List<Notification> notifications = notificationRepository
                .findByManagerAndReadFalseOrderByCreatedAtDesc(manager);
        return notifications.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * 🆕 Récupérer les notifications non lues d'un candidat
     */
    public List<NotificationDTO> getUnreadNotificationsForCandidate(User recipientUser) {
        List<Notification> notifications = notificationRepository
                .findByRecipientUserAndReadFalseOrderByCreatedAtDesc(recipientUser);
        return notifications.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * 🆕 Récupérer toutes les notifications d'un candidat
     */
    public List<NotificationDTO> getAllNotificationsForCandidate(User recipientUser) {
        List<Notification> notifications = notificationRepository
                .findByRecipientUserOrderByCreatedAtDesc(recipientUser);
        return notifications.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * 🆕 Récupérer les 10 dernières notifications d'un candidat
     */
    public List<NotificationDTO> getRecentNotificationsForCandidate(User recipientUser) {
        List<Notification> notifications = notificationRepository
                .findRecentCandidateNotifications(recipientUser);
        return notifications.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * 🆕 Compter les notifications non lues d'un candidat
     */
    public Long countUnreadNotificationsForCandidate(User recipientUser) {
        return notificationRepository.countUnreadByCandidateUser(recipientUser);
    }

    /**
     * Récupérer toutes les notifications d'un manager
     */
    public List<NotificationDTO> getAllNotifications(User manager) {
        List<Notification> notifications = notificationRepository
                .findByManagerOrderByCreatedAtDesc(manager);
        return notifications.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Récupérer les 10 dernières notifications
     */
    public List<NotificationDTO> getRecentNotifications(User manager) {
        List<Notification> notifications = notificationRepository
                .findRecentNotifications(manager);
        return notifications.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Compter les notifications non lues
     */
    public Long countUnreadNotifications(User manager) {
        return notificationRepository.countUnreadByManager(manager);
    }

    /**
     * Marquer une notification comme lue
     */
    public void markAsRead(Long notificationId) {
        notificationRepository.findById(notificationId).ifPresent(notification -> {
            notification.setRead(true);
            notification.setReadAt(LocalDateTime.now());
            notificationRepository.save(notification);
        });
    }

    /**
     * Marquer toutes les notifications comme lues
     */
    public void markAllAsRead(User manager) {
        List<Notification> unread = notificationRepository
                .findByManagerAndReadFalseOrderByCreatedAtDesc(manager);
        unread.forEach(notification -> {
            notification.setRead(true);
            notification.setReadAt(LocalDateTime.now());
        });
        notificationRepository.saveAll(unread);
    }

        /**
         * Marquer toutes les notifications candidat comme lues
         */
    public void markAllAsReadForCandidate(User recipientUser) {
        List<Notification> unread = notificationRepository
                .findByRecipientUserAndReadFalseOrderByCreatedAtDesc(recipientUser);
        unread.forEach(notification -> {
            notification.setRead(true);
            notification.setReadAt(LocalDateTime.now());
        });
        notificationRepository.saveAll(unread);
    }

    /**
     * Supprimer une notification
     */
    public void deleteNotification(Long notificationId) {
        notificationRepository.deleteById(notificationId);
    }

    /**
     * Supprimer toutes les notifications
     */
    public void deleteAllNotifications(User manager) {
        List<Notification> notifications = notificationRepository
                .findByManagerOrderByCreatedAtDesc(manager);
        notificationRepository.deleteAll(notifications);
    }

    /**
     * 🆕 Notifier le candidat que sa candidature a été refusée
     */
    public void notifyCandidateOfRejection(String candidateName, String candidatEmail, String rejectionReason, Integer candidateUserId) {
        User candidate = userRepository.findById(candidateUserId)
                .orElseThrow(() -> new RuntimeException("Candidat non trouvé avec l'ID: " + candidateUserId));

        // Créer notification en base de données
        Notification notification = Notification.builder()
                .recipientUser(candidate)
                .title("Candidature refusée")
                .message("Votre candidature a été refusée. Raison: " + rejectionReason)
                .candidatName(candidateName)
                .candidatEmail(candidatEmail)
                .type(NotificationType.APPLICATION_REJECTED)
                .read(false)
                .build();

        notificationRepository.save(notification);
        
        // Envoyer email au candidat
        emailService.sendRejectionEmail(candidatEmail, candidateName, rejectionReason);
        
        System.out.println("✅ Notification de rejet envoyée à " + candidateUserId);
    }

    /**
     * 🆕 Notifier le candidat qu'un entretien a été planifié
     */
    public void notifyCandidateOfInterviewScheduled(String candidateName, String candidatEmail, 
                                                    String interviewDate, String interviewTime, 
                                                    String interviewType, Integer candidateUserId) {
        User candidate = userRepository.findById(candidateUserId)
                .orElseThrow(() -> new RuntimeException("Candidat non trouvé avec l'ID: " + candidateUserId));

        String message = String.format(
                "Félicitations! Un entretien a été planifié pour vous. Date: %s à %s. Type: %s",
                interviewDate, interviewTime, interviewType
        );

        // Créer notification en base de données
        Notification notification = Notification.builder()
                .recipientUser(candidate)
                .title("Entretien planifié")
                .message(message)
                .candidatName(candidateName)
                .candidatEmail(candidatEmail)
                .type(NotificationType.INTERVIEW_SCHEDULED)
                .read(false)
                .build();

        notificationRepository.save(notification);
        
        // Envoyer email d'invitation à l'entretien
        emailService.sendInterviewInvitationEmail(candidatEmail, candidateName, interviewDate, interviewTime, interviewType, null);
        
        System.out.println("✅ Notification d'entretien envoyée à " + candidateUserId);
    }

    /**
     * Convertir Notification en DTO
     */
    private NotificationDTO convertToDTO(Notification notification) {
        return NotificationDTO.builder()
                .id(notification.getId())
                .title(notification.getTitle())
                .message(notification.getMessage())
                .candidateName(notification.getCandidatName())
                .candidatEmail(notification.getCandidatEmail())
                .applicationId(notification.getApplicationId())
                .type(notification.getType().name())
                .isRead(notification.isRead())
                .createdAt(notification.getCreatedAt())
                .readAt(notification.getReadAt())
                .build();
    }
}
