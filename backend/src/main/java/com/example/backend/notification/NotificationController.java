package com.example.backend.notification;

import com.example.backend.user.User;
import com.example.backend.user.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/v1/notifications")
public class NotificationController {
    private final NotificationService notificationService;
    private final UserRepository userRepository;

    public NotificationController(NotificationService notificationService, 
                                 UserRepository userRepository) {
        this.notificationService = notificationService;
        this.userRepository = userRepository;
    }

    /**
     * Récupérer les 10 dernières notifications du manager connecté
     * GET /api/v1/notifications/recent
     */
    @GetMapping("/recent")
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<List<NotificationDTO>> getRecentNotifications() {
        User manager = getManagerFromSecurityContext();
        if (manager == null) {
            return ResponseEntity.status(401).build();
        }
        List<NotificationDTO> notifications = notificationService.getRecentNotifications(manager);
        return ResponseEntity.ok(notifications);
    }

    /**
     * Récupérer toutes les notifications du manager connecté
     * GET /api/v1/notifications/all
     */
    @GetMapping("/all")
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<List<NotificationDTO>> getAllNotifications() {
        User manager = getManagerFromSecurityContext();
        if (manager == null) {
            return ResponseEntity.status(401).build();
        }
        List<NotificationDTO> notifications = notificationService.getAllNotifications(manager);
        return ResponseEntity.ok(notifications);
    }

    /**
     * Récupérer les notifications non lues du manager connecté
     * GET /api/v1/notifications/unread
     */
    @GetMapping("/unread")
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<List<NotificationDTO>> getUnreadNotifications() {
        User manager = getManagerFromSecurityContext();
        if (manager == null) {
            return ResponseEntity.status(401).build();
        }
        List<NotificationDTO> notifications = notificationService.getUnreadNotifications(manager);
        return ResponseEntity.ok(notifications);
    }

    /**
     * Compter les notifications non lues du manager connecté
     * GET /api/v1/notifications/unread-count
     */
    @GetMapping("/unread-count")
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<Long> getUnreadCount() {
        User manager = getManagerFromSecurityContext();
        if (manager == null) {
            return ResponseEntity.status(401).build();
        }
        Long count = notificationService.countUnreadNotifications(manager);
        return ResponseEntity.ok(count);
    }

    /**
     * Marquer une notification comme lue
     * PUT /api/v1/notifications/{id}/read
     */
    @PutMapping("/{id}/read")
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<Void> markAsRead(@PathVariable Long id) {
        User manager = getManagerFromSecurityContext();
        if (manager == null) {
            return ResponseEntity.status(401).build();
        }
        notificationService.markAsRead(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Marquer toutes les notifications comme lues
     * PUT /api/v1/notifications/mark-all-read
     */
    @PutMapping("/mark-all-read")
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<Void> markAllAsRead() {
        User manager = getManagerFromSecurityContext();
        if (manager == null) {
            return ResponseEntity.status(401).build();
        }
        notificationService.markAllAsRead(manager);
        return ResponseEntity.noContent().build();
    }

    /**
     * Supprimer une notification
     * DELETE /api/v1/notifications/{id}
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<Void> deleteNotification(@PathVariable Long id) {
        User manager = getManagerFromSecurityContext();
        if (manager == null) {
            return ResponseEntity.status(401).build();
        }
        notificationService.deleteNotification(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Supprimer toutes les notifications
     * DELETE /api/v1/notifications/all
     */
    @DeleteMapping("/all")
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<Void> deleteAllNotifications() {
        User manager = getManagerFromSecurityContext();
        if (manager == null) {
            return ResponseEntity.status(401).build();
        }
        notificationService.deleteAllNotifications(manager);
        return ResponseEntity.noContent().build();
    }

    /**
     * Extraire le manager du contexte de sécurité
     */
    private User getManagerFromSecurityContext() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && authentication.isAuthenticated()) {
                String email = authentication.getName();
                return userRepository.findByEmail(email).orElse(null);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    // 🆕 ============= ENDPOINTS POUR LES CANDIDATS =============

    /**
     * Récupérer les 10 dernières notifications du candidat connecté
     * GET /api/v1/notifications/candidate/recent
     */
    @GetMapping("/candidate/recent")
    public ResponseEntity<List<NotificationDTO>> getRecentNotificationsForCandidate() {
        User candidate = getUserFromSecurityContext();
        if (candidate == null) {
            return ResponseEntity.status(401).build();
        }
        List<NotificationDTO> notifications = notificationService.getRecentNotificationsForCandidate(candidate);
        return ResponseEntity.ok(notifications);
    }

    /**
     * Récupérer toutes les notifications du candidat connecté
     * GET /api/v1/notifications/candidate/all
     */
    @GetMapping("/candidate/all")
    public ResponseEntity<List<NotificationDTO>> getAllNotificationsForCandidate() {
        User candidate = getUserFromSecurityContext();
        if (candidate == null) {
            return ResponseEntity.status(401).build();
        }
        List<NotificationDTO> notifications = notificationService.getAllNotificationsForCandidate(candidate);
        return ResponseEntity.ok(notifications);
    }

    /**
     * Récupérer les notifications non lues du candidat connecté
     * GET /api/v1/notifications/candidate/unread
     */
    @GetMapping("/candidate/unread")
    public ResponseEntity<List<NotificationDTO>> getUnreadNotificationsForCandidate() {
        User candidate = getUserFromSecurityContext();
        if (candidate == null) {
            return ResponseEntity.status(401).build();
        }
        List<NotificationDTO> notifications = notificationService.getUnreadNotificationsForCandidate(candidate);
        return ResponseEntity.ok(notifications);
    }

    /**
     * Compter les notifications non lues du candidat connecté
     * GET /api/v1/notifications/candidate/unread-count
     */
    @GetMapping("/candidate/unread-count")
    public ResponseEntity<Long> getUnreadCountForCandidate() {
        User candidate = getUserFromSecurityContext();
        if (candidate == null) {
            return ResponseEntity.status(401).build();
        }
        Long count = notificationService.countUnreadNotificationsForCandidate(candidate);
        return ResponseEntity.ok(count);
    }

    /**
     * 🆕 Marquer une notification comme lue (pour candidat)
     * PUT /api/v1/notifications/candidate/{id}/read
     */
    @PutMapping("/candidate/{id}/read")
    public ResponseEntity<Void> markCandidateNotificationAsRead(@PathVariable Long id) {
        User user = getUserFromSecurityContext();
        if (user == null) {
            return ResponseEntity.status(401).build();
        }
        notificationService.markAsRead(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * 🆕 Marquer toutes les notifications candidat comme lues
     * PUT /api/v1/notifications/candidate/mark-all-read
     */
    @PutMapping("/candidate/mark-all-read")
    public ResponseEntity<Void> markAllCandidateNotificationsAsRead() {
        User user = getUserFromSecurityContext();
        if (user == null) {
            return ResponseEntity.status(401).build();
        }
        notificationService.markAllAsReadForCandidate(user);
        return ResponseEntity.noContent().build();
    }

    /**
     * 🆕 Supprimer une notification (pour candidat)
     * DELETE /api/v1/notifications/candidate/{id}
     */
    @DeleteMapping("/candidate/{id}")
    public ResponseEntity<Void> deleteCandidateNotification(@PathVariable Long id) {
        User user = getUserFromSecurityContext();
        if (user == null) {
            return ResponseEntity.status(401).build();
        }
        notificationService.deleteNotification(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Extraire l'utilisateur (candidat) du contexte de sécurité
     */
    private User getUserFromSecurityContext() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && authentication.isAuthenticated()) {
                String email = authentication.getName();
                return userRepository.findByEmail(email).orElse(null);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
