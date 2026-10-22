package com.example.backend.notification;

import com.example.backend.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    // Notifications pour managers (expéditeurs)
    List<Notification> findByManagerOrderByCreatedAtDesc(User manager);
    
    List<Notification> findByManagerAndReadFalseOrderByCreatedAtDesc(User manager);
    
    @Query("SELECT COUNT(n) FROM Notification n WHERE n.manager = ?1 AND n.read = false")
    Long countUnreadByManager(User manager);
    
    @Query("SELECT n FROM Notification n WHERE n.manager = ?1 ORDER BY n.createdAt DESC LIMIT 10")
    List<Notification> findRecentNotifications(User manager);
    
    // 🆕 Notifications pour candidats (destinataires)
    List<Notification> findByRecipientUserOrderByCreatedAtDesc(User recipientUser);
    
    List<Notification> findByRecipientUserAndReadFalseOrderByCreatedAtDesc(User recipientUser);
    
    @Query("SELECT COUNT(n) FROM Notification n WHERE n.recipientUser = ?1 AND n.read = false")
    Long countUnreadByCandidateUser(User recipientUser);
    
    @Query("SELECT n FROM Notification n WHERE n.recipientUser = ?1 ORDER BY n.createdAt DESC LIMIT 10")
    List<Notification> findRecentCandidateNotifications(User recipientUser);
}
